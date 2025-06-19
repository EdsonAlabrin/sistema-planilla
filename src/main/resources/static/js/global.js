// Archivo para JavaScript global de la aplicación.
// Puedes añadir funciones que necesites en múltiples páginas aquí.

document.addEventListener('DOMContentLoaded', function() {
    // Ejemplo: Un script para mostrar una alerta temporalmente y luego desvanecerla.
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 1s ease-out';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 1000); // Eliminar después de la transición
        }, 5000); // La alerta permanece visible durante 5 segundos
    });
});
