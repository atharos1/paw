package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.SeriesService;
import ar.edu.itba.paw.interfaces.UserService;
import ar.edu.itba.paw.model.Series;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.BadRequestException;
import ar.edu.itba.paw.model.exceptions.NotFoundException;
import ar.edu.itba.paw.model.exceptions.UnauthorizedException;
import ar.edu.itba.paw.webapp.form.CommentForm;
import ar.edu.itba.paw.webapp.form.PostForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class SeriesController {
    @Autowired
    private UserService userService;

    @Autowired
    private SeriesService seriesService;

    @RequestMapping(value = "/series", method = RequestMethod.GET)
    public ModelAndView series(@ModelAttribute("postForm") final PostForm postForm, @ModelAttribute("commentForm") final CommentForm commentForm, @RequestParam("id") long id) throws NotFoundException {
        final ModelAndView mav = new ModelAndView("series");
        Series series = seriesService.getSerieById(id).orElseThrow(NotFoundException::new);
        mav.addObject("series", series);
        Optional<User> u = userService.getLoggedUser();
        u.ifPresent(user -> mav.addObject("hasAvatar", userService.getUserAvatar(user.getId()).isPresent()));
        mav.addObject("postForm", postForm);
        mav.addObject("commentForm", commentForm);
        return mav;
    }

    @RequestMapping(value = "/addSeries", method = RequestMethod.POST)
    public ModelAndView addSeries(@RequestParam("seriesId") long seriesId) throws NotFoundException, UnauthorizedException {
        seriesService.followSeries(seriesId);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }

    @RequestMapping(value = "/unfollowSeries", method = RequestMethod.POST)
    public ModelAndView unfollowSeries(@RequestParam("seriesId") long seriesId) throws NotFoundException, UnauthorizedException {
        seriesService.unfollowSeries(seriesId);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }

    @RequestMapping(value = "/viewSeason", method = RequestMethod.POST)
    public ModelAndView viewSeason(@RequestParam("seriesId") long seriesId, @RequestParam("seasonId") long seasonId) throws UnauthorizedException, NotFoundException {
        seriesService.setViewedSeason(seasonId);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }

    @RequestMapping(value = "/unviewSeason", method = RequestMethod.POST)
    public ModelAndView unviewSeason(@RequestParam("seriesId") long seriesId, @RequestParam("seasonId") long seasonId) throws UnauthorizedException, NotFoundException {
        seriesService.unviewSeason(seasonId);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }

    @RequestMapping(value = "/viewEpisode", method = RequestMethod.POST)
    public ModelAndView viewEpisode(@RequestParam("seriesId") long seriesId, @RequestParam("episodeId") long episodeId,
                                    HttpServletRequest request) throws NotFoundException, UnauthorizedException {
        seriesService.setViewedEpisode(episodeId);
        String referer = request.getHeader("Referer");
        return new ModelAndView("redirect:" + referer);
    }

    @RequestMapping(value = "/unviewEpisode", method = RequestMethod.POST)
    public ModelAndView unviewEpisode(@RequestParam("seriesId") long seriesId, @RequestParam("episodeId") long episodeId) throws NotFoundException, UnauthorizedException {
        seriesService.unviewEpisode(episodeId);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }

    @RequestMapping(value = "/rate")
    public ModelAndView rate(@RequestParam("seriesId") long seriesId, @RequestParam("rating") int rating) throws NotFoundException, UnauthorizedException, BadRequestException {
        seriesService.rateSeries(seriesId, rating);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ModelAndView post(@Valid @ModelAttribute("postForm") final PostForm form, final BindingResult errors) throws Exception {
        if (errors.hasErrors()) {
            return series(form, new CommentForm(), form.getSeriesId());
        }
        seriesService.addSeriesReview(form.getBody(), form.getSeriesId());
        return new ModelAndView("redirect:/series?id=" + form.getSeriesId());
    }

    @RequestMapping(value = "/likePost", method = RequestMethod.POST)
    public ModelAndView likePost(@RequestParam("seriesId") long seriesId, @RequestParam("postId") long postId) throws NotFoundException, UnauthorizedException {
        seriesService.likePost(postId);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }

    @RequestMapping(value = "/unlikePost", method = RequestMethod.POST)
    public ModelAndView unlikePost(@RequestParam("seriesId") long seriesId, @RequestParam("postId") long postId) throws NotFoundException, UnauthorizedException {
        seriesService.unlikePost(postId);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }

    @RequestMapping(value = "/removePost", method = RequestMethod.POST)
    public ModelAndView removePost(@RequestParam("seriesId") long seriesId, @RequestParam("postId") long postId) throws UnauthorizedException, NotFoundException {
        seriesService.removePost(postId);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public ModelAndView comment(@Valid @ModelAttribute("commentForm") final CommentForm form, final BindingResult errors) throws Exception {
        if (errors.hasErrors()) {
            return series(new PostForm(), form, form.getCommentSeriesId());
        }
        seriesService.addCommentToPost(form.getCommentPostId(), form.getCommentBody());
        return new ModelAndView("redirect:/series?id=" + form.getCommentSeriesId());
    }

    @RequestMapping(value = "/likeComment", method = RequestMethod.POST)
    public ModelAndView likeComment(@RequestParam("seriesId") long seriesId, @RequestParam("postId") long postId, @RequestParam("commentId") long commentId) throws NotFoundException, UnauthorizedException {
        seriesService.likeComment(commentId);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }

    @RequestMapping(value = "/unlikeComment", method = RequestMethod.POST)
    public ModelAndView unlikeComment(@RequestParam("seriesId") long seriesId, @RequestParam("postId") long postId, @RequestParam("commentId") long commentId) throws NotFoundException, UnauthorizedException {
        seriesService.unlikeComment(commentId);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }

    @RequestMapping(value = "/removeComment", method = RequestMethod.POST)
    public ModelAndView removeComment(@RequestParam("seriesId") long seriesId, @RequestParam("postId") long postId, @RequestParam("commentId") long commentId) throws UnauthorizedException, NotFoundException {
        seriesService.removeComment(commentId);
        return new ModelAndView("redirect:/series?id=" + seriesId);
    }
}
