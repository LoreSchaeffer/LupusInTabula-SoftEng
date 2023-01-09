const clock = $('#clock');
const clockText = $('#clockText');
const chatHistory = $('#chatHistory');
const chatIcon = $('#chatIcon');
const chatInput = $('#chatInput');
const playersContainer = $('#playersContainer');

let selfUUID = null;
let active = false;

function newChatMessage(message) {
    const chatMessage = $(chatFragment.replaceAll("{channel}", message['channel'])
        .replaceAll("{from}", message['from'])
        .replaceAll("{message}", message['message']));
    chatHistory.append(chatMessage);
}

function addPlayer(player) {
    const col = $(`<div class="col-3 col"></div>`);

    const playerContainer = $(playerFragment.replaceAll('{player}', player['uuid'])
        .replaceAll('{icon}', player['alive'] ? (player['uuid'] === selfUUID ? player['role'].toLowerCase() : 'unknown') : 'dead')
        .replaceAll('{name}', player['name'])
        .replaceAll('{role}', player['uuid'] === selfUUID ? capitalize(player['role'].toLowerCase()) : 'Unknown')
    );
    if (!player['alive']) playerContainer.addClass('dead');
    col.append(playerContainer);
    playersContainer.append(col);
    playerContainer.on('click', () => {
        onPlayerClick(playerContainer);
    });
}

function updatePlayer(player, showRole) {
    const playerContainer = $(`#${player['uuid']}`);
    if (!player['alive']) {
        playerContainer.addClass('dead');
        playerContainer.find('.player-icon').attr('src', 'local://assets/images/roles/dead.jpg');
    }

    if (showRole) {
        if (player['alive']) playerContainer.find('.player-icon').attr('src', `local://assets/images/roles/${player['role'].toLowerCase()}.jpg`);
        playerContainer.find('.player-role').text(capitalize(player['role'].toLowerCase()));
    }
}

function onPlayerClick(playerContainer) {
    if (!active) return;

    window.cefQuery({
        request: JSON.stringify({type: 'player_click', uuid: playerContainer.attr('id')}),
        onSuccess: (response) => {
            response = JSON.parse(response);
            console.log(response);
            //TODO
        }
    });
}

$(document).ready(() => {
    window.cefQuery({
        request: JSON.stringify({type: 'get_self'}),
        onSuccess: (response) => {
            response = JSON.parse(response);
            selfUUID = response['uuid'];

            window.cefQuery({
                request: JSON.stringify({type: 'get_players'}),
                onSuccess: (players) => {
                    players = JSON.parse(players);
                    playersContainer.empty();

                    players.forEach((player) => {
                        addPlayer(player);
                    });
                }
            });
        }
    });
});

chatInput.on('keydown', (event) => {
    if (event.which === 13) {
        toBackend({type: 'chat_message', message: chatInput.val()});
        chatInput.val('');
    }
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

listeners['update_player'] = (update) => {
    updatePlayer(update['player'], update['show_role'], update['active']);
    if (update['player']['uuid'] === selfUUID && update['active']) active = true;
    else active = false;
}

const chatFragment = `<div class="chat-message"><p><span class="chat-channel">{channel}</span><span class="chat-sender">{from}</span>{message}</p></div>`;
const playerFragment = `<div id="{player}" class="player">
        <img class="player-icon" src="local://assets/images/roles/{icon}.jpg" alt="player">
        <p class="player-name">{name}</p>
        <p class="player-role">{role}</p>
      </div>`;