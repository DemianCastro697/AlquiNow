package com.alquinow.dao;

import com.alquinow.modelo.Usuario;
import com.alquinow.util.Conexion;
import com.alquinow.util.Password;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Acceso a datos para Usuario, Comprador y Vendedor.
 * Maneja registro, verificación por correo y autenticación.
 */
public class UsuarioDAO {

    public int registrar(Usuario u, String rol) throws SQLException {
        // 1. Agregamos cuenta_activa y codigo_verificacion al INSERT
        String sqlUsuario =
            "INSERT INTO Usuario (contrasena, dni, mail, tel, es_vendedor, es_huesped, cuenta_activa, codigo_verificacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = null;
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); 

            int idGenerado;
            try (PreparedStatement ps = con.prepareStatement(
                    sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, Password.hashear(u.getContrasena()));
                ps.setString(2, u.getDni());
                ps.setString(3, u.getMail());
                ps.setString(4, u.getTel());
                ps.setBoolean(5, "vendedor".equalsIgnoreCase(rol));
                ps.setBoolean(6, true); // Todos son huéspedes por defecto
                ps.setBoolean(7, false); // Nace inactiva hasta que verifique el código
                ps.setString(8, u.getCodigoVerificacion()); // Guardamos el código de 6 dígitos

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

            // MODIFICACIÓN: TODOS son compradores por defecto (para poder guardar favoritos y alquilar)
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Comprador (ID_usuario) VALUES (?)")) {
                ps.setInt(1, idGenerado);
                ps.executeUpdate();
            }

            // MODIFICACIÓN: SI ADEMÁS eligió ser vendedor, lo agregamos también a su respectiva tabla
            if ("vendedor".equalsIgnoreCase(rol)) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO Vendedor (ID_usuario, verificado) VALUES (?, FALSE)")) {
                    ps.setInt(1, idGenerado);
                    ps.executeUpdate();
                }
            }

            con.commit(); 
            return idGenerado;

        } catch (SQLException e) {
            if (con != null) {
                con.rollback(); 
            }
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    // Método para validar el código y activar la cuenta en MySQL
    public boolean verificarCodigo(String mail, String codigoIngresado) throws SQLException {
        String sqlSelect = "SELECT ID_usuario, codigo_verificacion FROM Usuario WHERE mail = ? AND cuenta_activa = 0";
        String sqlUpdate = "UPDATE Usuario SET cuenta_activa = 1, codigo_verificacion = NULL WHERE ID_usuario = ?";

        try (Connection con = Conexion.getConexion()) {
            con.setAutoCommit(false);
            try {
                int idUsuario = -1;
                String codigoBD = null;

                // 1. Buscamos el código asignado a ese correo
                try (PreparedStatement ps = con.prepareStatement(sqlSelect)) {
                    ps.setString(1, mail);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            idUsuario = rs.getInt("ID_usuario");
                            codigoBD = rs.getString("codigo_verificacion");
                        }
                    }
                }

                // 2. Comparamos el código ingresado con el de la base de datos
                if (idUsuario != -1 && codigoBD != null && codigoBD.equals(codigoIngresado)) {
                    // 3. Si coincide, activamos la cuenta
                    try (PreparedStatement psUp = con.prepareStatement(sqlUpdate)) {
                        psUp.setInt(1, idUsuario);
                        psUp.executeUpdate();
                    }
                    con.commit();
                    return true;
                }

                con.rollback();
                return false;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public Usuario autenticar(String mail, String passwordPlano) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE mail = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Validamos que la cuenta esté activa antes de dejarlo entrar
                    if (!rs.getBoolean("cuenta_activa")) {
                        return null; // Cuenta pendiente de verificación
                    }
                    
                    String hash = rs.getString("contrasena");
                    if (Password.verificar(passwordPlano, hash)) {
                        return mapear(rs);
                    }
                }
            }
        }
        return null;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE ID_usuario = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public boolean existeMail(String mail) throws SQLException {
        String sql = "SELECT 1 FROM Usuario WHERE mail = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mail);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("ID_usuario"));
        u.setContrasena(rs.getString("contrasena"));
        u.setDni(rs.getString("dni"));
        u.setMail(rs.getString("mail"));
        u.setTel(rs.getString("tel"));
        u.setEsVendedor(rs.getBoolean("es_vendedor"));
        u.setEsHuesped(rs.getBoolean("es_huesped"));
        // Mapeamos los nuevos campos de verificación
        u.setCuentaActiva(rs.getBoolean("cuenta_activa"));
        u.setCodigoVerificacion(rs.getString("codigo_verificacion"));
        return u;
    }
}