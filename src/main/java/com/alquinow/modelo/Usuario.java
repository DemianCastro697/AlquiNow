package com.alquinow.modelo;

public class Usuario {

    private int idUsuario;
    private String contrasena; 
    private String dni;
    private String mail;
    private String tel;

    private boolean esVendedor;
    private boolean esHuesped;
    
    // NUEVAS VARIABLES PARA EL MAIL
    private boolean cuentaActiva;
    private String codigoVerificacion;

    public Usuario() {
    }

    public Usuario(int idUsuario, String mail, String tel) {
        this.idUsuario = idUsuario;
        this.mail = mail;
        this.tel = tel;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public boolean isVendedor() { return esVendedor; }
    public void setEsVendedor(boolean esVendedor) { this.esVendedor = esVendedor; }

    public boolean isHuesped() { return esHuesped; }
    public void setEsHuesped(boolean esHuesped) { this.esHuesped = esHuesped; }

    public boolean isCuentaActiva() { return cuentaActiva; }
    public void setCuentaActiva(boolean cuentaActiva) { this.cuentaActiva = cuentaActiva; }

    public String getCodigoVerificacion() { return codigoVerificacion; }
    public void setCodigoVerificacion(String codigoVerificacion) { this.codigoVerificacion = codigoVerificacion; }
}