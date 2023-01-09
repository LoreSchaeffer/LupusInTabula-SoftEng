const listeners = {};

function onMessage(msg) {
    if (typeof (msg) !== 'object') {
        console.log('Invalid message received: ' + msg);
        return;
    }

    if (!msg['type'] in listeners) {
        console.log(`No listeners for type ${msg.type}`);
        return;
    }

    listeners[msg.type](msg);
}

function toBackend(data) {
    window.cefQuery({
        request: JSON.stringify(data)
    });
}

function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

const mainContainer = $('#mainContainer');

function showModal(data) {
    const modalComponent = $(modalFragment
        .replaceAll('{id}', data['id'])
        .replaceAll('{title}' , data['title'])
        .replaceAll('{body}', data['custom'] ? data['body'] : `<p>${data['body']}</p>`)
        .replaceAll('{size}', 'size' in data && data['size'] ? 'modal-lg' : ''));
    mainContainer.append();

    const modal = new bootstrap.Modal(modalComponent);

    $('.modal-close').on('click', () => {
        console.log("Closing modal");
        modal.hide();
    });

    modalComponent.on('hidden.bs.modal', () => {
        modal.dispose();
        modalComponent.remove();
    });

    modal.show();
}
listeners['show_modal'] = showModal;

const tooltips = $('[data-bs-toggle="tooltip"]')
tooltips.each((idx, elem) => {
    new bootstrap.Tooltip(elem);
});

const modalFragment = `<div class="modal fade" id="{id}" tabindex="-1" role="dialog" aria-labelledby="{id}" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered {size}">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <div class="modal-body">
        <h2>{title}</h2>
        {body}
      </div>
    </div>
  </div>
</div>`;