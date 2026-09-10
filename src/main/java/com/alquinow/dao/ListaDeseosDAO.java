package com.alquinow.dao;

import com.alquinow.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListaDeseosDAO {

    // 1. Agregar una propiedad a la lista de deseos
    public boolean agregarFavorito(int idComprador, int idPropiedad) throws SQLException {
        // Usamos NOW() de MySQL para que la base de datos ponga la fecha y hora sola
        String sql = "INSERT INTO lista_deseos (ID_comprador_fk, ID_propiedad_fk, fecha_agregado) VALUES (?, ?, NOW())";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, idComprador);
            ps.setInt(2, idPropiedad);
            return ps.executeUpdate() > 0;
        }
    }

    // 2. Eliminar una propiedad de la lista (cuando destildan el corazón)
    public boolean eliminarFavorito(int idComprador, int idPropiedad) throws SQLException {
        String sql = "DELETE FROM lista_deseos WHERE ID_comprador_fk = ? AND ID_propiedad_fk = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, idComprador);
            ps.setInt(2, idPropiedad);
            return ps.executeUpdate() > 0;
        }
    }

    // 3. Comprobar si ya es favorito (para saber de qué color mostrar el corazón en el HTML)
    public boolean esFavorito(int idComprador, int idPropiedad) throws SQLException {
        String sql = "SELECT 1 FROM lista_deseos WHERE ID_comprador_fk = ? AND ID_propiedad_fk = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, idComprador);
            ps.setInt(2, idPropiedad);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}