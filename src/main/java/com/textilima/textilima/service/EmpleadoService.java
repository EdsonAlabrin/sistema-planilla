package com.textilima.textilima.service;

import java.util.List;
import com.textilima.textilima.entities.Empleado;

public interface EmpleadoService {
   List<Empleado> listarTodosLosEmpleados();
    Empleado obtenerEmpleadoPorId(Integer id);
    Empleado crearEmpleado(Empleado empleado);
    Empleado actualizarEmpleado(Integer id, Empleado empleadoDetalles);
    void desactivarEmpleado(Integer id);
    void activarEmpleado(Integer id);
    
    // Métodos que deben ser implementados
    List<Empleado> listarEmpleadosActivos(); // Agregado/confirmado
    List<Empleado> buscarPorNombreOApellido(String query); // Agregado/confirmado
    Empleado obtenerEmpleadoPorDni(String dni); // Agregado/confirmado
}
