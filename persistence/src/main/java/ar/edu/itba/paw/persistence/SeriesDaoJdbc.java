package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.SeriesDao;
import ar.edu.itba.paw.interfaces.UserDao;
import ar.edu.itba.paw.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SeriesDaoJdbc implements SeriesDao {

    private RowMapper<Series> seriesRowMapper = (resultSet, i) -> {
        //Creo la serie.
        Series ret = new Series(resultSet.getString("name"));
        String description = resultSet.getString("description");
        if (description != null) {
            ret.setDescription(description);
        }
        ret.setId(resultSet.getLong("id"));
        Double rating = resultSet.getDouble("userRating");
        Double rounded = Math.round(rating * 10.0) / 10.0;
        ret.setTotalRating(rounded);
        ret.setImdbId(resultSet.getString("id_imdb"));
        ret.setRunningTime(resultSet.getInt("runtime"));
        ret.setNumFollowers(resultSet.getInt("followers"));
        ret.setBannerUrl(resultSet.getString("bannerurl"));
        ret.setPosterUrl(resultSet.getString("posterurl"));
        ret.setStatus(resultSet.getString("status"));
        ret.setNetwork(resultSet.getString("networkname"));
        //Seteo las fechas.
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String added = resultSet.getString("added");
        if (added != null) {
            try {
                ret.setAdded(format.parse(added));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String updated = resultSet.getString("updated");
        if (updated != null) {
            try {
                ret.setUpdated(format.parse(updated));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String firstAired = resultSet.getString("firstAired");
        if (firstAired != null) {
            try {
                ret.setFirstAired(format.parse(firstAired));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //Creo el genero.
        if (resultSet.getString("genre") != null) {
            Genre g = new Genre();
            g.setName(resultSet.getString("genre"));
            g.setId(resultSet.getInt("genreid"));
            ret.addGenre(g);
        }
        return ret;
    };

    private JdbcTemplate seriesJdbcTemplate;
    private SimpleJdbcInsert seriesjdbcInsert;
    private SimpleJdbcInsert genresjdbcInsert;
    private SimpleJdbcInsert hasGenrejdbcInsert;
    private SimpleJdbcInsert followsjdbcInsert;
    private SimpleJdbcInsert viewedEpisodesjdbcInsert;
    private SimpleJdbcInsert seriesReviewJdbcInsert;
    private SimpleJdbcInsert hasLikedSeriesReviewJdbcInsert;
    private SimpleJdbcInsert seriesReviewCommentsJdbcInsert;
    private SimpleJdbcInsert hasLikedSeriesReviewCommentsJdbcInsert;
    private SimpleJdbcInsert userSeriesRatingJdbcInsert;

    @Autowired
    private UserDao userDao;

    private Series getById(List<Series> series, long id) {
        for (Series s : series) {
            if (s.getId() == id)
                return s;
        }
        return null;
    }

    private List<Series> groupGenres(List<Series> seriesList) {
        List<Series> retList = new ArrayList<>();
        Series s;
        for (Series series : seriesList) {
            s = getById(retList, series.getId());
            if (s != null) {
                s.addGenres(series.getGenres());
            } else {
                retList.add(series);
            }
        }
        return retList;
    }

    @Autowired
    public SeriesDaoJdbc(final DataSource ds) {
        seriesJdbcTemplate = new JdbcTemplate(ds);
        seriesjdbcInsert = new SimpleJdbcInsert(seriesJdbcTemplate)
                .withTableName("series")
                .usingGeneratedKeyColumns("id");
        genresjdbcInsert = new SimpleJdbcInsert(seriesJdbcTemplate)
                .withTableName("genres")
                .usingGeneratedKeyColumns("id");
        hasGenrejdbcInsert = new SimpleJdbcInsert(seriesJdbcTemplate)
                .withTableName("hasgenre");
        followsjdbcInsert = new SimpleJdbcInsert(seriesJdbcTemplate)
                .withTableName("follows");
        viewedEpisodesjdbcInsert = new SimpleJdbcInsert(seriesJdbcTemplate)
                .withTableName("hasviewedepisode")
                .usingColumns("userid", "episodeid")
                .usingGeneratedKeyColumns("id");
        seriesReviewJdbcInsert = new SimpleJdbcInsert(seriesJdbcTemplate)
                .withTableName("seriesreview")
                .usingColumns("userid", "seriesid", "body");
        hasLikedSeriesReviewJdbcInsert = new SimpleJdbcInsert(seriesJdbcTemplate)
                .withTableName("haslikedseriesreview")
                .usingColumns("userid", "seriesreview");
        seriesReviewCommentsJdbcInsert = new SimpleJdbcInsert(seriesJdbcTemplate)
                .withTableName("seriesreviewcomments")
                .usingColumns("body", "userid", "postid");
        hasLikedSeriesReviewCommentsJdbcInsert = new SimpleJdbcInsert(seriesJdbcTemplate)
                .withTableName("haslikedseriesreviewcomment")
                .usingColumns("seriesreviewcomment", "userid");
        userSeriesRatingJdbcInsert = new SimpleJdbcInsert(seriesJdbcTemplate)
                .withTableName("userseriesrating");
    }

    @Override
    public List<Series> searchSeries(String seriesName, String genreName, String networkName) {
        return groupGenres(seriesJdbcTemplate.query("SELECT * " +
                        "FROM (series LEFT JOIN hasgenre ON series.id = hasgenre.seriesid LEFT JOIN genres ON genres.id = hasgenre.genreid LEFT JOIN network ON network.networkid = series.networkid) " +
                        "AS foo(id,tvdbid, name, description, userRating, status, runtime, networkid, firstaired, id_imdb, added, updated, posterurl, followers, bannerurl, seriesid, genreid, genreid1, genre, networkid1, networkname) " +
                        "WHERE LOWER(name) LIKE ? AND LOWER(genre) LIKE ? AND LOWER(networkname) LIKE ?",
                new Object[]{"%" + seriesName.toLowerCase() + "%", "%" + genreName.toLowerCase() + "%", "%" + networkName.toLowerCase() + "%"}, seriesRowMapper));
    }

    @Override
    public List<Series> getSeriesByName(String seriesName) {
        return groupGenres(seriesJdbcTemplate.query("SELECT * " +
                "FROM (series LEFT JOIN hasgenre ON series.id = hasgenre.seriesid LEFT JOIN genres ON genres.id = hasgenre.genreid LEFT JOIN network ON network.networkid = series.networkid) " +
                "AS foo(id,tvdbid, name, description, userRating, status, runtime, networkid, firstaired, id_imdb, added, updated, posterurl, followers, bannerurl, seriesid, genreid, genreid1, genre, networkid1, networkname) " +
                "WHERE LOWER(name) LIKE ?", new Object[]{"%" + seriesName.toLowerCase() + "%"}, seriesRowMapper));
    }

    @Override
    public List<Series> getSeriesByGenre(String genreName) {
        return groupGenres(seriesJdbcTemplate.query("SELECT * " +
                "FROM (series JOIN hasGenre ON hasgenre.seriesid = series.id JOIN genres ON hasgenre.genreid = genres.id LEFT JOIN network ON network.networkid = series.networkid) " +
                "AS foo(id, tvdbid,name, description, userRating, status, runtime, networkid, firstaired, id_imdb, added, updated, posterurl, followers, bannerurl, seriesid, genreid, genreid1, genre, networkid1, networkname)" +
                "WHERE LOWER(genre) LIKE ?", new Object[]{"%" + genreName.toLowerCase() + "%"}, seriesRowMapper));
    }

    @Override
    public List<Series> getSeriesByGenre(int id) {
        return groupGenres(seriesJdbcTemplate.query("SELECT * " +
                "FROM (series JOIN hasGenre ON hasgenre.seriesid = series.id JOIN genres ON hasgenre.genreid = genres.id LEFT JOIN network ON network.networkid = series.networkid) " +
                "AS foo(id, tvdbid,name, description, userRating, status, runtime, networkid, firstaired, id_imdb, added, updated, posterurl, followers, bannerurl, seriesid, genreid, genreid1, genre, networkid1, networkname)" +
                "WHERE genreid = ?", new Object[]{id}, seriesRowMapper));
    }

    @Override
    public List<Series> getBestSeriesByGenre(int genreId, int lowerLimit, int upperLimit) {
        return groupGenres(seriesJdbcTemplate.query("SELECT * " +
                        "FROM (series JOIN hasGenre ON hasgenre.seriesid = series.id JOIN genres ON hasgenre.genreid = genres.id LEFT JOIN network ON network.networkid = series.networkid) " +
                        "AS foo(id, tvdbid,name, description, userRating, status, runtime, networkid, firstaired, id_imdb, added, updated, posterurl, followers, bannerurl, seriesid, genreid, genreid1, genre, networkid1, networkname)" +
                        "WHERE genreid = ?" +
                        "ORDER BY userRating DESC LIMIT ? OFFSET ?",
                new Object[]{genreId, upperLimit - lowerLimit + 1, lowerLimit}, seriesRowMapper));
    }

    @Override
    public List<Series> getNewSeries(int lowerLimit, int upperLimit) {
        return groupGenres(seriesJdbcTemplate.query("SELECT * " +
                "FROM (series LEFT JOIN hasGenre ON hasgenre.seriesid = series.id LEFT JOIN genres ON hasgenre.genreid = genres.id LEFT JOIN network ON network.networkid = series.networkid) " +
                "AS foo(id, tvdbid,name, description, userRating, status, runtime, networkid, firstaired, id_imdb, added, updated, posterurl, followers, bannerurl, seriesid, genreid, genreid1, genre, networkid1, networkname) " +
                "WHERE bannerurl IS NOT NULL " +
                "ORDER BY firstaired DESC LIMIT ? OFFSET ?", new Object[]{upperLimit - lowerLimit + 1, lowerLimit}, seriesRowMapper));
    }

    @Override
    public Map<Genre, List<Series>> getBestSeriesByGenres(int lowerLimit, int upperLimit) {
        Map<Genre, List<Series>> ret = new HashMap<>();

        List<Genre> genreList = getAllGenres();
        List<Series> seriesList;
        for (Genre g : genreList) {
            seriesList = getBestSeriesByGenre(g.getId(), lowerLimit, upperLimit);
            ret.put(g, seriesList);
        }

        return ret;
    }

    @Override
    public List<Genre> getAllGenres() {
        return seriesJdbcTemplate.query("SELECT * " +
                        "FROM genres",
                (resultSet, i) -> {
                    Genre g = new Genre();
                    g.setName(resultSet.getString("genre"));
                    g.setId(resultSet.getInt("id"));
                    return g;
                });
    }

    @Override
    public List<String> getAllNetworks() {
        return seriesJdbcTemplate.query("SELECT name FROM network",(resultSet, i) -> {
            return resultSet.getString("name");
        });
    }

    @Override
    public Optional<Series> getSeriesById(final long id, final long userId) {
        final List<Series> seriesList = seriesJdbcTemplate.query("SELECT * " +
                "FROM (series LEFT JOIN hasGenre ON hasgenre.seriesid = series.id LEFT JOIN genres ON hasgenre.genreid = genres.id LEFT JOIN network ON network.networkid = series.networkid) " +
                "AS foo(id, tvdbid,name, description, userRating, status, runtime, networkid, firstaired, id_imdb, added, updated, posterurl, followers, bannerurl, seriesid, genreid, genreid1, genre, networkid1, networkname)" +
                "WHERE id = ?", new Object[]{id}, seriesRowMapper);
        if (seriesList.isEmpty()) {
            return Optional.empty();
        }
        Series series = groupGenres(seriesList).get(0);
        addAllSeasonsToSeries(series, userId);
        addAllPostsToSeries(series, userId);
        series.setFollows(userFollows(id, userId));
        series.setUserRating(getSeriesRating(id,userId));
        return Optional.of(series);
    }

    private Double getSeriesRating(long id, long userId) {
        List<Double> rating = seriesJdbcTemplate.query("SELECT rating " +
                "FROM userseriesrating " +
                "WHERE seriesid = ? AND userid = ?",
                new Object[]{id,userId},(resultSet,i)->resultSet.getDouble("rating"));
        return (rating.size() > 0) ? rating.get(0) : null;
    }

    private void addAllSeasonsToSeries(Series s, long userId) {
        List<Season> seasonList = getSeasonsBySeriesId(s.getId());
        for (Season season : seasonList) {
            season.setEpisodes(getEpisodesBySeasonId(season.getId(), userId));
            season.setSeasonAired(season.getEpisodeList().stream().anyMatch(episode -> episode.getAiring() != null
                    && Calendar.getInstance().getTime().compareTo(episode.getAiring()) >= 0));
        }
        setSeasonsViewed(seasonList);
        s.setSeasons(seasonList);
    }

    private void setSeasonsViewed(List<Season> seasonList) {
        int episodesViewed;
        for (Season season : seasonList) {
            episodesViewed = (int)season.getEpisodeList().stream().filter(episode -> episode.isViewed()).count();
            season.setViewed(season.getEpisodeList().size() == episodesViewed);
            season.setEpisodesViewed(episodesViewed);
        }
    }

    private void addAllPostsToSeries(Series series, long userId) {
        final List<Post> postList = seriesJdbcTemplate.query("SELECT seriesreview.id, seriesreview.body, seriesreview.numlikes, seriesreview.userid, exists(SELECT * FROM haslikedseriesreview WHERE userid = ? AND seriesreview = seriesreview.id) AS liked " +
                        "FROM seriesreview JOIN series ON seriesreview.seriesid = series.id " +
                        "WHERE series.id = ?", new Object[]{userId, series.getId()},
                (resultSet, i) -> {
                    Post post = new Post();
                    post.setBody(resultSet.getString("body"));
                    post.setPoints(resultSet.getInt("numlikes"));
                    post.setPostId(resultSet.getInt("id"));
                    post.setUserId(resultSet.getLong("userid"));
                    post.setLiked(resultSet.getBoolean("liked"));
                    return post;
                });
        addUserToPosts(postList);
        addAllCommentsToPosts(postList, userId);
        series.setSeriesPostList(postList);
    }

    private void addAllCommentsToPosts(List<Post> postList, long userId) {
        for (Post post : postList) {
            addAllCommentsToPost(post, userId);
        }
    }

    private void addAllCommentsToPost(Post post, long userId) {
        List<Comment> commentsList = seriesJdbcTemplate.query("SELECT seriesreviewcomments.*, " +
                        "exists(SELECT * FROM haslikedseriesreviewcomment WHERE haslikedseriesreviewcomment.seriesreviewcomment = seriesreviewcomments.id AND haslikedseriesreviewcomment.userid = ?) AS liked " +
                        "FROM seriesreviewcomments " +
                        "WHERE postid = ?", new Object[]{userId, post.getPostId()},
                (resultSet, i) -> {
                    Comment comment = new Comment();
                    comment.setCommentId(resultSet.getInt("id"));
                    comment.setBody(resultSet.getString("body"));
                    comment.setPoints(resultSet.getInt("numlikes"));
                    comment.setUserId(resultSet.getLong("userid"));
                    comment.setLiked(resultSet.getBoolean("liked"));
                    return comment;
                });
        addUsersToComments(commentsList);
        post.setComments(commentsList);
    }

    private void addUsersToComments(List<Comment> commentsList) {
        for(Comment comment : commentsList) {
            userDao.getUserById(comment.getUserId()).ifPresent(user -> {
                comment.setUser(user);
            });
        }
    }

    private void addUserToPosts(List<Post> postList) {
        for(Post post : postList) {
            userDao.getUserById(post.getUserId()).ifPresent(user -> {
                post.setUser(user);
            });
        }
    }

    @Override
    public long createSeries(Integer tvdbid, String seriesName, String seriesDescription, Double userRating, String status,
                             Integer runtime, Integer networkId, String firstAired, String idImdb, String added, String updated,
                             String posterUrl, String bannerUrl, Integer followers) {

        seriesjdbcInsert = seriesjdbcInsert.withTableName("series")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> args = new HashMap<>();
        args.put("tvdbid", tvdbid);
        args.put("name", seriesName);
        args.put("description", seriesDescription);
        args.put("userRating", userRating);
        args.put("status", status);
        args.put("runtime", runtime);
        args.put("networkId", networkId);
        args.put("firstAired", firstAired);
        args.put("id_imdb", idImdb);
        args.put("added", added);
        args.put("updated", updated);
        args.put("posterUrl", posterUrl);
        args.put("bannerUrl", bannerUrl);
        args.put("followers", followers);

        final Number seriesId = seriesjdbcInsert.executeAndReturnKey(args);
        return seriesId.longValue();
    }

    @Override
    public long addSeriesGenre(String genreName, List<Series> series) {

        genresjdbcInsert = genresjdbcInsert.withTableName("genres")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> args = new HashMap<>();
        args.put("genre", genreName);
        final Number genreId = genresjdbcInsert.executeAndReturnKey(args);
        if (series != null && series.size() > 0) {
            hasGenrejdbcInsert = hasGenrejdbcInsert.withTableName("hasgenre");
            args.clear();
            MapSqlParameterSource entry;
            List<MapSqlParameterSource> entries = new ArrayList<>();
            for (Series s : series) {
                entry = new MapSqlParameterSource()
                        .addValue("seriesid", s.getId())
                        .addValue("genreid", genreId);
                entries.add(entry);
            }
            hasGenrejdbcInsert.executeBatch(entries.toArray(new MapSqlParameterSource[entries.size()]));
        }
        return genreId.longValue();
    }

    @Override
    public void setSeriesRunningTime(long seriesId, int runningTime) {
        seriesJdbcTemplate.update("UPDATE series SET runtime = ? WHERE id = ?", runningTime, seriesId);
    }

    @Override
    public void setSeriesNetwork(long seriesId, int networkId) {
        seriesJdbcTemplate.update("UPDATE series SET networkId = ? WHERE id = ?", networkId, seriesId);
    }

    @Override
    public void setSeriesDescription(long seriesId, String description) {
        seriesJdbcTemplate.update("UPDATE series SET description = ? WHERE id = ?", description, seriesId);
    }

    @Override
    public List<Season> getSeasonsBySeriesId(long seriesId) {
        List<Season> seasonList = seriesJdbcTemplate.query("SELECT * " +
                "FROM series JOIN season ON series.id = season.seriesId " +
                "WHERE series.id = ? " +
                "ORDER BY seasonnumber", new Object[]{seriesId}, (resultSet, i) -> {
            Season s = new Season();
            s.setSeasonNumber(resultSet.getInt("seasonnumber"));
            s.setId(resultSet.getLong("seasonid"));
            return s;
        });
        return seasonList;
    }

    @Override
    public List<Episode> getEpisodesBySeasonId(long seasonId, long userId) {
        List<Episode> episodeList = seriesJdbcTemplate.query("SELECT episode.*, exists(SELECT * FROM hasviewedepisode WHERE userid = ? AND hasviewedepisode.episodeid = episode.id AND current_date >= episode.aired) as viewed " +
                "FROM episode  " +
                "WHERE episode.seasonid = ? " +
                "ORDER BY numepisode", new Object[]{userId, seasonId}, (resultSet, i) -> {
            Episode ret = new Episode();
            ret.setEpisodeNumber(resultSet.getInt("numepisode"));
            ret.setDescription(resultSet.getString("overview"));
            ret.setName(resultSet.getString("name"));
            ret.setViewed(resultSet.getBoolean("viewed"));
            ret.setId(resultSet.getLong("id"));
            ret.setAiring(resultSet.getDate("aired"));
            return ret;
        });
        return episodeList;
    }


    @Override
    public List<Series> getNextToBeSeen(long userId) {
        List<Series> toBeSeenSeriesList = seriesJdbcTemplate.query("SELECT episode.id AS episodeid,\n" +
                        "episode.name AS episodename,\n" +
                        "episode.numepisode AS episodenumber,\n" +
                        "(SELECT series.id FROM series WHERE series.id = episode.seriesid) AS seriesid,\n" +
                        "(SELECT series.name FROM series WHERE series.id = episode.seriesid) AS seriesname,\n" +
                        "(SELECT series.posterurl FROM series WHERE series.id = episode.seriesid) AS seriesposterurl,\n" +
                        "(SELECT season.seasonid FROM season WHERE season.seasonid = episode.seasonid) AS seasonid,\n" +
                        "(SELECT season.seasonnumber FROM season WHERE season.seasonid = episode.seasonid) AS seasonnumber\n" +
                        "FROM episode\n" +
                        "WHERE NOT EXISTS(SELECT * FROM hasviewedepisode WHERE hasviewedepisode.episodeid = episode.id AND hasviewedepisode.userid = ?)\n" +
                        "AND NOT EXISTS(SELECT e1.id FROM episode AS e1 WHERE NOT EXISTS(SELECT hasviewedepisode.episodeid\n" +
                        "FROM hasviewedepisode\n" +
                        "WHERE hasviewedepisode.episodeid = e1.id AND hasviewedepisode.userid = ?) AND e1.seriesid = episode.seriesid AND e1.aired < episode.aired)\n" +
                        "AND episode.seriesid IN (SELECT follows.seriesid FROM follows WHERE follows.userid = ?)\n" +
                        "AND current_date >= episode.aired",
                new Object[]{userId, userId, userId}, (resultSet, i) -> {
                    Series series = new Series();
                    series.setName(resultSet.getString("seriesname"));
                    series.setId(resultSet.getLong("seriesid"));
                    series.setPosterUrl(resultSet.getString("seriesposterurl"));

                    List<Season> auxSeasonList = new ArrayList<>(1);
                    Season season = new Season();
                    season.setId(resultSet.getLong("seasonid"));
                    season.setSeasonNumber(resultSet.getInt("seasonnumber"));

                    List<Episode> auxEpisodeList = new ArrayList<>(1);
                    Episode episode = new Episode();
                    episode.setId(resultSet.getLong("episodeid"));
                    episode.setName(resultSet.getString("episodename"));
                    episode.setEpisodeNumber(resultSet.getInt("episodenumber"));

                    auxEpisodeList.add(episode);
                    season.setEpisodeList(auxEpisodeList);
                    auxSeasonList.add(season);
                    series.setSeasons(auxSeasonList);

                    return series;
                });
        List<Series> retList = processToBeSeenList(toBeSeenSeriesList);
        return retList;
    }

    @Override
    public Optional<List<Series>> getRecentlyWatched(long userId, int number) {
        if(!userDao.userExists(userId)) return Optional.empty();
        List<Series> seriesList = seriesJdbcTemplate.query("SELECT DISTINCT seriesname, posterurl, seriesid FROM (SELECT id AS idhasviewed,\n" +
                "(SELECT name FROM series WHERE series.id = (SELECT DISTINCT episode.seriesid FROM episode WHERE episode.id = hasviewedepisode.episodeid)) AS seriesname,\n" +
                "(SELECT posterurl FROM series WHERE series.id = (SELECT DISTINCT episode.seriesid FROM episode WHERE episode.id = hasviewedepisode.episodeid)) AS posterurl,\n" +
                "(SELECT id FROM series WHERE series.id = (SELECT DISTINCT episode.seriesid FROM episode WHERE episode.id = hasviewedepisode.episodeid)) AS seriesid\n" +
                "FROM hasviewedepisode\n" +
                "WHERE userid = ?) AS foo(idhasviewed, seriesname, posterurl, seriesid)\n" +
                "LIMIT ?", new Object[]{userId, number}, (resultSet, i) -> {
            Series ret = new Series();
            ret.setId(resultSet.getLong("seriesid"));
            ret.setPosterUrl(resultSet.getString("posterurl"));
            ret.setName(resultSet.getString("seriesname"));
            return ret;
        });
        return Optional.of(seriesList);
    }

    @Override
    public Optional<List<Series>> getAddedSeries(long userId) {
        if(!userDao.userExists(userId)) return Optional.empty();
        List<Series> seriesList = seriesJdbcTemplate.query("SELECT * " +
                "FROM follows JOIN series ON follows.seriesid = series.id " +
                "WHERE userid = ?", new Object[]{userId}, (resultSet, i) -> {
            Series ret = new Series();
            ret.setName(resultSet.getString("name"));
            ret.setId(resultSet.getLong("seriesid"));
            ret.setBannerUrl(resultSet.getString("bannerUrl"));
            ret.setPosterUrl(resultSet.getString("posterUrl"));

            return ret;
        });
        return Optional.of(seriesList);
    }

    @Override
    public Optional<List<Series>> getUpcomingEpisodes(long userId) {
        Optional<List<Series>> seriesList = getAddedSeries(userId);
        seriesList.ifPresent(list -> {
            list.forEach(series -> {
                getUpcomingEpisode(series).ifPresent(seasons -> {
                    series.setSeasons(seasons);
                });
            });
        });
        seriesList = seriesList.map(list -> { return list.stream().filter( series -> !series.getSeasons().isEmpty() ).collect(Collectors.toList()); });
        return seriesList;
    }

    private Optional<List<Season>> getUpcomingEpisode(Series series) {
        List<Season> seasonsList = seriesJdbcTemplate.query("SELECT id AS episodeid,\n" +
                        "name AS episodename,\n" +
                        "numepisode AS episodenumber,\n" +
                        "aired as airing," +
                        "(SELECT season.seasonnumber FROM season WHERE season.seasonid = e1.seasonid) AS seasonnumber\n" +
                        "FROM episode AS e1\n" +
                        "WHERE e1.aired = (SELECT min(episode.aired) FROM episode WHERE current_date < episode.aired AND episode.seriesid = e1.seriesid)\n" +
                        "AND e1.seriesid = ?\n" +
                        "ORDER BY seasonnumber, episodenumber ASC", new Object[]{series.getId()},
                (resultSet, i) -> {
            Season season = new Season();
            season.setId(resultSet.getLong("episodeid"));
            season.setSeasonNumber(resultSet.getInt("seasonnumber"));

            List<Episode> episodeList = new ArrayList<>(1);
            Episode episode = new Episode();
            episode.setId(resultSet.getLong("episodeid"));
            episode.setName(resultSet.getString("episodename"));
            episode.setEpisodeNumber(resultSet.getInt("episodenumber"));
            episode.setAiring(resultSet.getDate("airing"));
            episodeList.add(episode);
            season.setEpisodeList(episodeList);
            return season;
        });

        if(seasonsList.isEmpty()) return Optional.empty();
        return Optional.of(seasonsList.subList(0, 1));

    }

    private List<Series> processToBeSeenList(List<Series> toBeSeenSeriesList) {
        List<Series> retList = new ArrayList<>();
        Set<Long> idSet = new HashSet<>();
        for(Series series1 : toBeSeenSeriesList) {
            if(!idSet.contains(series1.getId())) {
                Series oldestSeries = series1;
                idSet.add(series1.getId());
                for (Series series2 : toBeSeenSeriesList) {
                    if (oldestSeries.getId() == series2.getId()
                            && compareToBeSeenSeries(oldestSeries, series2) > 0) {
                        oldestSeries = series2;
                    }
                }
                retList.add(oldestSeries);
            }
        }
        return retList;
    }

    private int compareToBeSeenSeries(Series oldestSeries, Series series2) {
        if(oldestSeries.getSeasons().get(0).getSeasonNumber() < series2.getSeasons().get(0).getSeasonNumber()) {
            return -1;
        } else if(oldestSeries.getSeasons().get(0).getSeasonNumber() > series2.getSeasons().get(0).getSeasonNumber()) {
            return 1;
        }
        Season seriesSeason1 = oldestSeries.getSeasons().get(0);
        Season seriesSeason2 = series2.getSeasons().get(0);

        if(seriesSeason1.getEpisodeList().get(0).getEpisodeNumber() < seriesSeason2.getEpisodeList().get(0).getEpisodeNumber()) {
            return -1;
        } else if(seriesSeason1.getEpisodeList().get(0).getEpisodeNumber() > seriesSeason2.getEpisodeList().get(0).getEpisodeNumber()){
            return 1;
        }

        return 0;
    }

    @Override
    public int followSeries(long seriesId, long userId) {

        //Todo: ask if there is a utility method for this.
        if(userFollows(seriesId, userId)) return -1;

        Map<String, Object> args = new HashMap<>();
        args.put("userId",userId);
        args.put("seriesId", seriesId);

        int numRowsAffected = followsjdbcInsert.execute(args);
        if(numRowsAffected != 0) {
            seriesJdbcTemplate.update("UPDATE series SET followers = (followers + 1) WHERE id = ?", seriesId);
        }
        return numRowsAffected;
    }

    @Override
    public int unfollowSeries(long seriesId, long userId) {
        int rowsDeleted = seriesJdbcTemplate.update("DELETE FROM follows WHERE userid = ? AND seriesid = ?",userId,seriesId);
        if(rowsDeleted > 0){
            seriesJdbcTemplate.update("UPDATE series SET followers = (followers - 1) WHERE id = ?", seriesId);
        }
        return rowsDeleted;
    }
    @Override
    public boolean userFollows(long seriesId, long userId) {
        List<Integer> series = seriesJdbcTemplate.query("SELECT * " +
                        "FROM follows " +
                        "WHERE follows.userid = ? AND follows.seriesid = ?", new Object[]{userId, seriesId},
                (resultSet, i) -> resultSet.getInt("seriesid"));
        return series.size() > 0;
    }
    @Override
    public int setViewedEpisode(long episodeId, long userId) {
        List<Date> dateList = seriesJdbcTemplate.query("SELECT episode.aired AS airing\n" +
                "FROM episode\n" +
                "WHERE episode.id = ?", new Object[]{episodeId}, (resultSet, i) ->{
            return resultSet.getDate("airing");
        });
        if(dateList.isEmpty()) return 0;
        Date date = dateList.get(0);
        if(date != null && Calendar.getInstance().getTime().compareTo(date) < 0) return 0;
        Map<String, Object> args = new HashMap<>();
        args.put("userId",userId);
        args.put("episodeId", episodeId);
        int numRowsAffected = viewedEpisodesjdbcInsert.execute(args);
        return numRowsAffected;
    }

    @Override
    public int setViewedSeason(long seasonId, long userId) {
        List<Episode> seasonEpisodes = getEpisodesBySeasonId(seasonId,userId);
        int rows = 0;
        for(Episode episode : seasonEpisodes) {
            if(episode.getAiring() != null && Calendar.getInstance().getTime().compareTo(episode.getAiring()) >= 0) {
                rows += setViewedEpisode(episode.getId(),userId);
            }
        }
        return rows;
    }
    @Override
    public int unviewSeason(long seasonId, long userId) {
        return seriesJdbcTemplate.update("DELETE FROM hasviewedepisode " +
                "WHERE hasviewedepisode.userid = ? AND EXISTS(SELECT * FROM episode WHERE hasviewedepisode.episodeid = episode.id AND episode.seasonid = ?)",
                new Object[]{userId,seasonId});
    }
    @Override
    public int unviewEpisode(long userId, long episodeId) {
        int result = seriesJdbcTemplate.update("DELETE FROM hasviewedepisode WHERE hasviewedepisode.userid = ? AND hasviewedepisode.episodeid = ?", new Object[]{userId, episodeId});
        return result;
    }

    @Override
    public int addSeriesReview(String body, long seriesId, long userId) {
        Map<String, Object> args = new HashMap<>();
        args.put("userid", userId);
        args.put("seriesid", seriesId);
        args.put("body", body);
        int numRows = seriesReviewJdbcInsert.execute(args);
        return numRows;
    }

    @Override
    public int likePost(long userId, long postId) {
        if(hasLikedPost(userId, postId)) return -1;
        Map<String, Object> args = new HashMap<>();
        args.put("userid", userId);
        args.put("seriesreview", postId);
        int numRows = hasLikedSeriesReviewJdbcInsert.execute(args);
        if(numRows != 0) {
            addPointsToPost(postId, 1);
        }
        return numRows;
    }

    @Override
    public int unlikePost(long userId, long postId) {
        int numRows = seriesJdbcTemplate.update("DELETE FROM seriesreview WHERE userid = ? AND id = ?", new Object[]{userId, postId});
        if(numRows != 0) {
            addPointsToPost(postId, -1);
        }
        return numRows;
    }

    @Override
    public int addCommentToPost(long commentPostId, String commentBody, long commentUserId) {
        Map<String, Object> args = new HashMap<>();
        args.put("body", commentBody);
        args.put("postid", commentPostId);
        args.put("userid", commentUserId);
        int numRows = seriesReviewCommentsJdbcInsert.execute(args);
        return numRows;
    }

    @Override
    public int likeComment(long userId, long commentId) {
        if(likedComment(userId, commentId)) return -1;
        Map<String, Object> args = new HashMap<>();
        args.put("userid", userId);
        args.put("seriesreviewcomment", commentId);

        int numRows = hasLikedSeriesReviewCommentsJdbcInsert.execute(args);
        if(numRows != 0) {
            addPointsToComment(commentId, 1);
        }
        return numRows;
    }

    private boolean likedComment(long userId, long commentId) {
        Integer count = seriesJdbcTemplate.queryForObject("SELECT count(*) FROM haslikedseriesreviewcomment WHERE seriesreviewcomment = ? AND userid = ?",
                new Object[]{commentId,userId},
                Integer.class);
        return count > 0;
    }

    @Override
    public int unlikeComment(long userId, long commentId) {
        int numRows = seriesJdbcTemplate.update("DELETE FROM haslikedseriesreviewcomment WHERE seriesreviewcomment = ? AND userid = ?", new Object[]{commentId, userId});
        if(numRows != 0) {
            addPointsToComment(commentId, -1);
        }
        return numRows;
    }

    @Override
    public int removeComment(long commentId) {
        int numRows = seriesJdbcTemplate.update("DELETE FROM seriesreviewcomments WHERE id = ?", new Object[]{commentId});
        return numRows;
    }

    @Override
    public int removePost(long postId) {
        int numRows = seriesJdbcTemplate.update("DELETE FROM seriesreview WHERE id = ?", new Object[]{postId});
        return numRows;
    }

    @Override
    public int rateSeries(long seriesId, long userId, double rating) {
        Double oldRating = getUserSeriesRating(userId,seriesId);
        int numRows = 0;
        if(oldRating != null){
            numRows = seriesJdbcTemplate.update("UPDATE userseriesrating SET rating = ? WHERE userid = ? AND seriesid = ?",
                    new Object[]{rating,userId,seriesId});
        } else {
            Map<String, Object> args = new HashMap<>();
            args.put("userid", userId);
            args.put("seriesid", seriesId);
            args.put("rating",rating);
            numRows = userSeriesRatingJdbcInsert.execute(args);
        }
        if(numRows == 0) {
            return 0;
        }
        Double avg = seriesJdbcTemplate.queryForObject("SELECT avg(rating) FROM userseriesrating WHERE seriesid = ?",new Object[]{seriesId},Double.class);
        seriesJdbcTemplate.update("UPDATE series SET userRating = ?  WHERE id = ?",new Object[]{avg,seriesId});
        return numRows;
    }

    @Override
    public long getPostAuthorId(long postId) {
        List<Long> idList = seriesJdbcTemplate.query("SELECT userid FROM seriesreview WHERE seriesreview.id = ?", new Object[]{postId},
                (resultSet, i) -> {
                    return resultSet.getLong("userid");
                });
        if(idList.isEmpty()) return -1;
        return idList.get(0);
    }

    @Override
    public long getCommentAuthorId(long commentId) {
        List<Long> idList = seriesJdbcTemplate.query("SELECT userid FROM seriesreviewcomments WHERE id = ?", new Object[]{commentId},
                (resultSet, i) -> {
                    return resultSet.getLong("userid");
                });
        if(idList.isEmpty()) return -1;
        return idList.get(0);
    }

    private void addPointsToComment(long commentId, int points) {
        seriesJdbcTemplate.update("UPDATE seriesreviewcomments SET numlikes = numlikes + (?) WHERE id = ?", new Object[]{points, commentId});
    }

    private void addPointsToPost(long postId, int points) {
        seriesJdbcTemplate.update("UPDATE seriesreview SET numlikes = numlikes + (?) WHERE id = ?", new Object[]{points, postId});
    }

    private boolean hasLikedPost(long userId, long postId) {
        Integer count = seriesJdbcTemplate.queryForObject("SELECT count(*) FROM haslikedseriesreview " +
                "WHERE userid = ? AND seriesreview = ?", new Object[]{userId, postId},Integer.class);
        return count > 0;
    }

    private Double getUserSeriesRating(long userId,long seriesId) {
        List<Double> series = seriesJdbcTemplate.query("SELECT * " +
                        "FROM userseriesrating " +
                        "WHERE userid = ? AND seriesid = ?", new Object[]{userId, seriesId},
                (resultSet, i) -> resultSet.getDouble("rating"));
        return (series.size() > 0) ? series.get(0) : null;
    }

}
