const allowedCodeChars = 'ABCDEFGHJKLMNPQRSTUVWXYZ0123456789';
const allowedNameChars = 'ABCDEFGHIJKLMNOPQRTSUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_';
const tooltips = $('[data-bs-toggle="tooltip"]')
tooltips.each((idx, elem) => {
    new bootstrap.Tooltip(elem);
});

$('#newGame').on('click', () => {
    toBackend({type: 'new_game'});
});

$('#joinGame').on('keydown', (event) => {
    if (event.which === 13) toBackend({type: 'join_game', data: [event.target.value.toUpperCase()]});
});

$('#joinGame').on('input', (event) => {
    let value = event.target.value.toUpperCase();
    if (!allowedCodeChars.includes(value[value.length - 1])) value = value.slice(0, -1);
    event.target.value = value;
});

$('#closeBtn').on('click', () => {
    toBackend({type: 'close'});
});

$('#version').on('click', () => {
    //TODO Show version modal
});

$('#settingsBtn').on('click', () => {
    toBackend({type: 'set_scene', data: ['settings']});
});

$('#helpBtn').on('click', () => {
    toBackend({type: 'set_scene', data: ['help']});
});

$(document).on('shown.bs.modal', () => {
    $('#nameSelector').on('keydown', (event) => {
        if (event.which === 13) {
            let value = event.target.value;
            console.log(value);
            if (value.length < 0) return;

            toBackend({type: 'set_username', data: [event.target.value]});
            bootstrap.Modal.getInstance($('#username_selection')).hide();
        }
    });

    $('#nameSelector').on('input', (event) => {
        let value = event.target.value;
        if (!allowedNameChars.includes(value[value.length - 1])) value = value.slice(0, -1);
        event.target.value = value;
    });
});