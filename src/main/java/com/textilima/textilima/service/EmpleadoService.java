package com.textilima.textilima.service;

import java.util.List;
import com.textilima.textilima.entities.Empleado;

public interface EmpleadoService {
    Empleado crearEmpleado(Empleado empleado);
    Empleado actualizarEmpleado(Integer idEmpleado, Empleado empleado);
    Empleado obtenerEmpleadoPorId(Integer idEmpleado);
    Empleado obtenerEmpleadoPorDni(String dni);
    List<Empleado> listarTodosLosEmpleados();
    List<Empleado> listarEmpleadosActivos();
    List<Empleado> buscarPorNombreOApellido(String busqueda);
    void desactivarEmpleado(Integer idEmpleado);
    void activarEmpleado(Integer idEmpleado);
}
