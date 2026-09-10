package com.alquinow.servlet;

import com.alquinow.dao.DisponibilidadDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// Esta anotación define la URL a la que el frontend hará la petición
@WebServlet("/disponibilidad")
public class DisponibilidadServlet extends HttpServlet {

    private DisponibilidadDAO disponibilidadDAO = new DisponibilidadDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Configuramos la respuesta para que el frontend entienda que es un JSON
        response.setContentType("application/json; charset=UTF-8");
        
        String idParam = request.getParameter("idPropiedad");
        
        try (PrintWriter out = response.getWriter()) {
            if (idParam == null || idParam.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Falta el ID de la propiedad\"}");
                return;
            }

            int idPropiedad = Integer.parseInt(idParam);
            
            // Le pedimos al DAO las fechas ocupadas
            List<String> fechasOcupadas = disponibilidadDAO.obtenerFechasOcupadas(idPropiedad);
            
            // Construimos la lista de fechas en formato JSON 
            // Ejemplo del resultado: ["2026-08-25", "2026-08-26"]
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < fechasOcupadas.size(); i++) {
                json.append("\"").append(fechasOcupadas.get(i)).append("\"");
                if (i < fechasOcupadas.size() - 1) {
                    json.append(", ");
                }
            }
            json.append("]");
            
            out.print(json.toString());
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\": \"ID de propiedad inválido\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\": \"Error en el servidor\"}");
        }
    }
}