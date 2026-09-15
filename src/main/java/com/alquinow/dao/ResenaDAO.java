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
        // Los nombres de las columnas ahora coinciden exactamente con tu tabla MySQL
        String sql = "INSERT INTO resena (ID_propiedad_fk, ID_comprador_fk, id_reserva_fk, puntuacion, comentario) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, resena.getIdPropiedad());
            ps.setInt(2, resena.getIdUsuario());
            ps.setInt(3, resena.getIdReservaFk()); 
            ps.setInt(4, resena.getCalificacion()); // Esto está bien si tu objeto Java usa getCalificacion()
            ps.setString(5, resena.getComentario());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Resena> obtenerPorPropiedad(int idPropiedad) {
        List<Resena> resenas = new ArrayList<>();
        
        String sql = "SELECT r.*, u.mail "
                   + "FROM resena r "
                   + "JOIN Usuario u ON r.id_usuario = u.ID_usuario "
                   + "WHERE r.id_propiedad = ? "
                   + "ORDER BY r.fecha DESC";
                   
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPropiedad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { // ACÁ ESTABA TU MAPEO
                    Resena r = new Resena();
                    r.setId(rs.getInt("id"));
                    r.setIdPropiedad(rs.getInt("id_propiedad"));
                    r.setIdUsuario(rs.getInt("id_usuario"));
                    
                    r.setIdReservaFk(rs.getInt("id_reserva_fk")); // <-- 3. Leemos el ID de la base de datos
                    
                    r.setCalificacion(rs.getInt("calificacion"));
                    r.setComentario(rs.getString("comentario"));
                    r.setFecha(rs.getTimestamp("fecha"));
                    
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
    
    // 4. NUEVO MÉTODO: Chequea si el usuario ya reseñó esta estadía
    public boolean existeResenaParaReserva(int idReserva) {
        String sql = "SELECT 1 FROM resena WHERE id_reserva_fk = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Devuelve true si la reseña ya existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}