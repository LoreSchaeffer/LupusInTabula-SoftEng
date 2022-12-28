const playerlist = $('#playerlist');

$(window).on('load', () => {
    window.cefQuery({
        request: JSON.stringify({type: 'get_game_code'}),
        onSuccess: (response) => {
            $('#gameCode').text(response);
        }
    });

    window.cefQuery({
        request: JSON.stringify({type: 'get_self'}),
        onSuccess: (response) => {
            response = JSON.parse(response);
            addPlayer(response['uuid'], response['name'], true, response['master']);
        }
    });
});

function addPlayer(id, name, self, master) {
    const fragment = master ? playerMasterFragment : playerFragment;
    const player = $(fragment.replaceAll('{id}', id).replaceAll('{name}', name));
    if (self) player.addClass('self');
    playerlist.append(player);
}

function removePlayer(id) {
    playerlist.remove($('#' + id));
}

$('#exitBtn').on('click', () => {
    toBackend({type: 'leave_game'});
});

$('#startGame').on('click', () => {
    toBackend({type: 'start_game'});
});

$('#gameCode').on('click', () => {
    const input = $('<input type="text" style="opacity: 0; position: absolute">');
    mainContainer.append(input);
    input.val($('#gameCode').text());
    input.select();
    document.execCommand('copy');
    input.remove();
});

listeners['player_joined_lobby'] = (data) => {
    addPlayer(data['uuid'], data['name'], false, data['master']);
}

listeners['player_left_lobby'] = (data) => {
    removePlayer(data['uuid']);
}

const playerFragment = `<div id="{id}" class="player">
    <img class="player-icon" src="local://assets/images/user_icon.png" alt="User icon">
        <h3>{name}</h3>
</div>`;
const playerMasterFragment = `<div id="{id}" class="player">
    <img class="player-icon" src="local://assets/images/user_icon.png" alt="User icon">
        <h3>{name}</h3>
        <h4 class="crown"><i class="fa-solid fa-crown"></i></h4>
</div>`;