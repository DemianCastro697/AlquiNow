package com.alquinow.dao;

import com.alquinow.modelo.Resena;
import com.alquinow.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResenaDAO {

    public boolean insertar(Resena resena) {
        String sql = "INSERT INTO resena (id_propiedad, id_usuario, calificacion, comentario) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, resena.getIdPropiedad());
            ps.setInt(2, resena.getIdUsuario());
            ps.setInt(3, resena.getCalificacion());
            ps.setString(4, resena.getComentario());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Resena> obtenerPorPropiedad(int idPropiedad) {
        List<Resena> resenas = new ArrayList<>();
        // Join con usuario para obtener el email (u.mail)
        String sql = "SELECT r.*, u.mail "
                   + "FROM resena r "
                   + "JOIN Usuario u ON r.id_usuario = u.ID_usuario "
                   + "WHERE r.id_propiedad = ? "
                   + "ORDER BY r.fecha DESC";
                   
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPropiedad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Resena r = new Resena();
                    r.setId(rs.getInt("id"));
                    r.setIdPropiedad(rs.getInt("id_propiedad"));
                    r.setIdUsuario(rs.getInt("id_usuario"));
                    r.setCalificacion(rs.getInt("calificacion"));
                    r.setComentario(rs.getString("comentario"));
                    r.setFecha(rs.getTimestamp("fecha"));
                    // En tu tabla de usuario no hay nombre/apellido, usamos el mail para mostrar en la reseña
                    String mail = rs.getString("mail");
                    String nombreMostrado = mail != null ? mail.split("@")[0] : "Usuario";
                    r.setNombreUsuario(nombreMostrado);
                    resenas.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resenas;
    }
}
