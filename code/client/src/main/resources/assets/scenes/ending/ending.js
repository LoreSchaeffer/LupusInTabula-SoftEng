const winnerText = $('#winner');

$(document).ready(() => {
    window.cefQuery({
        request: JSON.stringify({type: 'get_self'}),
        onSuccess: (response) => {
            const self = JSON.parse(response);
            const selfRole = self['role'];
            //TODO Needed for the background (future)

            window.cefQuery({
                request: JSON.stringify({type: 'get_winner'}),
                onSuccess: (winner) => {
                    if (winner === "WEREWOLF") {
                        winnerText.text('${win.villagers}');
                    } else {
                        winnerText.text('${win.werewolves}');
                    }
                }
            });
        }
    });
});

$('#quit').on('click', () => {
    toBackend({type: 'leave_game'});
});

$('#replay').on('click', () => {
    toBackend({type: 'replay'});
});