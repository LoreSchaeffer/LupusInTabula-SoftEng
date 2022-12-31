const clock = $('#clock');
const clockText = $('#clockText');
const chatHistory = $('#chatHistory');

function newChatMessage(message) {
    const chatMessage = $(chatFragment.replaceAll("{channel}", message['channel'])
        .replaceAll("{from}", message['from'])
        .replaceAll("{message}", message['message']));
    chatHistory.append(chatMessage);
}

$(window).on('load', () => {
    newChatMessage({channel: 'All', from: 'Test', message: 'Test message'});
    newChatMessage({channel: 'All', from: 'Test', message: 'Test message'});
    newChatMessage({channel: 'All', from: 'Test', message: 'Test message'});
    newChatMessage({channel: 'All', from: 'Test', message: 'Test message'});
    newChatMessage({channel: 'All', from: 'Test', message: 'Test message'});
    newChatMessage({channel: 'All', from: 'Test', message: 'Test message'});
    newChatMessage({channel: 'All', from: 'Test', message: 'Test message'});
});


listeners['timer'] = (data) => {
    console.log(data);
}

const chatFragment = `<div class="chat-message"><p><span class="chat-channel">{channel}</span><span>{from}</span>{message}</p></div>`;