package com.alquinow.servlet;

import com.alquinow.dao.UsuarioDAO;
import com.alquinow.modelo.Usuario;
import com.alquinow.util.EmailUtil; // Importación de tu utilidad de correos

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Random; // Importación para generar números aleatorios

/**
 * Servlet que maneja el registro de nuevos usuarios.
 * Recibe los datos del formulario (registro.html) por POST.
 */
@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // MODIFICACIÓN 1: Capturamos el nombre que viene del HTML
        String nombre = req.getParameter("nombre");
        String mail = req.getParameter("mail");
        String pass = req.getParameter("contrasena");
        String dni = req.getParameter("dni");
        String tel = req.getParameter("tel");
        String rol = "comprador";

        // 1. Validación de campos básicos (Agregamos validación para el nombre)
        if (nombre == null || nombre.isBlank() || mail == null || mail.isBlank() || pass == null || pass.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/registro.html?error=vacios");
            return;
        }

        // 2. Validación estricta de DNI en el servidor
        if (dni == null || !dni.matches("\\d{8}")) {
            resp.sendRedirect(req.getContextPath() + "/registro.html?error=dni");
            return;
        }

        // 3. Validación estricta de Teléfono en el servidor
        if (tel == null || !tel.matches("\\+549\\d{10}")) {
            resp.sendRedirect(req.getContextPath() + "/registro.html?error=tel");
            return;
        }
        
        // 4. Validación estricta de la Contraseña
        if (pass == null || !pass.matches("^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$")) {
            resp.sendRedirect(req.getContextPath() + "/registro.html?error=pass");
            return;
        }
        
        try {
            // 5. Verificamos si el correo ya existe
            if (usuarioDAO.existeMail(mail)) {
                resp.sendRedirect(req.getContextPath() + "/registro.html?error=existe");
                return;
            }

            // 6. Generamos el código secreto de 6 dígitos
            Random rnd = new Random();
            int numeroAleatorio = rnd.nextInt(999999);
            String codigoGenerado = String.format("%06d", numeroAleatorio);

            Usuario u = new Usuario();
            
            // MODIFICACIÓN 2: Le inyectamos el nombre al objeto Usuario
            u.setNombre(nombre);
            
            u.setMail(mail);
            u.setContrasena(pass); // el DAO la hashea
            u.setDni(dni);
            u.setTel(tel);
            u.setCodigoVerificacion(codigoGenerado);

            int id = usuarioDAO.registrar(u, rol);
            
            if (id > 0) {
                // 7. Disparamos el correo real a través de tu cuenta de Gmail
                EmailUtil.enviarCodigo(mail, codigoGenerado);
                
                // 8. Mandamos al usuario a la pantalla de verificación con el mail en la URL
                resp.sendRedirect(req.getContextPath() + "/verificar.html?mail=" + mail);
            } else {
                resp.sendRedirect(req.getContextPath() + "/registro.html?error=db");
            }

        } catch (Exception e) {
            throw new ServletException("Error al registrar usuario", e);
        }
    }
}