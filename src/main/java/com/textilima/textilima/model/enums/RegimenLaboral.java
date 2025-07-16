package com.textilima.textilima.model.enums;

public enum RegimenLaboral {
    REGIMEN_GENERAL("Régimen General"),
    MICROEMPRESA("Microempresa (Ley MYPE)"),
    PEQUENA_EMPRESA("Pequeña Empresa (Ley MYPE)"),
    CONSTRUCCION_CIVIL("Construcción Civil"),
    AGRARIO("Régimen Agrario"),
    TRABAJADOR_HOGAR("Trabajador del Hogar");

    private final String descripcion;

    RegimenLaboral(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
