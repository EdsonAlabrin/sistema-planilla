package com.textilima.textilima.service.impl;

import com.textilima.textilima.entities.Empleado; // Importa la entidad Empleado
import com.textilima.textilima.entities.Puesto; // Importa la entidad Puesto, ya que Empleado tiene una relación con Puesto
import com.textilima.textilima.repository.EmpleadoRepository; // Importa el repositorio de Empleado
import com.textilima.textilima.repository.PuestoRepository; // Importa el repositorio de Puesto
import com.textilima.textilima.service.EmpleadoService; // Importa la interfaz de servicio de Empleado
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime; // Para las fechas de creación/actualización
import java.util.List;

// Anotación que marca esta clase como un componente de servicio de Spring
@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    private final EmpleadoRepository empleadoRepository;
    private final PuestoRepository puestoRepository; // Necesario para gestionar la relación con Puesto

    // Inyección de dependencias a través del constructor
    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository, PuestoRepository puestoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.puestoRepository = puestoRepository;
    }

    /**
     * Crea un nuevo empleado en la base de datos.
     * @param empleado El objeto Empleado a guardar.
     * @return El empleado guardado.
     */
    @Override
    @Transactional // La operación modifica la base de datos
    public Empleado crearEmpleado(Empleado empleado) {
        // Validar si el puesto existe antes de guardar el empleado
        if (empleado.getPuesto() != null && empleado.getPuesto().getIdPuesto() != null) {
            Puesto puesto = puestoRepository.findById(empleado.getPuesto().getIdPuesto())
                            .orElseThrow(() -> new RuntimeException("Puesto no encontrado con ID: " + empleado.getPuesto().getIdPuesto()));
            empleado.setPuesto(puesto);
        } else {
            throw new IllegalArgumentException("El empleado debe tener un puesto asignado con un ID válido.");
        }

        // Antes de guardar, asegúrate que las fechas created_at y updated_at se seteen si es una nueva creación
        if (empleado.getCreatedAt() == null) {
            empleado.setCreatedAt(LocalDateTime.now());
        }
        empleado.setUpdatedAt(LocalDateTime.now()); // Siempre actualizar updated_at

        return empleadoRepository.save(empleado);
    }

    /**
     * Actualiza la información de un empleado existente.
     * @param idEmpleado El ID del empleado a actualizar.
     * @param empleado Los nuevos datos del empleado.
     * @return El empleado actualizado.
     */
    @Override
    @Transactional // La operación modifica la base de datos
    public Empleado actualizarEmpleado(Integer idEmpleado, Empleado empleado) {
        return empleadoRepository.findById(idEmpleado)
                .map(empleadoExistente -> {
                    // Actualizar campos del empleado existente con los nuevos datos
                    empleadoExistente.setNombres(empleado.getNombres());
                    empleadoExistente.setApellidos(empleado.getApellidos());
                    empleadoExistente.setTipoDocumento(empleado.getTipoDocumento());
                    empleadoExistente.setNumeroDocumento(empleado.getNumeroDocumento());
                    empleadoExistente.setFechaNacimiento(empleado.getFechaNacimiento());
                    empleadoExistente.setSexo(empleado.getSexo());
                    empleadoExistente.setEstadoCivil(empleado.getEstadoCivil());
                    empleadoExistente.setNacionalidad(empleado.getNacionalidad());
                    empleadoExistente.setCorreo(empleado.getCorreo());
                    empleadoExistente.setDireccionCompleta(empleado.getDireccionCompleta());
                    empleadoExistente.setDistrito(empleado.getDistrito());
                    empleadoExistente.setProvincia(empleado.getProvincia());
                    empleadoExistente.setDepartamento(empleado.getDepartamento());
                    empleadoExistente.setFechaIngreso(empleado.getFechaIngreso());
                    empleadoExistente.setFechaCese(empleado.getFechaCese()); // Puede ser null
                    empleadoExistente.setEstado(empleado.getEstado());

                    // Actualizar puesto (validar si el puesto existe)
                    if (empleado.getPuesto() != null && empleado.getPuesto().getIdPuesto() != null) {
                        Puesto puesto = puestoRepository.findById(empleado.getPuesto().getIdPuesto())
                                .orElseThrow(() -> new RuntimeException("Puesto no encontrado con ID: " + empleado.getPuesto().getIdPuesto()));
                        empleadoExistente.setPuesto(puesto);
                    } else {
                        throw new IllegalArgumentException("El empleado debe tener un puesto asignado con un ID válido para la actualización.");
                    }

                    // Actualizar banco (puede ser null)
                    empleadoExistente.setBanco(empleado.getBanco()); // Asegúrate que la entidad Banco se gestiona correctamente
                    empleadoExistente.setNumeroCuentaBanco(empleado.getNumeroCuentaBanco());

                    empleadoExistente.setRegimenLaboral(empleado.getRegimenLaboral());
                    empleadoExistente.setSistemaPensiones(empleado.getSistemaPensiones());
                    empleadoExistente.setCodigoPension(empleado.getCodigoPension());
                    empleadoExistente.setNombreAfp(empleado.getNombreAfp());

                    empleadoExistente.setUpdatedAt(LocalDateTime.now()); // Actualizar fecha de modificación
                    return empleadoRepository.save(empleadoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + idEmpleado));
    }

    /**
     * Obtiene un empleado por su ID.
     * @param idEmpleado El ID del empleado.
     * @return El empleado si existe.
     */
    @Override
    @Transactional(readOnly = true) // La operación es solo de lectura
    public Empleado obtenerEmpleadoPorId(Integer idEmpleado) {
        return empleadoRepository.findById(idEmpleado).orElse(null);
    }

    /**
     * Obtiene un empleado por su número de documento (DNI).
     * @param dni El número de documento del empleado.
     * @return El empleado si existe.
     */
    @Override
    @Transactional(readOnly = true) // La operación es solo de lectura
    public Empleado obtenerEmpleadoPorDni(String dni) {
        return empleadoRepository.findByNumeroDocumento(dni).orElse(null);
    }

    /**
     * Lista todos los empleados en la base de datos.
     * @return Una lista de todos los empleados.
     */
    @Override
    @Transactional(readOnly = true) // La operación es solo de lectura
    public List<Empleado> listarTodosLosEmpleados() {
        return empleadoRepository.findAll();
    }

    /**
     * Lista los empleados que están activos (estado = true).
     * @return Una lista de empleados activos.
     */
    @Override
    @Transactional(readOnly = true) // La operación es solo de lectura
    public List<Empleado> listarEmpleadosActivos() {
        return empleadoRepository.findByEstado(true);
    }

    /**
     * Busca empleados por nombre o apellido (no sensible a mayúsculas/minúsculas).
     * @param busqueda La cadena de búsqueda.
     * @return Una lista de empleados que coinciden con la búsqueda.
     */
    @Override
    @Transactional(readOnly = true) // La operación es solo de lectura
    public List<Empleado> buscarPorNombreOApellido(String busqueda) {
        return empleadoRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(busqueda, busqueda);
    }

    /**
     * Desactiva un empleado cambiando su estado a falso y registrando la fecha de cese.
     * @param idEmpleado El ID del empleado a desactivar.
     */
    @Override
    @Transactional // La operación modifica la base de datos
    public void desactivarEmpleado(Integer idEmpleado) {
        empleadoRepository.findById(idEmpleado).ifPresent(empleado -> {
            empleado.setEstado(false);
            empleado.setFechaCese(LocalDate.now()); // Establecer fecha de cese
            empleado.setUpdatedAt(LocalDateTime.now());
            empleadoRepository.save(empleado);
        });
    }

    /**
     * Activa un empleado cambiando su estado a verdadero y eliminando la fecha de cese.
     * @param idEmpleado El ID del empleado a activar.
     */
    @Override
    @Transactional // La operación modifica la base de datos
    public void activarEmpleado(Integer idEmpleado) {
        empleadoRepository.findById(idEmpleado).ifPresent(empleado -> {
            empleado.setEstado(true);
            empleado.setFechaCese(null); // Eliminar fecha de cese
            empleado.setUpdatedAt(LocalDateTime.now());
            empleadoRepository.save(empleado);
        });
    }
}
