package com.alquinow.dao;

import com.alquinow.modelo.Reserva;
import com.alquinow.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos para Reserva.
 */
public class ReservaDAO {

    /**
     * Crea una reserva nueva, bloquea los días en Disponibilidad y devuelve su
     * ID generado.
     */
    public int crear(Reserva r) throws SQLException {
        String sqlReserva
                = "INSERT INTO Reserva "
                + "(ID_comprador_fk, ID_propiedad_fk, fecha_inicio, fecha_final, "
                + " estado, monto_total, fecha_reserva, dias_cancelacion_aplicados, "
                + " fecha_limite_cancelacion) "
                + "VALUES (?, ?, ?, ?, ?, ?, CURDATE(), ?, ?)";

        String sqlDisponibilidad
                = "INSERT INTO Disponibilidad (ID_propiedad_fk, fecha, estado) VALUES (?, ?, 'Ocupado')";

        Connection con = null;
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // Arranca la transacción

            int idGenerado = -1;

            // 1. Insertar la reserva
            try (PreparedStatement ps = con.prepareStatement(sqlReserva, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, r.getIdCompradorFk());
                ps.setInt(2, r.getIdPropiedadFk());
                ps.setDate(3, r.getFechaInicio());
                ps.setDate(4, r.getFechaFinal());
                ps.setString(5, r.getEstado() == null ? "pendiente" : r.getEstado());
                ps.setBigDecimal(6, r.getMontoTotal());

                if (r.getDiasCancelacionAplicados() == null) {
                    ps.setNull(7, java.sql.Types.INTEGER);
                } else {
                    ps.setInt(7, r.getDiasCancelacionAplicados());
                }
                ps.setDate(8, r.getFechaLimiteCancelacion());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                    } else {
                        con.rollback();
                        return -1;
                    }
                }
            }

            // 2. Insertar las fechas bloqueadas en la tabla Disponibilidad
            try (PreparedStatement psDisp = con.prepareStatement(sqlDisponibilidad)) {
                // Convertimos las fechas de SQL a LocalDate de Java para poder iterar fácilmente
                LocalDate inicio = r.getFechaInicio().toLocalDate();
                LocalDate fin = r.getFechaFinal().toLocalDate();

                // Recorremos día por día: desde el inicio hasta el día ANTERIOR a la salida
                for (LocalDate fecha = inicio; fecha.isBefore(fin); fecha = fecha.plusDays(1)) {
                    psDisp.setInt(1, r.getIdPropiedadFk());
                    psDisp.setDate(2, java.sql.Date.valueOf(fecha));
                    psDisp.addBatch(); // Usamos batch para enviar todos los inserts de una sola vez
                }
                psDisp.executeBatch(); // Ejecutamos la carga masiva
            }

            con.commit(); // Confirmamos que todo se guardó correctamente
            return idGenerado;

        } catch (SQLException e) {
            if (con != null) {
                con.rollback(); // Si algo falló (ej. en la reserva o en el calendario), deshacemos todo
            }
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    /**
     * Lista las reservas de un comprador, con la dirección exacta de la
     * propiedad.
     */
    public List<Reserva> listarPorComprador(int idComprador) throws SQLException {
        // 1. Sumamos p.calle y p.altura a la selección
        String sql
                = "SELECT r.*, p.ciudad AS ciudad_prop, p.calle AS calle_prop, p.altura AS altura_prop "
                + "FROM Reserva r "
                + "JOIN Propiedad p ON r.ID_propiedad_fk = p.ID_propiedad "
                + "WHERE r.ID_comprador_fk = ? "
                + "ORDER BY r.fecha_reserva DESC";

        List<Reserva> lista = new ArrayList<>();
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idComprador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reserva r = mapear(rs);
                    // 2. Guardamos los nuevos datos de la dirección en la reserva
                    r.setCiudad(rs.getString("ciudad_prop"));
                    r.setCalle(rs.getString("calle_prop"));
                    r.setAltura(rs.getInt("altura_prop"));
                    lista.add(r);
                }
            }
        }
        return lista;
    }

    /**
     * Cambia el estado de una reserva (ej: "cancelada", "confirmada").
     */
    public boolean actualizarEstado(int idReserva, String nuevoEstado)
            throws SQLException {
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(
                "UPDATE Reserva SET estado = ? WHERE ID_reserva = ?")) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idReserva);
            return ps.executeUpdate() > 0;
        }
    }

    private Reserva mapear(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        r.setIdReserva(rs.getInt("ID_reserva"));
        r.setIdCompradorFk(rs.getInt("ID_comprador_fk"));
        r.setIdPropiedadFk(rs.getInt("ID_propiedad_fk"));
        r.setFechaInicio(rs.getDate("fecha_inicio"));
        r.setFechaFinal(rs.getDate("fecha_final"));
        r.setEstado(rs.getString("estado"));
        r.setMontoTotal(rs.getBigDecimal("monto_total"));
        r.setFechaReserva(rs.getDate("fecha_reserva"));
        r.setDiasCancelacionAplicados(rs.getInt("dias_cancelacion_aplicados"));
        r.setFechaLimiteCancelacion(rs.getDate("fecha_limite_cancelacion"));
        return r;
    }
}
