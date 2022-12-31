listeners['game_start_countdown'] = (value) => {
    $('#countdown').text(value['value']);
}