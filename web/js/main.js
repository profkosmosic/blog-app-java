function Burger() {
    let content = document.getElementById("navMobileContent");
    if(content.style.display === "block") {
        content.style.display = "none";
    }
    else {
        content.style.display = "block";
    }
}

function Close() {
    let content = document.getElementById("navMobileContent");
    content.display.style = "none";
}