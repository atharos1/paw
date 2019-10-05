package ar.edu.itba.paw.interfaces;

import ar.edu.itba.paw.model.*;

import java.util.List;
import java.util.Map;

public interface SeriesDao {

    List<Series> searchSeries(String seriesName,String genreName,String networkName,int minRating,int maxRating);
    List<Series> getSeriesByName(String seriesName);

    List<Series> getSeriesByGenre(String genreName);
    List<Series> getSeriesByGenre(int id);

    List<Series> getBestSeriesByGenre(int genreId, int lowerLimit, int upperLimit);

    List<Genre> getAllGenres();

    List<Series> getNewSeries(int lowerLimit, int upperLimit);

    Map<Genre, List<Series>> getBestSeriesByGenres(int lowerLimit, int upperLimit);

    Series getSeriesById(long id, long userId);

    long createSeries(Integer tvdbid, String seriesName, String seriesDescription, Double userRating, String status, Integer runtime,
                      Integer networkId, String firstAired, String idImdb, String added, String updated, String posterUrl, String bannerUrl, Integer followers);

    long addSeriesGenre(String genre,List<Series> series);
    void setSeriesRunningTime(long seriesId, int runningTime);
    void setSeriesNetwork(long seriesId, int networkId);
    void setSeriesDescription(long seriesId, String seriesDescription);

    List<Season> getSeasonsBySeriesId(long seriesId);

    List<Episode> getEpisodesBySeasonId(long seasonId, long userId);
    List<Comment> getSeriesCommentsById(long seriesId);

    List<Series> getNextToBeSeen(long userId);

    List<Series> getRecentlyWatched(long userId, int number);

    int followSeries(long seriesId, long userId);
    int setViewedEpisode(long episodeId,long userId);
    int setViewedSeason(long seasonId,long userId);
    int unviewSeason(long seasonId,long userId);
    int unviewEpisode(long userId, long episodeId);

    int addSeriesReview(String body, long seriesId, long userId);

    int likePost(long userId, long postId);

    int unlikePost(long userId, long postId);

    int addCommentToPost(long commentPostId, String commentBody, long commentUserId);

    int likeComment(long userId, long commentId);

    int unlikeComment(long userId, long commentId);

    int removeComment(long commentId);

    int removePost(long postId);

    int rateSeries(long seriesId,long userId,double rating);
}
