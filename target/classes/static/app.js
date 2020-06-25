var socket = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#messages").html("");
}

function connect() {
    socket = io('http://127.0.0.1:8180?userId=1');
    setConnected(true);
    socket.on("ping_event", function(message){
        showMessage(message);
    });
}

function disconnect() {
    socket.emit("disconnect_event", "User Disconnect Request");
    setConnected(false);
    console.log("Disconnected");
}

function sendData() {

    if(socket != null){
        socket.emit("ping_event", JSON.stringify(
        {
            'name': $("#name").val()
        }));
    }else{
        console.log("Socket not initialized.");
    }

}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
    console.log("Messages table updated!");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").unbind().click(function () {
        connect();
    });
    $("#disconnect").unbind().click(function () {
        disconnect();
    });
    $("#send").unbind().click(function () {
        sendData();
    });
});
