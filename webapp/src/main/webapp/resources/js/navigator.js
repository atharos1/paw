function getRelativeURL() {
    return window.location.pathname.substring(
        window.location.pathname.lastIndexOf("/") + 1
    );
}

function selectCurrentMenuItem() {
    var relativeURL = getRelativeURL();

    if(relativeURL == "")
        relativeURL = "home";

    if (relativeURL != 'series') {
        var link = $(".all-left-navs").find("#menu_" + relativeURL);

        var previousImg = $(link).find("img").attr("src");
        var newImg = previousImg.replace(".png", "_active.png");
        $(link).find("img").attr("src", newImg);
        link.parent().addClass("active");
    }
}

$( document ).ready(function() {
    selectCurrentMenuItem();
});

function extend() {
    if ($(".page-left").hasClass("extended")) {
        $(".page-left").removeClass("extended");
    } else {
        $(".page-left").addClass("extended");
    }
}

function confirmAction(event, message) {
    event.preventDefault();

    var form = event.target;
    var accept = confirm(message);

    if (accept)
        form.submit();
}