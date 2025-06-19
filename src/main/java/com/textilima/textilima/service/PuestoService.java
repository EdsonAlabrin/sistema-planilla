package com.textilima.textilima.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.textilima.textilima.entities.Puesto;

@Service
public interface PuestoService {
    Puesto crearPuesto(Puesto puesto);
    Puesto actualizarPuesto(Integer idPuesto, Puesto puesto);
    Puesto obtenerPuestoPorId(Integer idPuesto);
    List<Puesto> listarTodosLosPuestos();
    void eliminarPuesto(Integer idPuesto); // Considerar si realmente se elimina o se desactiva
    Puesto obtenerPuestoPorNombre(String nombrePuesto);

}
