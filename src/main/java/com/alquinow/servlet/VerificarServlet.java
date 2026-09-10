package com.alquinow.servlet;

import com.alquinow.dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/verificar")
public class VerificarServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String mail = req.getParameter("mail");
        String codigo = req.getParameter("codigo");

        if (mail == null || codigo == null || codigo.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/verificar.html?mail=" + mail + "&error=invalido");
            return;
        }

        try {
            // El DAO compara el código y cambia cuenta_activa a 1 si es correcto
            boolean activado = usuarioDAO.verificarCodigo(mail, codigo);

            if (activado) {
                // Código correcto -> lo mandamos al login para que inicie sesión
                resp.sendRedirect(req.getContextPath() + "/login.html?verificado=1");
            } else {
                // Código incorrecto -> vuelve a la misma pantalla con un error
                resp.sendRedirect(req.getContextPath() + "/verificar.html?mail=" + mail + "&error=invalido");
            }
        } catch (Exception e) {
            throw new ServletException("Error al verificar la cuenta", e);
        }
    }
}