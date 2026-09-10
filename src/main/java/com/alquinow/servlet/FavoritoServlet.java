package com.alquinow.servlet;

import com.alquinow.dao.ListaDeseosDAO;
import com.alquinow.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/favorito")
public class FavoritoServlet extends HttpServlet {

    private final ListaDeseosDAO deseosDAO = new ListaDeseosDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Verificamos que el usuario haya iniciado sesión
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Error 401: No autorizado
            resp.getWriter().write("no_auth");
            return;
        }

        Usuario u = (Usuario) session.getAttribute("usuario");
        int idComprador = u.getIdUsuario(); // Tu ID_comprador_fk es el mismo que el ID_usuario
        
        String idPropiedadStr = req.getParameter("idPropiedad");
        String accion = req.getParameter("accion"); // Vendrá como "agregar" o "eliminar"

        if (idPropiedadStr == null || accion == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Error 400
            return;
        }

        try {
            int idPropiedad = Integer.parseInt(idPropiedadStr);
            boolean exito = false;

            // 2. Procesamos la acción en la base de datos
            if (accion.equals("agregar")) {
                if (!deseosDAO.esFavorito(idComprador, idPropiedad)) {
                    exito = deseosDAO.agregarFavorito(idComprador, idPropiedad);
                } else {
                    exito = true; // Si ya estaba, lo damos por exitoso
                }
                if(exito) resp.getWriter().write("agregado");
                
            } else if (accion.equals("eliminar")) {
                exito = deseosDAO.eliminarFavorito(idComprador, idPropiedad);
                if(exito) resp.getWriter().write("eliminado");
            }
            
        } catch (Exception e) {
            e.printStackTrace(); // NUEVO: Esto va a imprimir el error exacto en la consola de NetBeans
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
        }
    }
}