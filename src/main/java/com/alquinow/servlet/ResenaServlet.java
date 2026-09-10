package com.alquinow.servlet;

import com.alquinow.dao.ResenaDAO;
import com.alquinow.modelo.Resena;
import com.alquinow.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/resenas")
public class ResenaServlet extends HttpServlet {

    private final ResenaDAO resenaDAO = new ResenaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Obtener reseñas de una propiedad específica
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String idPropiedadStr = req.getParameter("idPropiedad");
        if (idPropiedadStr != null) {
            int idPropiedad = Integer.parseInt(idPropiedadStr);
            List<Resena> resenas = resenaDAO.obtenerPorPropiedad(idPropiedad);
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < resenas.size(); i++) {
                Resena r = resenas.get(i);
                json.append("{")
                    .append("\"id\":").append(r.getId()).append(",")
                    .append("\"nombreUsuario\":\"").append(escape(r.getNombreUsuario())).append("\",")
                    .append("\"calificacion\":").append(r.getCalificacion()).append(",")
                    .append("\"comentario\":\"").append(escape(r.getComentario())).append("\",")
                    .append("\"fecha\":\"").append(r.getFecha().toString()).append("\"")
                    .append("}");
                if (i < resenas.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            
            try (PrintWriter out = resp.getWriter()) {
                out.print(json.toString());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"Debes iniciar sesión para dejar una reseña\"}");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        String idPropiedadStr = req.getParameter("idPropiedad");
        String calificacionStr = req.getParameter("calificacion");
        String comentario = req.getParameter("comentario");

        try {
            int idPropiedad = Integer.parseInt(idPropiedadStr);
            int calificacion = Integer.parseInt(calificacionStr);
            
            if (calificacion < 1 || calificacion > 5) {
                out.print("{\"error\":\"La calificación debe estar entre 1 y 5\"}");
                return;
            }

            Resena resena = new Resena();
            resena.setIdPropiedad(idPropiedad);
            resena.setIdUsuario(usuario.getIdUsuario());
            resena.setCalificacion(calificacion);
            resena.setComentario(comentario);

            if (resenaDAO.insertar(resena)) {
                out.print("{\"ok\":true, \"mensaje\":\"Reseña publicada con éxito\"}");
            } else {
                out.print("{\"error\":\"No se pudo publicar la reseña\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"Datos inválidos\"}");
        }
    }

    // Helper simple para escapar comillas en JSON
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}
