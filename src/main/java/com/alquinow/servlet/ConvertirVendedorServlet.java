package com.alquinow.servlet;

import com.alquinow.modelo.Usuario;
import com.alquinow.util.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/convertir-vendedor")
public class ConvertirVendedorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        Usuario u = (Usuario) session.getAttribute("usuario");

        // Si ya es vendedor, lo mandamos directo al panel
        if (u.isVendedor()) {
            resp.sendRedirect("panel-vendedor.html");
            return;
        }

        String updateUsuario = "UPDATE Usuario SET es_vendedor = 1 WHERE ID_usuario = ?";
        String insertVendedor = "INSERT INTO Vendedor (ID_usuario, verificado) VALUES (?, 0)";

        try (Connection con = Conexion.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(updateUsuario);
                 PreparedStatement ps2 = con.prepareStatement(insertVendedor)) {
                
                ps1.setInt(1, u.getIdUsuario());
                ps1.executeUpdate();
                
                ps2.setInt(1, u.getIdUsuario());
                ps2.executeUpdate();
                
                con.commit();
                
                // Actualizamos la sesión en vivo para que el menú cambie
                u.setEsVendedor(true);
                session.setAttribute("usuario", u);
                
                resp.sendRedirect("panel-vendedor.html");
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}