package com.alquinow.modelo;

import java.sql.Timestamp;

public class Resena {
    private int id;
    private int idPropiedad;
    private int idUsuario;
    private String nombreUsuario; // Extra, para mostrar en el frontend
    private int calificacion;
    private String comentario;
    private Timestamp fecha;

    public Resena() {
    }

    public Resena(int id, int idPropiedad, int idUsuario, int calificacion, String comentario, Timestamp fecha) {
        this.id = id;
        this.idPropiedad = idPropiedad;
        this.idUsuario = idUsuario;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPropiedad() { return idPropiedad; }
    public void setIdPropiedad(int idPropiedad) { this.idPropiedad = idPropiedad; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { this.calificacion = calificacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
}
