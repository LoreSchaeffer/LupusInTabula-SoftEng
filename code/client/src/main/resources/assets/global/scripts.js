const listeners = {};

function onMessage(msg) {
    if (typeof (msg) !== 'object') {
        console.log('Invalid message received: ' + msg);
        return;
    }

    if (!msg.type in listeners) {
        console.log(`No listeners for type ${msg.type}`);
        return;
    }

    listeners[msg.type](msg.data);
}

function toBackend(data) {
    window.cefQuery({
        request: JSON.stringify(data)
    });
}

const mainContainer = $('#mainContainer');

function showModal(data) {
    const modalComponent = $(modalFragment.replaceAll("{size}", 'size' in data ? data.size : '')
        .replaceAll("{id}", data['id'])
        .replaceAll("{content}", data['content']));
    mainContainer.append();

    const modal = new bootstrap.Modal(modalComponent);

    $(`#close-${data['id']}`).on('click', () => {
        modal.hide();
    });

    modalComponent.on('hidden.bs.modal', () => {
        modal.dispose();
        modalComponent.remove();
    });

    modal.show();
}
listeners['show_modal'] = showModal;

const modalFragment = `<div class="modal fade" id="{id}" tabindex="-1" role="dialog" aria-labelledby="{id}" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered {size}">
    <div class="modal-content">
      <div class="modal-header">
        <a id="close-{id}" type="button" data-dismiss="modal"><i class="fa-solid fa-xmark"></i></a>
      </div>
      <div class="modal-body">
        {content}
      </div>
    </div>
  </div>
</div>`;