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
        onSuccess: (player) => {
            player = JSON.parse(player);
            addPlayer(player, true);

            if (!player['master']) {
                window.cefQuery({
                    request: JSON.stringify({type: 'get_players'}),
                    onSuccess: (players) => {
                        players = JSON.parse(players);

                        for (const p of players) {
                            if (player['uuid'] == p['uuid']) continue;
                            addPlayer(p, false);
                        }
                    }
                });
            }
        }
    });
});

function addPlayer(player, self) {
    const fragment = player['master'] ? playerMasterFragment : playerFragment;
    const p = $(fragment.replaceAll('{id}', player['uuid'])
        .replaceAll('{name}', player['name']));
    if (self) p.addClass('self');
    playerlist.append(p);
}

function removePlayer(id) {
    $('#' + id).remove();
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

listeners['player_join'] = (data) => {
    addPlayer(data['player'], false);
}

listeners['player_leave'] = (data) => {
    removePlayer(data['client_id']);
}

listeners['ready_to_start'] = (ready) => {
    if (ready) $('#startGame').removeAttr('disabled');
    else $('#startGame').attr('disabled', 'true');
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