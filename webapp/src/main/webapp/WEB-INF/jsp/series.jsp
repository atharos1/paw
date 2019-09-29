<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <meta name="viewport" content="width=1040">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <meta name="MobileOptimized" content="1040">
    <meta http-equiv="content-language" content="en">

    <meta name="title" content="TUTV - Watch and track TV shows online">
    <title>TUTV - Watch and track TV shows online</title>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/resources/img/shortcuticon.png"/>">

    <!-- Bootstrap core CSS -->
    <link href="<c:url value="/resources/css/bootstrap.css"/>" rel="stylesheet">
    <link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/resources/css/tvst.css"/>">
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-rwoIResjU2yc3z8GV/NPeZWAv56rSmLldC3R/AZzGRnGxQQKnKkoFVhFQhNUwEyJ" crossorigin="anonymous">
    <link rel="stylesheet" href="<c:url value="/resources/css/style.css"/>">

    <script>document.getElementsByTagName("html")[0].className += " js";</script>
    <script src="<c:url value="/resources/js/jquery.min.js"/>"></script>
    <script src="<c:url value="/resources/js/popper.min.js"/>"></script>
    <script src="<c:url value="/resources/js/bootstrap.js"/>"></script>
    <script src="<c:url value="/resources/js/util.js"/>"></script>
    <script src="<c:url value="/resources/js/main.js"/>"></script>
    <script src="<c:url value="/resources/js/navigator.js"/>"></script>
</head>
<body id="container" class="home no-touch white   reduced-right ">
<div class="body-inner">
    <div class="page-left page-sidebar page-column ">
        <div class="scrollable scrolling-element">
            <%@ include file="sideMenu.jsp" %>
        </div>
    </div>
    <div class="page-center page-column ">
        <div class="page-center-inner">
            <div class="main-block">
                <div id="explore">
                    <section id="new-shows">
                        <h1>${series.name}</h1>
                        <div id="myCarousel" class="carousel slide" data-ride="carousel">
                            <div class="carousel-inner">
                                <div class="carousel-item active">
                                    <img src="${series.bannerUrl}" itemprop="image" alt="${series.bannerUrl}">
                                    <div class="carousel-caption">
                                        <div class="text-center">
                                            <span class="star"></span>
                                            <h2>${series.userRating}/5</h2>
                                        </div>
                                        <%--                                        <div class="container h-20">--%>
                                        <%--                                            <div class="starrating risingstar d-flex justify-content-center flex-row-reverse">--%>
                                        <%--                                                <input type="radio" id="star5" name="rating" value="5" /><label for="star5" title="5 star"></label>--%>
                                        <%--                                                <input type="radio" id="star4" name="rating" value="4" /><label for="star4" title="4 star"></label>--%>
                                        <%--                                                <input type="radio" id="star3" name="rating" value="3" /><label for="star3" title="3 star"></label>--%>
                                        <%--                                                <input type="radio" id="star2" name="rating" value="2" /><label for="star2" title="2 star"></label>--%>
                                        <%--                                                <input type="radio" id="star1" name="rating" value="1" /><label for="star1" title="1 star"></label>--%>
                                        <%--                                            </div>--%>
                                        <%--                                        </div>--%>
                                        <form action="<c:url value="/addSeries?seriesId=${series.id}&userId=${user.id}"/>"
                                              method="post">
                                            <button class="add-button" type="submit">Add</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                    <div class="main-block-container">
                        <div id="show-details" class="show">
                            <div class="row show-nav">
                                <div class="col-lg-7">
                                    <div class="basic-infos">
                                        <span>${series.network}</span>
                                        <span class="separator">•</span>
                                        <span>${fn:length(series.seasons)} <spring:message code="series.season"/><c:if
                                                test="${fn:length(series.seasons) gt 1}">s</c:if></span>
                                    </div>
                                    <div class="overview">
                                        ${series.seriesDescription}
                                    </div>
                                    <div class="followers">
                                        ${series.numFollowers} <spring:message code="index.followers"/>
                                    </div>
                                </div>
                                <div class="col-lg-1"></div>
                                <div class="col-lg-4"></div>
                            </div>
                            <div id="content" class="show-main alt">
                                <div class="content-container">
                                    <div class="left">
                                        <div class="w-100 no-padding no-margin">
                                            <ul class="cd-accordion cd-accordion--animated">
                                                <c:forEach items="${series.seasons}" var="season" varStatus="item">
                                                    <li class="cd-accordion__item cd-accordion__item--has-children">
                                                        <input class="cd-accordion__input" type="checkbox"
                                                               name="group-${item.index}" id="group-${item.index}">
                                                        <label class="cd-accordion__label cd-accordion__label--icon-folder drop"
                                                               for="group-${item.index}"><span
                                                                class="big-size">Season ${season.seasonNumber}</span></label>

                                                        <ul class="cd-accordion__sub cd-accordion__sub--l1">
                                                            <c:forEach items="${season.episodeList}" var="episode">
                                                                <li class="cd-accordion__label cd-accordion__label--icon-img">
                                                                    <div class="cd-accordion__item">
                                                                        <h3>${episode.episodeNumber}
                                                                            - ${episode.name}</h3>
                                                                        <c:if test="${not empty user}">
                                                                            <c:choose>
                                                                                <c:when test="${episode.viewed}">
                                                                                    <span style="font-family: FontAwesome,serif; font-style: normal"
                                                                                          class="check viewed">&#xf058</span>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <form action="<c:url value="/viewEpisode?seriesId=${series.id}&episodeId=${episode.id}&userId=${user.id}"/>"
                                                                                          method="post">
                                                                                        <button type="submit"
                                                                                                style="font-family: FontAwesome,serif; font-style: normal"
                                                                                                class="check">&#xf058
                                                                                        </button>
                                                                                    </form>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </c:if>
                                                                    </div>
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                        <div id="discussion">
                                            <h2 id="community-title" class="title"><spring:message code="series.discussion"/></h2>
                                            <div id="show-comments">
                                                <div class="comments">
                                                    <div class="new-comment comment">
                                                        <div class="disclaimer">
                                                            <p class="disclaimer-title"><spring:message code="series.spoil"/></p>
                                                        </div>
                                                        <form:form class="post" modelAttribute="postForm" action="/post"
                                                                   method="post"
                                                                   enctype="application/x-www-form-urlencoded">
                                                            <div class="top">
                                                                <form:errors path="body" element="p" cssClass="error text-left"/>
                                                                <div class="holder mode-comment mode">
                                                                    <div class="comment-mode mode">
                                                                        <div class="textarea-wrapper">
                                                                            <div class="mentions-input-box">
                                                                                <spring:message code="series.enterComment" var="placeholder"/>
                                                                                <form:textarea
                                                                                        placeholder="${placeholder}"
                                                                                        path="body"
                                                                                        style="overflow: hidden; height: 40px;"/>
                                                                                <form:input type="hidden" path="userId"
                                                                                            value="${user.id}"/>
                                                                                <form:input type="hidden"
                                                                                            path="seriesId"
                                                                                            value="${series.id}"/>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="author">
                                                                    <img class="author-picture img-circle"
                                                                         src="https://d1zfszn0v5ya99.cloudfront.net/user/15629037/profile_picture/5d0d499d718d5_square.png"
                                                                         alt="${user.userName}">
                                                                </div>
                                                            </div>
                                                            <div class="submit-comment">
                                                                <button type="submit" class="submit-comment-btn">
                                                                    <spring:message code="series.post"/></button>
                                                            </div>
                                                        </form:form>
                                                    </div>
                                                    <div class="comments-list">
                                                        <c:forEach var="post" items="${series.postList}">
                                                            <div class="comment clearfix extended">
                                                                <article class="post">
                                                                    <div class="top">
