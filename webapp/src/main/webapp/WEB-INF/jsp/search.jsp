<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8"%>

<html>
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
</head>
<body>
<div class="full-overlay"></div>
<div class="container h-100">
    <div class="row justify-content-center h-100">
        <div class="col-lg-5 col-sm-11 align-self-center white-background">
            <div class="w-100 divide-section-bottom text-center">
                <img src="<c:url value="/resources/img/shortcuticon.png"/>" alt="TUTV">
                <span class="title-page"><spring:message code="search.advancedSearch"/></span>
            </div>
            <form action="<c:url value="/search"/>" method="post" enctype="application/x-www-form-urlencoded">
                <div class="form-group">
                    <label for="seriesNameInput"><spring:message code="search.name"/> </label>
                    <input type="text" class="form-control" id="seriesNameInput" name="name">
                </div>
                <div class="form-group">
                    <label for="seriesGenreInputSelect"><spring:message code="search.genre"/></label>
                    <select class="form-control" id="seriesGenreInputSelect" name="genre">
                        <option value="" selected>
                            <spring:message code="search.all"/>
                        </option>
                        <c:forEach items="${genres}" var="genre">
                            <option value="<c:out value="${genre.name}"/>"><c:out value="${genre.name}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="networkInputSelect"><spring:message code="search.network"/></label>
                    <select class="form-control" id="networkInputSelect" name="network">
                        <option value="" selected>
                            <spring:message code="search.all"/>
                        </option>
                        <c:forEach items="${networks}" var="network">
                            <option value="<c:out value="${network}"/>"><c:out value="${network}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <div class="text-center m-3">
                        <input class="tutv-button" type="submit" value="<spring:message code="search.search"/>"/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="text-center m-3" >
                        <a href="<c:url value="/"/>" style="color: black">
                            <input class="tutv-button" type="button" value="<spring:message code="error.goHome"/>">
                        </a>
                    </div>
                </div>

            </form>
        </div>
    </div>
</div>
</body>
</html>
