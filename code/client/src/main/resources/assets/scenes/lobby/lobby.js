$(window).on('load', () => {
    window.cefQuery({
        request: JSON.stringify({type: 'get_game_code'}),
        onSuccess: (response) => {
            $('#gameCode').text(response);
        }
    });

    window.cefQuery({
        request: JSON.stringify({type: 'get_players'}),
        onSuccess: (response) => {

        }
    });
});

function addPlayer(name, self, isHost) {

}

listeners['player_joined_lobby'] = (data) => {
    addPlayer(data.name, data.self, data.host);
}