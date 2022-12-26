const progress = $('#progressBar');

listeners['bootstrap'] = (data) => {
    progress.css("width", data + "%");
};