<%--                                                                        TODO remove for admin--%>
<%--                                                                        <div class="remove-zone">--%>
<%--                                                                            <a class="report-btn popover-link" href="#"--%>
<%--                                                                               data-toggle="tooltip" data-placement="right"--%>
<%--                                                                               title="" rel="popover"--%>
<%--                                                                               data-original-title="Report"><i--%>
<%--                                                                                    class="icon-tvst-flag"></i></a>--%>
<%--                                                                        </div>--%>
                                                                        <div class="holder">
                                                                            <div class="author-label mb-3">
                                                                                <span>${post.user.userName}</span>
                                                                                <div class="float-right mr-5">
                                                                                    <c:choose>
                                                                                        <c:when test="${post.liked}">
                                                                                            <span class="post-liked" style="font-family: FontAwesome,serif; font-style: normal">&#xf004</span>
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                            <span style="font-family: FontAwesome,serif; font-style: normal">&#xf004</span>
                                                                                        </c:otherwise>
                                                                                    </c:choose>
                                                                                    <span>${post.points}</span>
                                                                                </div>
                                                                            </div>
                                                                            <blockquote class="original">
                                                                                <p>${post.body}</p>
                                                                            </blockquote>
                                                                        </div>
                                                                    </div>
                                                                </article>
                                                                <div class="replies sub-comment">
                                                                    <c:forEach var="comment" items="${post.comments}">
                                                                        <article class="reply clearfix initialized">
                                                                            <div class="holder">
                                                                                <div class="author-label">
                                                                                    <span>${comment.user.userName}</span>
                                                                                    <div class="float-right">
                                                                                        <c:choose>
                                                                                            <c:when test="${comment.liked}">
                                                                                                <span class="post-liked" style="font-family: FontAwesome,serif; font-style: normal">&#xf004</span>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <span style="font-family: FontAwesome,serif; font-style: normal">&#xf004</span>
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                        <span>${comment.points}</span>
                                                                                    </div>
                                                                                </div>
                                                                                <blockquote>
                                                                                    <p>${comment.body}</p>
                                                                                </blockquote>
                                                                            </div>
                                                                        </article>
                                                                    </c:forEach>
                                                                    <form class="reply clearfix">
                                                                        <div class="holder">
                                                                            <div class="textarea-wrapper">
                                                                                <div class="mentions-input-box">
                                                                                <textarea rows="1"
                                                                                          placeholder="Enter your reply here"
                                                                                          style="overflow: hidden; height: 50px;"></textarea>
                                                                                </div>
                                                                            </div>
                                                                            <div class="clearfix"></div>
                                                                        </div>
                                                                    </form>
                                                                </div>
                                                            </div>
                                                        </c:forEach>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>