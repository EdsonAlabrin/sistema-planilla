// src/main/resources/static/js/modal-handler.js

// Variable global para almacenar la URL de confirmación.
let currentConfirmUrl = '';

/**
 * Cuando el DOM (Document Object Model) esté completamente cargado,
 * adjuntamos los escuchadores de eventos a los botones.
 * Esto asegura que los elementos HTML existan antes de intentar manipulados.
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Content Loaded: modal-handler.js iniciado.');

    // Selecciona todos los botones con la clase 'action-button'
    // que se usan para activar/desactivar empleados.
    const actionButtons = document.querySelectorAll('.action-button');

    // Itera sobre cada botón y le adjunta un escuchador de eventos 'click'.
    actionButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            // Previene el comportamiento por defecto del enlace (evita la navegación inmediata).
            event.preventDefault();
            console.log('Botón de acción clickeado. Previniendo comportamiento por defecto.');

            // Obtiene el ID del empleado y el tipo de acción de los atributos 'data-'.
            const empleadoId = this.getAttribute('data-id');
            const actionType = this.getAttribute('data-action'); // 'activar' o 'desactivar'

            let message = '';
            let title = '';

            // Define el mensaje y el título del modal basado en el tipo de acción.
            if (actionType === 'desactivar') {
                message = '¿Estás seguro de desactivar a este empleado? Una vez desactivado, no podrá acceder al sistema hasta que sea activado de nuevo.';
                title = 'Confirmar Desactivación';
            } else if (actionType === 'activar') {
                message = '¿Estás seguro de activar a este empleado? Una vez activado, podrá acceder al sistema.';
                title = 'Confirmar Activación';
            }

            // Llama a la función para mostrar el modal con los detalles específicos.
            showConfirmModal(title, message, empleadoId, actionType);
        });
    });

    // Adjuntar evento al botón "Cancelar" del modal
    const cancelActionBtn = document.getElementById('cancelActionBtn');
    if (cancelActionBtn) {
        cancelActionBtn.addEventListener('click', function() {
            console.log('Botón Cancelar clickeado. Ocultando modal.');
            hideConfirmModal();
        });
    }

    // Adjuntar evento al botón "Confirmar" del modal
    const confirmActionBtn = document.getElementById('confirmActionBtn');
    if (confirmActionBtn) {
        confirmActionBtn.addEventListener('click', function() {
            // Redirige la página a la URL de confirmación previamente guardada.
            console.log('Botón Confirmar clickeado. Redirigiendo a:', currentConfirmUrl);
            if (currentConfirmUrl) {
                window.location.href = currentConfirmUrl;
            } else {
                console.error('ERROR: currentConfirmUrl no está definido al hacer clic en Confirmar.');
                // Aquí podrías añadir una notificación al usuario si quieres.
            }
        });
    }
});


/**
 * Muestra el modal de confirmación con el mensaje y la acción adecuados.
 * @param {string} title - El título que se mostrará en el encabezado del modal.
 * @param {string} message - El mensaje principal que se mostrará en el cuerpo del modal.
 * @param {string} empleadoId - El ID del empleado involucrado en la acción.
 * @param {string} actionType - El tipo de acción ('activar' o 'desactivar').
 */
function showConfirmModal(title, message, empleadoId, actionType) {
    console.log(`showConfirmModal: Función iniciada. ID: ${empleadoId}, Acción: ${actionType}`);

    const modal = document.getElementById('confirmModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');

    if (!modal || !modalTitle || !modalMessage) {
        console.error('showConfirmModal: ERROR - Uno o más elementos del modal no fueron encontrados. Verifique IDs en el HTML.');
        return;
    }

    // Establecer el contenido del modal
    modalTitle.textContent = title;
    modalMessage.textContent = message;

    // Construir la URL a la que se redirigirá la página al confirmar.
    currentConfirmUrl = `/empleados/${actionType}/${empleadoId}`;
    console.log(`showConfirmModal: URL de confirmación establecida a: ${currentConfirmUrl}`);

    // Mostrar el modal.
    modal.style.display = 'flex';
    console.log('showConfirmModal: Modal visible (display: flex).');
}

/**
 * Oculta el modal de confirmación.
 */
function hideConfirmModal() {
    console.log('hideConfirmModal: Función iniciada.');
    const modal = document.getElementById('confirmModal');
    if (modal) {
        modal.style.display = 'none';
        console.log('hideConfirmModal: Modal oculto (display: none).');
    }
}

// Listener global para cerrar el modal si el usuario hace clic fuera de su contenido.
window.onclick = function(event) {
    const modal = document.getElementById('confirmModal');
    if (modal && modal.style.display === 'flex' && event.target === modal) {
        console.log('window.onclick: Clic detectado fuera del contenido del modal. Ocultando.');
        hideConfirmModal();
    }
};
