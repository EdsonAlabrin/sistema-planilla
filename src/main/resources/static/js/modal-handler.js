// src/main/resources/static/js/modal-handler.js

// ... (resto del código del modal-handler.js, incluyendo showConfirmModal, hideConfirmModal, etc.)

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Content Loaded: modal-handler.js iniciado.');

    // Selecciona todos los botones con la clase 'action-button'
    const actionButtons = document.querySelectorAll('.action-button');

    // Adjunta un escuchador de eventos a cada botón de acción
    actionButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault(); // Previene el comportamiento por defecto del enlace
            const empleadoId = this.getAttribute('data-id');
            const actionType = this.getAttribute('data-action');

            let message = '';
            let title = '';

            if (actionType === 'desactivar') {
                message = '¿Estás seguro de desactivar a este empleado? Una vez desactivado, no podrá acceder al sistema hasta que sea activado de nuevo.';
                title = 'Confirmar Desactivación';
            } else if (actionType === 'activar') {
                message = '¿Estás seguro de activar a este empleado? Una vez activado, podrá acceder al sistema.';
                title = 'Confirmar Activación';
            }

            // Llama a la función para mostrar el modal
            showConfirmModal(title, message, empleadoId, actionType);
        });
    });

    // ... (listeners para el botón Cancelar y Confirmar del modal, y window.onclick)
    const cancelActionBtn = document.getElementById('cancelActionBtn');
    if (cancelActionBtn) {
        cancelActionBtn.addEventListener('click', function() {
            hideConfirmModal();
        });
    }

    const confirmActionBtn = document.getElementById('confirmActionBtn');
    if (confirmActionBtn) {
        confirmActionBtn.addEventListener('click', function() {
            if (currentConfirmUrl) {
                window.location.href = currentConfirmUrl; // Esto envía la solicitud POST al controlador
            }
        });
    }

    window.onclick = function(event) {
        const modal = document.getElementById('confirmModal');
        if (modal && modal.style.display === 'flex' && event.target === modal) {
            hideConfirmModal();
        }
    };
});
