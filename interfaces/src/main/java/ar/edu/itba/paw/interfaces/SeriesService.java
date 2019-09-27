package ar.edu.itba.paw.interfaces;

import ar.edu.itba.paw.model.Genre;
import ar.edu.itba.paw.model.Season;
import ar.edu.itba.paw.model.Series;

import java.util.List;
import java.util.Map;

public interface SeriesService {

    List<Series> getSeriesByName(String name);

    Series getSerieById(long id, long userId);

    List<Series> getSeriesByGenreAndNumber(int genreId, int num);
    List<Series> getAllSeriesByGenre(String genreName);
    public List<Series> getAllSeriesByGenre(int id);
    Map<Genre, List<Series>> getSeriesByGenreMap(int lowerNumber, int upperNumber);
    List<Series> getNewestSeries(int lowerNumber, int upperNumber);
    List<Season> getSeasonsBySeriesId(long seriesId);

}
