var stompClient = null;

function setConnected(connected) {
    if (connected) {
        $("#numbers").show();
    }
    else {
        $("#numbers").hide();
    }
    $("#number-message").html("");
}

function connect() {
    var socket = new SockJS('/game_of_three');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/queue/rival/number', function (numberMessage) {
            showAppMessage(JSON.parse(numberMessage.body));
        });
    });
}

function sendRandomNumberMessage() {
    stompClient.send("/api/random_number", {}, JSON.stringify(
        {'number': $("#random_number").val()}
    ));
}

function sendAdditionNumberMessage(addition) {
    stompClient.send("/api/addition_number", {}, JSON.stringify(
        {'addition': addition}
    ));
}

function showAppMessage(message) {

    $("#app-message").append(
        "<tr><td>" + message.addition + " = " + message.number + " .. " + message.message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#send" ).click(function() { sendRandomNumberMessage(); });
    $( "#send_0" ).click(function() { sendAdditionNumberMessage(0); });
    $( "#send_1" ).click(function() { sendAdditionNumberMessage(1); });
    $( "#send_-1" ).click(function() { sendAdditionNumberMessage(-1); });
});
$(document).ready(function(){
    connect();
});

