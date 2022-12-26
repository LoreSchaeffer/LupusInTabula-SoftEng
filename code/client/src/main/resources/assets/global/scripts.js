const listeners = {};

function onMessage(msg) {
    if (typeof(msg) !== 'object') {
        console.log('Invalid message received: ' + msg);
        return;
    }

    if (!msg.type in listeners) {
        console.log(`No listeners for type ${msg.type}`);
        return;
    }

    listeners[msg.type](msg.data);
}

function toBackend(data) {
    window.cefQuery({
        request: JSON.stringify(data)
    });
}