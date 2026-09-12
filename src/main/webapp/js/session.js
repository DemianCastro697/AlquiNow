//// Apenas carga la página, le preguntamos al backend si hay sesión activa
fetch('api/session')
  .then(response => response.json())
  .then(data => {
    if (data.conectado) {
      // Si hay sesión, buscamos la barra de navegación
      const navLinks = document.querySelector('.nav-links');
      
      // Si la página tiene una barra de navegación, la actualizamos
      if (navLinks) {
          let html = '';
          
          // Si tiene permiso de huésped, le agregamos estas opciones
          if (data.esHuesped) {
              html += `<a href="index.html">Buscar</a>`;
              html += `<a href="mis-reservas.html">Mis Reservas</a>`;
              html += `<a href="mis-favoritos.html">Mis Favoritos ❤️</a>`; // NUEVO BOTÓN AGREGADO
          }
          
          // Si tiene permiso de vendedor, sumamos el acceso a su panel
          if (data.esVendedor) {
              html += `<a href="panel-vendedor.html">Mi Panel</a>`;
          } else {
              // Si NO es vendedor, le ofrecemos convertirse
              html += `<a href="convertir-vendedor" style="color: #28a745; font-weight: bold;">Publicar mi propiedad</a>`;
          }
          
          // Finalmente, todos necesitan el botón para salir
          html += `<a href="logout" style="color: var(--brasa); font-weight: bold;">Cerrar Sesión</a>`;
          
          // Insertamos todo el bloque armado en el HTML
          navLinks.innerHTML = html;
      }
    }
  })
  .catch(error => console.error("Error chequeando la sesión:", error));