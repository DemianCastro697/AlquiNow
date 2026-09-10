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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/mis-favoritos-data")
public class MisFavoritosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Usuario u = (Usuario) session.getAttribute("usuario");
        int idComprador = u.getIdUsuario();

        StringBuilder json = new StringBuilder("[");

        // CORREGIDO: Usamos precio_por_noche según tu esquema
        String sql = "SELECT p.ID_propiedad, p.calle, p.altura, p.ciudad, p.provincia, p.descripcion, p.cant_personas, p.precio_por_noche " +
                     "FROM propiedad p " +
                     "INNER JOIN lista_deseos l ON p.ID_propiedad = l.ID_propiedad_fk " +
                     "WHERE l.ID_comprador_fk = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idComprador);
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    
                    json.append("{");
                    json.append("\"id\":").append(rs.getInt("ID_propiedad")).append(",");
                    json.append("\"calle\":\"").append(rs.getString("calle")).append("\",");
                    json.append("\"altura\":\"").append(rs.getString("altura")).append("\",");
                    json.append("\"ciudad\":\"").append(rs.getString("ciudad")).append("\",");
                    json.append("\"provincia\":\"").append(rs.getString("provincia")).append("\",");
                    json.append("\"descripcion\":\"").append(rs.getString("descripcion")).append("\",");
                    json.append("\"cantPersonas\":").append(rs.getInt("cant_personas")).append(",");
                    // CORREGIDO: Leemos la columna exacta de MySQL
                    json.append("\"precioPorNoche\":").append(rs.getDouble("precio_por_noche"));
                    json.append("}");
                    
                    first = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        json.append("]");
        try (PrintWriter out = resp.getWriter()) {
            out.print(json.toString());
        }
    }
}
