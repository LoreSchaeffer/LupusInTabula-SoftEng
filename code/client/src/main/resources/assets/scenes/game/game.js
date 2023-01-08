const clock = $('#clock');
const clockText = $('#clockText');
const chatHistory = $('#chatHistory');
const chatIcon = $('#chatIcon');
const playersContainer = $('#playersContainer');

function newChatMessage(message) {
    const chatMessage = $(chatFragment.replaceAll("{channel}", message['channel'])
        .replaceAll("{from}", message['from'])
        .replaceAll("{message}", message['message']));
    chatHistory.append(chatMessage);
}

function addPlayer(player) {
    const col = $(`<div class="col-3 col"></div>`);

    const playerContainer = $(playerFragment.replaceAll("{player}", player['id'])
        .replaceAll("{icon}", !player['dead'] ? player['icon'] : 'dead')
        .replaceAll("{name}", player['name'])
        .replaceAll("{role}", player['role'])
    );
    if (player['dead']) playerContainer.addClass('dead');
    col.append(playerContainer);
    playersContainer.append(col);
}

$(document).ready(() => {
    addPlayer({id: '1', icon: 'unknown', name: 'Player 1', role: 'Villager'});
    addPlayer({id: '2', icon: 'villager', name: 'Player 2', role: 'Villager'});
    addPlayer({id: '3', icon: 'werewolf', name: 'Player 3', role: 'Villager'});
    addPlayer({id: '4', icon: 'unknown', name: 'Player 4', role: 'Villager'});
    addPlayer({id: '5', icon: 'unknown', name: 'Player 5', role: 'Villager', dead: true});
    addPlayer({id: '6', icon: 'unknown', name: 'Player 6', role: 'Villager'});
    addPlayer({id: '7', icon: 'unknown', name: 'Player 7', role: 'Villager'});
    addPlayer({id: '8', icon: 'unknown', name: 'Player 8', role: 'Villager'});
});

listeners['timer'] = (data) => {
    console.log(data);
}

listeners['chat_message'] = (message) => {
    newChatMessage(message);
}

listeners['set_chat_icon'] = (icon) => {
    chatIcon.attr('src', `local://assets/images/${icon['icon']}.png`);
}

const chatFragment = `<div class="chat-message"><p><span class="chat-channel">{channel}</span><span class="chat-sender">{from}</span>{message}</p></div>`;
const playerFragment = `<div id="{player}" class="player">
        <img class="player-icon" src="local://assets/images/roles/{icon}.jpg" alt="player">
        <p class="player-name">{name}</p>
        <p class="player-role">{role}</p>
      </div>`;