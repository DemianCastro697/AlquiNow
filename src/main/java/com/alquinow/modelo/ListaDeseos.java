package com.alquinow.modelo;

import java.sql.Timestamp;

public class ListaDeseos {
    private int idLista;
    private int idCompradorFk;
    private int idPropiedadFk;
    private Timestamp fechaAgregado;

    public ListaDeseos() {
    }

    public ListaDeseos(int idCompradorFk, int idPropiedadFk) {
        this.idCompradorFk = idCompradorFk;
        this.idPropiedadFk = idPropiedadFk;
    }

    public int getIdLista() { return idLista; }
    public void setIdLista(int idLista) { this.idLista = idLista; }

    public int getIdCompradorFk() { return idCompradorFk; }
    public void setIdCompradorFk(int idCompradorFk) { this.idCompradorFk = idCompradorFk; }

    public int getIdPropiedadFk() { return idPropiedadFk; }
    public void setIdPropiedadFk(int idPropiedadFk) { this.idPropiedadFk = idPropiedadFk; }

    public Timestamp getFechaAgregado() { return fechaAgregado; }
    public void setFechaAgregado(Timestamp fechaAgregado) { this.fechaAgregado = fechaAgregado; }
}