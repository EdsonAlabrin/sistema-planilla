package com.textilima.textilima.service;

import java.util.List;
import java.util.Optional;

import com.textilima.textilima.entities.Empleado;

public interface EmpleadoService {
    List<Empleado> listarTodosLosEmpleados();

    Optional<Empleado> obtenerEmpleadoPorId(Integer idEmpleado);

    Empleado crearEmpleado(Empleado empleado);

    Empleado actualizarEmpleado(Integer idEmpleado, Empleado empleadoDetalles);

    void desactivarEmpleado(Integer idEmpleado);

    void activarEmpleado(Integer idEmpleado);

    // Métodos que deben ser implementados
    List<Empleado> listarEmpleadosActivos(); // Agregado/confirmado

    List<Empleado> buscarPorNombreOApellido(String query); // Agregado/confirmado

    Empleado obtenerEmpleadoPorDni(String dni); // Agregado/confirmado

    boolean cambiarEstadoEmpleado(Integer id, boolean nuevoEstado);

    /**
     * Busca un empleado por su número de documento (DNI).
     * @param numeroDocumento El número de documento del empleado.
     * @return Un Optional que contiene el Empleado si se encuentra, o vacío si no.
     */
    Optional<Empleado> findByNumeroDocumento(String numeroDocumento);
}
