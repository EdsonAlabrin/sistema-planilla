package com.textilima.textilima.model.enums;

public enum EstadoCivil {
    Soltero("Soltero(a)"),
    Casado("Casado(a)"),
    Divorciado("Divorciado(a)"),
    Viudo("Viudo(a)"),
    Conviviente("Conviviente");

    private final String descripcion;

    EstadoCivil(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
