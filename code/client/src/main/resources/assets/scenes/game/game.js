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
    chatHistory.animate({ scrollTop: chatHistory.height() }, 1000);
}

function addPlayer(player) {
    const col = $(`<div class="col-3 col"></div>`);

    const playerContainer = $(playerFragment.replaceAll('{player}', player['uuid'])
        .replaceAll('{icon}', player['alive'] ? (showRole(player) ? player['role'].toLowerCase() : 'unknown') : 'dead')
        .replaceAll('{name}', player['name'])
        .replaceAll('{role}', showRole(player) ? capitalize(player['role'].toLowerCase()) : 'Unknown')
    );
    if (!player['alive']) playerContainer.addClass('dead');
    col.append(playerContainer);
    playersContainer.append(col);
    playerContainer.on('click', () => {
        onPlayerClick(playerContainer);
    });
}

function updatePlayer(player) {
    const playerContainer = $(`#${player['uuid']}`);
    if (!player['alive']) {
        playerContainer.addClass('dead');
        playerContainer.find('.player-icon').attr('src', 'local://assets/images/roles/dead.jpg');
    }

    if (showRole(player)) {
        if (player['alive']) playerContainer.find('.player-icon').attr('src', `local://assets/images/roles/${player['role'].toLowerCase()}.jpg`);
        playerContainer.find('.player-role').text(capitalize(player['role'].toLowerCase()));
    }
}

function showRole(player) {
    return player['uuid'] === selfUUID || player['role_known_by'].includes(selfUUID);
}

function onPlayerClick(playerContainer) {
    if (!active) return;
    toBackend({type: 'player_click', id: playerContainer.attr('id')});
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
    const time = data['time'];
    if (time >= 0) {
        clockText.text(time);
        clock.fadeTo('fast', 1);
    } else {
        clock.fadeTo('fast', 0);
    }
}

listeners['chat_message'] = (message) => {
    newChatMessage(message);
}

listeners['set_chat_icon'] = (icon) => {
    chatIcon.attr('src', `local://assets/images/${icon['icon']}.png`);
}

listeners['update_player'] = (update) => {
    updatePlayer(update['player'], update['active']);
    active = update['player']['uuid'] === selfUUID && update['active'];
}

listeners['game_update'] = (update) => {
    const game = update['game'];

    if (game['night']) {
        mainContainer.removeClass('day');
        mainContainer.addClass('night');
    } else {
        mainContainer.removeClass('night');
        mainContainer.addClass('day');
    }
}

const chatFragment = `<div class="chat-message"><p><span class="chat-channel">{channel}</span><span class="chat-sender">{from}</span>{message}</p></div>`;
const playerFragment = `<div id="{player}" class="player">
        <img class="player-icon" src="local://assets/images/roles/{icon}.jpg" alt="player">
        <p class="player-name">{name}</p>
        <p class="player-role">{role}</p>
      </div>`;