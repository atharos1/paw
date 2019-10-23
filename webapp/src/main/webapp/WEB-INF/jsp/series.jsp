<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta charset="utf-8">
    <meta name="viewport" content="width=1040">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <meta name="MobileOptimized" content="1040">
    <meta http-equiv="content-language" content="en">

    <meta name="title" content="TUTV">
    <title>TUTV</title>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/resources/img/shortcuticon.png"/>">

    <!-- Bootstrap core CSS -->
    <link href="<c:url value="/resources/css/bootstrap.css"/>" rel="stylesheet">
    <link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/resources/css/tvst.css"/>">
    <link href="<c:url value="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css"/>" rel="stylesheet"
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
    <%@ include file="sideMenu.jsp" %>
    <div class="page-center page-column">
        <div class="page-center-inner">
            <div class="main-block">
                <div id="explore">
                    <section id="new-shows">
                        <h1>${series.name}</h1>
                        <div id="myCarousel" class="carousel slide" data-ride="carousel">
                            <div class="carousel-inner">
                                <div class="carousel-item active">
                                    <img src="https://image.tmdb.org/t/p/original${series.bannerUrl}" itemprop="image" alt="https://image.tmdb.org/t/p/original${series.bannerUrl}">
                                    <div class="carousel-caption">
                                        <div class="text-center">
                                            <span class="star"></span>
                                            <h2>${series.totalRating} / 5.0</h2>
                                        </div>
                                        <c:if test="${isLogged}">
                                            <c:choose>
                                                <c:when test="${series.follows}">
                                                    <form action="<c:url value="/unfollowSeries?seriesId=${series.id}"/>" method="post">
                                                        <button class="add-button" type="submit"><spring:message code="series.unfollow"/></button>
                                                    </form>
                                                </c:when>
                                                <c:otherwise>
                                                    <form action="<c:url value="/addSeries?seriesId=${series.id}"/>" method="post">
                                                        <button class="add-button" type="submit"><spring:message code="series.follow"/></button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                    <div class="main-block-container">
                        <div id="show-details" class="show">
                            <div class="row show-nav">
                                <div class="col-lg-8">
                                    <div class="basic-infos">
                                        <span>${series.network}</span>
                                        <span class="separator">•</span>
                                        <span>${fn:length(series.seasons)} <spring:message code="series.season"/><c:if
                                                test="${fn:length(series.seasons) ne 1}">s</c:if></span>
                                    </div>
                                    <div class="overview">
                                        ${series.seriesDescription}
                                    </div>
                                    <div class="followers">
                                        ${series.numFollowers} <spring:message code="index.followers"/><c:if
                                                test="${series.numFollowers ne 1}"><spring:message code="index.sufix"/></c:if>
                                    </div>
                                </div>
                                <c:if test="${isLogged}">
                                    <div class="col-lg-4">
                                        <div class="container h-20">
                                            <div class="starrating risingstar d-flex justify-content-center flex-row-reverse">
                                                <c:forEach var="index" begin="0" end="4">
                                                    <c:choose>
                                                        <c:when test="${series.userRating eq (5-index)}">
                                                            <input id="star${5-index}" name="rating" type="radio" checked value="${5-index}" onclick="window.location.href='<c:url value="/rate?seriesId=${series.id}&rating=${5-index}"/>'"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <input id="star${5-index}" name="rating" type="radio" value="${5-index}" onclick="window.location.href='<c:url value="/rate?seriesId=${series.id}&rating=${5-index}"/>'"/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <label for="star${5-index}" title="${5-index} <spring:message code="series.star"/><c:if test="${series.numFollowers ne 1}">s</c:if>"></label>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
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
                                                        <c:choose>
                                                            <c:when test="${isLogged && season.viewed}">
                                                                <label class="cd-accordion__label cd-accordion__label--icon-folder drop drop-watched" for="group-${item.index}">
                                                                    <span class="big-size"><spring:message code="series.Season"/> ${season.seasonNumber}</span>
                                                                    <span class="ml-3 viewed-episodes">${season.episodesViewed} / ${fn:length(season.episodeList)}</span>
                                                                    <form action="<c:url value="/unviewSeason?seriesId=${series.id}&seasonId=${season.id}"/>"
                                                                          method="post">
                                                                        <button type="submit"
                                                                                style="font-family: FontAwesome,serif; font-style: normal"
                                                                                class="check-season viewed">&#xf058
                                                                        </button>
                                                                    </form>
                                                                </label>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <label class="cd-accordion__label cd-accordion__label--icon-folder drop" for="group-${item.index}">
                                                                    <span class="big-size"><spring:message code="series.Season"/> ${season.seasonNumber}</span>
                                                                    <c:if test="${isLogged}">
                                                                        <span class="ml-3 viewed-episodes">${season.episodesViewed} / ${fn:length(season.episodeList)}</span>
                                                                        <c:if test="${season.seasonAired}">
                                                                            <form action="<c:url value="/viewSeason?seriesId=${series.id}&seasonId=${season.id}"/>"
                                                                                  method="post">
                                                                                <button type="submit"
                                                                                        style="font-family: FontAwesome,serif; font-style: normal"
                                                                                        class="check-season">&#xf058
                                                                                </button>
                                                                            </form>
                                                                        </c:if>
                                                                    </c:if>
                                                                </label>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <ul class="cd-accordion__sub cd-accordion__sub--l1">
                                                            <c:forEach items="${season.episodeList}" var="episode">
                                                                <li class="cd-accordion__label cd-accordion__label--icon-img">
                                                                    <div class="cd-accordion__item">
                                                                        <h3>${episode.episodeNumber}
                                                                            - ${episode.name}</h3>
                                                                        <span class="ml-3 episode-date"><fmt:formatDate value="${episode.airing}" type="date" dateStyle="short"/></span>
                                                                        <c:set var="today_date" value="<%=new java.util.Date()%>"/>
                                                                        <c:if test="${isLogged && (episode.airing lt today_date)}">
                                                                            <c:choose>
                                                                                <c:when test="${episode.viewed}">
                                                                                    <form action="<c:url value="/unviewEpisode?seriesId=${series.id}&episodeId=${episode.id}"/>"
                                                                                          method="post">
                                                                                        <button type="submit"
                                                                                                style="font-family: FontAwesome,serif; font-style: normal"
                                                                                                class="check viewed">&#xf058
                                                                                        </button>
                                                                                    </form>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <form action="<c:url value="/viewEpisode?seriesId=${series.id}&episodeId=${episode.id}"/>"
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
                                                    <c:if test="${isLogged && not user.isBanned}">
                                                        <div class="new-comment comment">
                                                            <div class="disclaimer">
                                                                <p class="disclaimer-title"><spring:message code="series.spoil"/></p>
                                                            </div>
                                                            <c:url value='post' var="actionPostValue"/>
                                                            <form:form class="post" modelAttribute="postForm" action="${actionPostValue}"
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
                                                                                    <form:input type="hidden"
                                                                                                path="seriesId"
                                                                                                value="${series.id}"/>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="author">
                                                                        <c:choose>
                                                                            <c:when test="${hasAvatar}">
                                                                                <img class="author-picture img-circle" src="<c:url value="/user/${user.id}/avatar"/>" alt="${user.userName}">
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <img class="author-picture img-circle" src="<c:url value="https://d36rlb2fgh8cjd.cloudfront.net/default-images/default-user-q80.png"/>" alt="${user.userName}">
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </div>
                                                                </div>
                                                                <div class="submit-comment">
                                                                    <button type="submit" class="submit-comment-btn">
                                                                        <spring:message code="series.post"/></button>
                                                                </div>
                                                            </form:form>
                                                        </div>
                                                    </c:if>
                                                    <div class="comments-list">
                                                        <c:if test="${not isLogged && empty series.postList}">
                                                            <spring:message code="series.noPosts"/>
                                                        </c:if>
                                                        <c:forEach var="post" items="${series.postList}">
                                                            <div class="comment clearfix extended">
                                                                <article class="post">
                                                                    <div class="top">
                                                                        <div class="holder">
                                                                            <div class="author-label mb-3">
                                                                                <c:choose>
                                                                                    <c:when test="${isLogged}">
                                                                                        <a href="<c:url value="/profile?id=${post.userId}"/>" title="<spring:message code="index.profile"/>">
                                                                                            <span>${post.user.userName}</span>
                                                                                        </a>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <span>${post.user.userName}</span>
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                                <c:if test="${user.isAdmin && user.id ne post.userId}">
                                                                                    <c:choose>
                                                                                        <c:when test="${post.user.isBanned}">
                                                                                            <form action="<c:url value="/unbanUser?seriesId=${series.id}&userId=${post.userId}"/>"
                                                                                                  method="post" class="float-left" onsubmit="confirmAction(event,'<spring:message code="series.sureUnbanUser"/>')">
                                                                                                <button type="submit" class="remove">
                                                                                                    <img src="<c:url value="/resources/img/unban.png"/>" title="<spring:message code="series.unban"/>" alt="<spring:message code="series.unban"/>">
                                                                                                </button>
                                                                                            </form>
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                            <form action="<c:url value="/banUser?seriesId=${series.id}&userId=${post.userId}"/>"
                                                                                                  method="post" class="float-left" onsubmit="confirmAction(event,'<spring:message code="series.sureBanUser"/>')">
                                                                                                <button class="heart post-liked" style="font-family: FontAwesome,serif; font-style: normal">&#xf05e</button>
                                                                                            </form>
                                                                                        </c:otherwise>
                                                                                    </c:choose>
                                                                                </c:if>
                                                                                <div class="float-right mr-5">
                                                                                    <c:if test="${user.isAdmin || user.id eq post.userId}">
                                                                                        <form action="<c:url value="/removePost?seriesId=${series.id}&postId=${post.postId}"/>"
                                                                                              method="post" class="float-left" onsubmit="confirmAction(event,'<spring:message code="series.sureRemovePort"/>')">
                                                                                            <button type="submit" class="remove">
                                                                                                <img src="<c:url value="/resources/img/remove.png"/>" alt="<spring:message code="series.remove"/>">
                                                                                            </button>
                                                                                        </form>
                                                                                    </c:if>
                                                                                    <c:choose>
                                                                                        <c:when test="${isLogged}">
                                                                                            <c:choose>
                                                                                                <c:when test="${post.liked}">
                                                                                                    <form action="<c:url value="/unlikePost?seriesId=${series.id}&postId=${post.postId}"/>"
                                                                                                          method="post" class="float-left">
                                                                                                        <button type="submit" class="heart post-liked no-padding" style="font-family: FontAwesome,serif; font-style: normal">&#xf004</button>
                                                                                                        <span>${post.points}</span>
                                                                                                    </form>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <form action="<c:url value="/likePost?seriesId=${series.id}&postId=${post.postId}"/>"
                                                                                                          method="post" class="float-left">
                                                                                                        <button type="submit" class="heart no-padding" style="font-family: FontAwesome,serif; font-style: normal">&#xf004</button>
                                                                                                        <span>${post.points}</span>
                                                                                                    </form>
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                            <span style="font-family: FontAwesome,serif; font-style: normal">&#xf004</span>
                                                                                            <span>${post.points}</span>
                                                                                        </c:otherwise>
                                                                                    </c:choose>
                                                                                </div>
                                                                            </div>
                                                                            <blockquote class="original">
                                                                                <p>${post.body}</p>
                                                                            </blockquote>
                                                                        </div>
                                                                    </div>
                                                                </article>
                                                                <c:if test="${(isLogged && not user.isBanned)|| not empty post.comments}">
                                                                    <div class="replies sub-comment">
                                                                        <c:forEach var="comment" items="${post.comments}">
                                                                            <article class="reply clearfix initialized">
                                                                                <div class="holder">
                                                                                    <div class="author-label">
                                                                                        <c:choose>
                                                                                            <c:when test="${isLogged}">
                                                                                                <a href="<c:url value="/profile?id=${comment.userId}"/>" title="<spring:message code="index.profile"/>">
                                                                                                    <span style="font-family: proximaNova; color: #777;">${comment.user.userName}</span>
                                                                                                </a>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <span style="font-family: proximaNova; color: #777;">${comment.user.userName}</span>
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                        <c:if test="${user.isAdmin && user.id ne comment.userId}">
                                                                                            <c:choose>
                                                                                                <c:when test="${comment.user.isBanned}">
                                                                                                    <form action="<c:url value="/unbanUser?seriesId=${series.id}&userId=${comment.userId}"/>"
                                                                                                          method="post" class="float-left" onsubmit="confirmAction(event,'<spring:message code="series.sureUnbanUser"/>')">
                                                                                                        <button type="submit" class="remove">
                                                                                                            <img src="<c:url value="/resources/img/unban.png"/>" title="<spring:message code="series.unban"/>" alt="<spring:message code="series.unban"/>">
                                                                                                        </button>
                                                                                                    </form>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <form action="<c:url value="/banUser?seriesId=${series.id}&userId=${comment.userId}"/>"
                                                                                                          method="post" class="float-left" onsubmit="confirmAction(event,'<spring:message code="series.sureBanUser"/>')">
                                                                                                        <button class="heart post-liked" style="font-family: FontAwesome,serif; font-style: normal">&#xf05e</button>
                                                                                                    </form>
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                        </c:if>
                                                                                        <div class="float-right">
                                                                                            <c:if test="${user.isAdmin || user.id eq comment.userId}">
                                                                                                <form action="<c:url value="/removeComment?seriesId=${series.id}&postId=${post.postId}&commentId=${comment.commentId}"/>"
                                                                                                      method="post" class="float-left" onsubmit="confirmAction(event,'<spring:message code="series.sureRemoveComment"/>')">
                                                                                                    <button type="submit" class="remove-small">
                                                                                                        <img src="<c:url value="/resources/img/remove.png"/>" alt="<spring:message code="series.remove"/>">
                                                                                                    </button>
                                                                                                </form>
                                                                                            </c:if>
                                                                                            <c:choose>
                                                                                                <c:when test="${isLogged}">
                                                                                                    <c:choose>
                                                                                                        <c:when test="${comment.liked}">
                                                                                                            <form action="<c:url value="/unlikeComment?seriesId=${series.id}&postId=${post.postId}&commentId=${comment.commentId}"/>"
                                                                                                                  method="post" class="float-left">
                                                                                                                <button type="submit" class="heart post-liked" style="font-family: FontAwesome,serif; font-style: normal">&#xf004</button>
                                                                                                                <span>${comment.points}</span>
                                                                                                            </form>
                                                                                                        </c:when>
                                                                                                        <c:otherwise>
                                                                                                            <form action="<c:url value="/likeComment?seriesId=${series.id}&postId=${post.postId}&commentId=${comment.commentId}"/>"
                                                                                                                  method="post" class="float-left">
                                                                                                                <button type="submit" class="heart" style="font-family: FontAwesome,serif; font-style: normal">&#xf004</button>
                                                                                                                <span>${comment.points}</span>
                                                                                                            </form>
                                                                                                        </c:otherwise>
                                                                                                    </c:choose>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <span style="font-family: FontAwesome,serif; font-style: normal">&#xf004</span>
                                                                                                    <span>${comment.points}</span>
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                        </div>
                                                                                    </div>
                                                                                    <blockquote>
                                                                                        <p>${comment.body}</p>
                                                                                    </blockquote>
                                                                                </div>
                                                                            </article>
                                                                        </c:forEach>
                                                                        <c:if test="${isLogged && not user.isBanned}">
                                                                            <c:url value='comment' var="actionCommentValue"/>
                                                                            <form:form class="reply clearfix" modelAttribute="commentForm" action="${actionCommentValue}"
                                                                                       method="post"
                                                                                       enctype="application/x-www-form-urlencoded">
                                                                                <form:errors path="commentBody" element="p" cssClass="error text-left"/>
                                                                                <div class="holder">
                                                                                    <div class="textarea-wrapper">
                                                                                        <div class="mentions-input-box">
                                                                                            <spring:message code="series.enterReply" var="placeholder_reply"/>
                                                                                            <form:textarea
                                                                                                    rows="1"
                                                                                                    placeholder="${placeholder_reply}"
                                                                                                    path="commentBody"
                                                                                                    style="overflow: hidden; height: 50px;"/>
                                                                                            <form:input type="hidden"
                                                                                                        path="commentSeriesId"
                                                                                                        value="${series.id}"/>
                                                                                            <form:input type="hidden"
                                                                                                        path="commentPostId"
                                                                                                        value="${post.postId}"/>
                                                                                            <button type="submit" class="post-comment"><spring:message code="series.post"/></button>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="clearfix"></div>
                                                                                </div>
                                                                            </form:form>
                                                                        </c:if>
                                                                    </div>
                                                                </c:if>
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
