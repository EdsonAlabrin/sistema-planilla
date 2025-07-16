package com.textilima.textilima.model.enums;

public enum Sexo {
    M("M"), 
    F("F");

    private final String descripcion;

    Sexo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
