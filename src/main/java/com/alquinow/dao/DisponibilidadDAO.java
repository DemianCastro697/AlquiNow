package com.alquinow.dao;

import com.alquinow.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DisponibilidadDAO {

    // 1. Definimos la consulta SQL
    private static final String SQL_FECHAS_OCUPADAS = 
        "SELECT fecha FROM Disponibilidad WHERE ID_propiedad_fk = ? AND estado = 'Ocupado'";

    // 2. Creamos el método que ejecutará la consulta
    public List<String> obtenerFechasOcupadas(int idPropiedad) {
        List<String> fechasOcupadas = new ArrayList<>();

        // Usamos la clase Conexion que ya tienen en el proyecto
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FECHAS_OCUPADAS)) {

            // Reemplazamos el "?" por el ID de la propiedad
            pstmt.setInt(1, idPropiedad);

            // Ejecutamos la consulta y leemos los resultados
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Guardamos cada fecha en la lista
                    fechasOcupadas.add(rs.getString("fecha"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fechasOcupadas;
    }
}