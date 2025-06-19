package com.textilima.textilima.service.impl;

import com.textilima.textilima.entities.Puesto; // Importa la entidad Puesto
import com.textilima.textilima.repository.PuestoRepository; // Importa el repositorio de Puesto
import com.textilima.textilima.service.PuestoService; // Importa la interfaz de servicio de Puesto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // Para las fechas de creación/actualización
import java.util.List;
// Anotación que marca esta clase como un componente de servicio de Spring
@Service
public class PuestoServiceImpl implements PuestoService {

    private final PuestoRepository puestoRepository;

    // Inyección de dependencias a través del constructor
    @Autowired
    public PuestoServiceImpl(PuestoRepository puestoRepository) {
        this.puestoRepository = puestoRepository;
    }

    /**
     * Crea un nuevo puesto en la base de datos.
     *
     * @param puesto El objeto Puesto a guardar.
     * @return El puesto guardado.
     */
    @Override
    @Transactional // La operación modifica la base de datos
    public Puesto crearPuesto(Puesto puesto) {
        // Antes de guardar, asegúrate que las fechas created_at y updated_at se seteen si es una nueva creación
        if (puesto.getCreatedAt() == null) {
            puesto.setCreatedAt(LocalDateTime.now());
        }
        puesto.setUpdatedAt(LocalDateTime.now()); // Siempre actualizar updated_at
        return puestoRepository.save(puesto);
    }

    /**
     * Actualiza la información de un puesto existente.
     *
     * @param idPuesto El ID del puesto a actualizar.
     * @param puesto Los nuevos datos del puesto.
     * @return El puesto actualizado.
     * @throws RuntimeException si el puesto no se encuentra.
     */
    @Override
    @Transactional // La operación modifica la base de datos
    public Puesto actualizarPuesto(Integer idPuesto, Puesto puesto) {
        return puestoRepository.findById(idPuesto)
                .map(puestoExistente -> {
                    puestoExistente.setNombrePuesto(puesto.getNombrePuesto());
                    puestoExistente.setSalarioBase(puesto.getSalarioBase());
                    puestoExistente.setJornadaLaboralHoras(puesto.getJornadaLaboralHoras());
                    puestoExistente.setUpdatedAt(LocalDateTime.now()); // Actualizar fecha de modificación
                    return puestoRepository.save(puestoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Puesto no encontrado con ID: " + idPuesto));
    }

    /**
     * Obtiene un puesto por su ID.
     *
     * @param idPuesto El ID del puesto.
     * @return El puesto si existe, o null si no se encuentra.
     */
    @Override
    @Transactional(readOnly = true) // La operación es solo de lectura
    public Puesto obtenerPuestoPorId(Integer idPuesto) {
        return puestoRepository.findById(idPuesto).orElse(null);
    }

    /**
     * Lista todos los puestos en la base de datos.
     *
     * @return Una lista de todos los puestos.
     */
    @Override
    @Transactional(readOnly = true) // La operación es solo de lectura
    public List<Puesto> listarTodosLosPuestos() {
        return puestoRepository.findAll();
    }

    /**
     * Elimina un puesto por su ID.
     * (Considera si un borrado lógico (cambiar estado) es más apropiado que un borrado físico en un entorno de producción).
     *
     * @param idPuesto El ID del puesto a eliminar.
     */
    @Override
    @Transactional // La operación modifica la base de datos
    public void eliminarPuesto(Integer idPuesto) {
        if (!puestoRepository.existsById(idPuesto)) {
            throw new RuntimeException("Puesto con ID " + idPuesto + " no encontrado para eliminar.");
        }
        puestoRepository.deleteById(idPuesto);
    }

    /**
     * Obtiene un puesto por su nombre.
     * @param nombrePuesto El nombre del puesto.
     * @return El puesto si existe, o null si no se encuentra.
     */
    @Override
    @Transactional(readOnly = true)
    public Puesto obtenerPuestoPorNombre(String nombrePuesto) {
        return puestoRepository.findByNombrePuesto(nombrePuesto).orElse(null);
    }
}
