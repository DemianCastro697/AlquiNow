// Apenas carga la página, le preguntamos al backend si hay sesión activa
fetch('api/session')
  .then(response => response.json())
  .then(data => {
    if (data.conectado) {
      const navLinks = document.querySelector('.nav-links');
      
      if (navLinks) {
          let html = '';
          
          if (data.esHuesped) {
              html += `<a href="index.html">Buscar</a>`;
              html += `<a href="mis-reservas.html">Mis Reservas</a>`;
              html += `<a href="mis-favoritos.html">Mis Favoritos ❤️</a>`; 
          }
          
          if (data.esVendedor) {
              html += `<a href="panel-vendedor.html">Mi Panel</a>`;
          } else {
              html += `<a href="convertir-vendedor" style="color: #28a745; font-weight: bold;">Publicar mi propiedad</a>`;
          }
          
          // ---> EL SALUDO CON TU NOMBRE <---
          let nombreUsuario = data.nombre ? data.nombre : "Usuario";
          html += `<span style="margin-left: 15px; margin-right: 15px; color: #333; font-weight: bold; cursor: default;">👋 Hola, ${nombreUsuario}</span>`;
          
          html += `<a href="logout" style="color: var(--brasa); font-weight: bold;">Cerrar Sesión</a>`;
          
          navLinks.innerHTML = html;
      }
    }
  })
  .catch(error => console.error("Error chequeando la sesión:", error));