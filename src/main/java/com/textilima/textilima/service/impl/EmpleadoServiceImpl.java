package com.textilima.textilima.service.impl;

import com.textilima.textilima.entities.Banco;
import com.textilima.textilima.entities.Empleado; // Importa la entidad Empleado
import com.textilima.textilima.entities.Puesto; // Importa la entidad Puesto, ya que Empleado tiene una relación con Puesto
import com.textilima.textilima.repository.BancoRepository;
import com.textilima.textilima.repository.EmpleadoRepository; // Importa el repositorio de Empleado
import com.textilima.textilima.repository.PuestoRepository; // Importa el repositorio de Puesto
import com.textilima.textilima.service.EmpleadoService; // Importa la interfaz de servicio de Empleado

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime; // Para las fechas de creación/actualización
import java.util.List;
import java.util.Optional;

// Anotación que marca esta clase como un componente de servicio de Spring
@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    
    private final EmpleadoRepository empleadoRepository;
    private final PuestoRepository puestoRepository;
    private final BancoRepository bancoRepository;

    @Autowired
    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository, PuestoRepository puestoRepository, BancoRepository bancoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.puestoRepository = puestoRepository;
        this.bancoRepository = bancoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> listarTodosLosEmpleados() {
        List<Empleado> empleados = empleadoRepository.findAll();
        empleados.forEach(empleado -> {
            if (empleado.getPuesto() != null) {
                empleado.getPuesto().getNombrePuesto();
            }
            if (empleado.getBanco() != null) {
                empleado.getBanco().getNombreBanco();
            }
            if (empleado.getNumeroHijos() == null) {
                empleado.setNumeroHijos(0);
            }
        });
        return empleados;
    }

    @Override
    @Transactional(readOnly = true)
    public Empleado obtenerEmpleadoPorId(Integer id) {
        Optional<Empleado> empleadoOptional = empleadoRepository.findById(id);
        
        empleadoOptional.ifPresent(empleado -> {
            if (empleado.getPuesto() != null) {
                empleado.getPuesto().getNombrePuesto();
            }
            if (empleado.getBanco() != null) {
                empleado.getBanco().getNombreBanco();
            }
            if (empleado.getNumeroHijos() == null) {
                empleado.setNumeroHijos(0);
            }
        });
        
        return empleadoOptional.orElse(null);
    }

    @Override
    @Transactional
    public Empleado crearEmpleado(Empleado empleado) {
        if (empleado.getPuesto() != null && empleado.getPuesto().getIdPuesto() != null) {
            Puesto puesto = puestoRepository.findById(empleado.getPuesto().getIdPuesto())
                                .orElseThrow(() -> new IllegalArgumentException("Puesto no encontrado"));
            empleado.setPuesto(puesto);
        } else {
            throw new IllegalArgumentException("El empleado debe tener un puesto asignado.");
        }

        if (empleado.getBanco() != null && empleado.getBanco().getIdBanco() != null) {
            Banco banco = bancoRepository.findById(empleado.getBanco().getIdBanco())
                                .orElseThrow(() -> new IllegalArgumentException("Banco no encontrado"));
            empleado.setBanco(banco);
        } else {
            empleado.setBanco(null);
        }

        if (empleado.getNumeroHijos() == null) {
            empleado.setNumeroHijos(0);
        }
        
        empleado.setEstado(true);
        empleado.setCreatedAt(LocalDateTime.now());
        empleado.setUpdatedAt(LocalDateTime.now());
        return empleadoRepository.save(empleado);
    }

    @Override
    @Transactional
    public Empleado actualizarEmpleado(Integer id, Empleado empleadoDetalles) {
        Empleado empleadoExistente = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

        empleadoExistente.setNombres(empleadoDetalles.getNombres());
        empleadoExistente.setApellidos(empleadoDetalles.getApellidos());
        empleadoExistente.setTipoDocumento(empleadoDetalles.getTipoDocumento());
        empleadoExistente.setNumeroDocumento(empleadoDetalles.getNumeroDocumento());
        empleadoExistente.setFechaNacimiento(empleadoDetalles.getFechaNacimiento());
        empleadoExistente.setSexo(empleadoDetalles.getSexo());
        empleadoExistente.setEstadoCivil(empleadoDetalles.getEstadoCivil());
        empleadoExistente.setNacionalidad(empleadoDetalles.getNacionalidad());
        empleadoExistente.setCorreo(empleadoDetalles.getCorreo());
        empleadoExistente.setDireccionCompleta(empleadoDetalles.getDireccionCompleta());
        empleadoExistente.setDistrito(empleadoDetalles.getDistrito());
        empleadoExistente.setProvincia(empleadoDetalles.getProvincia());
        empleadoExistente.setDepartamento(empleadoDetalles.getDepartamento());
        empleadoExistente.setFechaIngreso(empleadoDetalles.getFechaIngreso());
        empleadoExistente.setEstado(empleadoDetalles.getEstado());
        empleadoExistente.setFechaCese(empleadoDetalles.getFechaCese());
        empleadoExistente.setRegimenLaboral(empleadoDetalles.getRegimenLaboral());
        empleadoExistente.setNumeroHijos(empleadoDetalles.getNumeroHijos() != null ? empleadoDetalles.getNumeroHijos() : 0); // Corregido: usar getNumeroHijos
        empleadoExistente.setSistemaPensiones(empleadoDetalles.getSistemaPensiones());
        empleadoExistente.setCodigoPension(empleadoDetalles.getCodigoPension());
        empleadoExistente.setNombreAfp(empleadoDetalles.getNombreAfp());
        empleadoExistente.setNumeroCuentaBanco(empleadoDetalles.getNumeroCuentaBanco());

        if (empleadoDetalles.getPuesto() != null && empleadoDetalles.getPuesto().getIdPuesto() != null) {
            Puesto puesto = puestoRepository.findById(empleadoDetalles.getPuesto().getIdPuesto())
                                .orElseThrow(() -> new IllegalArgumentException("Puesto no encontrado"));
            empleadoExistente.setPuesto(puesto);
        } else {
            throw new IllegalArgumentException("El empleado debe tener un puesto asignado.");
        }

        if (empleadoDetalles.getBanco() != null && empleadoDetalles.getBanco().getIdBanco() != null) {
            Banco banco = bancoRepository.findById(empleadoDetalles.getBanco().getIdBanco())
                                .orElseThrow(() -> new IllegalArgumentException("Banco no encontrado"));
            empleadoExistente.setBanco(banco);
        } else {
            empleadoExistente.setBanco(null);
        }

        empleadoExistente.setUpdatedAt(LocalDateTime.now());
        return empleadoRepository.save(empleadoExistente);
    }

    @Override
    @Transactional
    public void desactivarEmpleado(Integer id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));
        empleado.setEstado(false);
        empleado.setFechaCese(java.time.LocalDate.now());
        empleado.setUpdatedAt(LocalDateTime.now());
        empleadoRepository.save(empleado);
    }

    @Override
    @Transactional
    public void activarEmpleado(Integer id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));
        empleado.setEstado(true);
        empleado.setFechaCese(null);
        empleado.setUpdatedAt(LocalDateTime.now());
        empleadoRepository.save(empleado);
    }

    // --- MÉTODOS FALTANTES A IMPLEMENTAR ---

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> listarEmpleadosActivos() {
        return empleadoRepository.findByEstado(true); // Asumiendo que tienes este método en EmpleadoRepository
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> buscarPorNombreOApellido(String query) {
        // Implementación de búsqueda por nombre o apellido (case-insensitive)
        return empleadoRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(query, query); // Asumiendo este método en EmpleadoRepository
    }

    @Override
    @Transactional(readOnly = true)
    public Empleado obtenerEmpleadoPorDni(String dni) {
        // Implementación de búsqueda por número de documento (DNI)
        return empleadoRepository.findByNumeroDocumento(dni).orElse(null); // Asumiendo este método en EmpleadoRepository
    }
}
