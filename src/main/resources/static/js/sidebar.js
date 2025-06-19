// src/main/resources/static/js/sidebar-menu.js
function toggleSubmenu(id) {
    const submenu = document.getElementById(id);
    const toggleBtn = submenu.previousElementSibling; // El botón es el elemento hermano anterior

    // Cerrar otros submenús abiertos
    document.querySelectorAll('.submenu').forEach(s => {
        // Asegúrate de cerrar solo si el submenú no es el actual
        if (s.id !== id && s.style.display === 'flex') {
            s.style.display = 'none';
            // También remueve la clase 'active' del botón de alternancia correspondiente
            if (s.previousElementSibling) {
                s.previousElementSibling.classList.remove('active');
            }
        }
    });

    // Alternar el submenú actual
    if (submenu.style.display === 'flex') {
        submenu.style.display = 'none';
        toggleBtn.classList.remove('active');
    } else {
        submenu.style.display = 'flex';
        toggleBtn.classList.add('active');
    }
}

// Opcional: Mantener el submenú abierto si un elemento dentro de él está activo
// Esto es útil si recargas la página y quieres que el menú permanezca expandido
document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;

    document.querySelectorAll('.sidebar a').forEach(link => {
        // Verifica si el href del enlace coincide con la ruta actual
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active-link'); // Puedes añadir una clase CSS para resaltar el enlace activo
            let parentSubmenu = link.closest('.submenu');
            if (parentSubmenu) {
                parentSubmenu.style.display = 'flex';
                // También activa el botón de alternancia del submenú
                let toggleBtn = parentSubmenu.previousElementSibling;
                if (toggleBtn && toggleBtn.classList.contains('submenu-toggle')) {
                    toggleBtn.classList.add('active');
                }
            }
        }
    });
});
