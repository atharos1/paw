package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.SeriesService;
import ar.edu.itba.paw.model.exceptions.NotFoundException;
import ar.edu.itba.paw.model.exceptions.UnauthorizedException;
import ar.edu.itba.paw.webapp.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;

@Controller
public class ViewsController {

	@Autowired
	private SeriesService seriesService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		final ModelAndView mav = new ModelAndView("index");
		mav.addObject("newShows", seriesService.getNewestSeries(0,4));
		mav.addObject("seriesMap", seriesService.getSeriesByGenreMap(0,7));
		return mav;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search() {
		final ModelAndView mav = new ModelAndView("search");
		mav.addObject("genres",seriesService.getAllGenres());
        mav.addObject("networks",seriesService.getAllNetworks());
		return mav;
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView search(@Valid @ModelAttribute("searchForm") final SearchForm form, final BindingResult errors) {
	    if(errors.hasErrors()){
	        return search();
        }
        final ModelAndView mav = new ModelAndView("searchResults");
        mav.addObject("searchResults",seriesService.searchSeries(form.getName(),form.getGenre(),form.getNetwork()));
        return mav;
    }

    @RequestMapping(value = "/upcoming", method = RequestMethod.GET)
    public ModelAndView upcoming() throws UnauthorizedException {
        ModelAndView mav = new ModelAndView("upcoming");
        mav.addObject("upcoming", seriesService.getUpcomingEpisodes());
        return mav;
    }

    @RequestMapping(value = "/watchlist", method = RequestMethod.GET)
    public ModelAndView watchlist() throws UnauthorizedException, NotFoundException {
        ModelAndView mav = new ModelAndView("watchlist");
        mav.addObject("watchlist", seriesService.getWatchList());
        return mav;
    }
}
