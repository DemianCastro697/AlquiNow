package com.alquinow.servlet;

import com.alquinow.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/session")
public class SessionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        // Pedimos la sesión, pero le pasamos "false" para que NO cree una nueva si no existe
        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute("usuario") != null) {
            // ¡Hay alguien conectado! Le pasamos los datos básicos al frontend
            Usuario u = (Usuario) session.getAttribute("usuario");
            
            // Mandamos ambos permisos en formato JSON (true o false)
           out.print("{\"conectado\": true, \"esVendedor\": " + u.isVendedor() + ", \"esHuesped\": " + u.isHuesped() + ", \"usuario\": {\"idUsuario\": " + u.getIdUsuario() + "}}");
        } else {
            // Nadie logueado
            out.print("{\"conectado\": false}");
        }
    }
}