package com.textilima.textilima.service.impl;

import com.textilima.textilima.model.Puesto;
import com.textilima.textilima.repository.PuestoRepository; // Importa el repositorio de Puesto
import com.textilima.textilima.service.PuestoService; // Importa la interfaz de servicio de Puesto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
// Anotación que marca esta clase como un componente de servicio de Spring
@Service
public class PuestoServiceImpl implements PuestoService {

   @Autowired
    private PuestoRepository puestoRepository;

    /**
     * Obtiene una lista de todos los puestos.
     * @return Una lista de objetos Puesto.
     */
    @Override
    public List<Puesto> listarTodosLosPuestos() {
        return puestoRepository.findAll();
    }

    /**
     * Obtiene un puesto por su ID.
     * @param id El ID del puesto.
     * @return Un Optional que contiene el Puesto si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<Puesto> obtenerPuestoPorId(Integer idPuesto) {
        return puestoRepository.findById(idPuesto);
    }

    /**
     * Guarda un nuevo puesto.
     * @param puesto El objeto Puesto a guardar.
     * @return El Puesto guardado.
     */
    @Override
    public Puesto crearPuesto(Puesto puesto) {
        return puestoRepository.save(puesto);
    }

    /**
     * Actualiza un puesto existente.
     * @param id El ID del puesto a actualizar.
     * @param puesto Detalles del puesto para actualizar.
     * @return El Puesto actualizado.
     */
    @Override
    public Puesto actualizarPuesto(Integer idPuesto, Puesto puesto) {
        Optional<Puesto> existingPuesto = puestoRepository.findById(idPuesto);
        if (existingPuesto.isPresent()) {
            Puesto updatedPuesto = existingPuesto.get();
            updatedPuesto.setNombrePuesto(puesto.getNombrePuesto());
            updatedPuesto.setSalarioBase(puesto.getSalarioBase());
            updatedPuesto.setJornadaLaboralHoras(puesto.getJornadaLaboralHoras());
            updatedPuesto.setHoraInicioJornada(puesto.getHoraInicioJornada()); // Actualizar nueva propiedad
            updatedPuesto.setHoraFinJornada(puesto.getHoraFinJornada());     // Actualizar nueva propiedad
            updatedPuesto.setDescripcion(puesto.getDescripcion());
            return puestoRepository.save(updatedPuesto);
        } else {
            throw new RuntimeException("Puesto con ID " + idPuesto + " no encontrado para actualización.");
        }
    }

    /**
     * Elimina un puesto por su ID.
     * @param id El ID del puesto a eliminar.
     */
    @Override
    public void eliminarPuesto(Integer idPuesto) {
        puestoRepository.deleteById(idPuesto);
    }

    /**
     * Busca un puesto por su nombre.
     * @param nombrePuesto El nombre del puesto.
     * @return Un Optional que contiene el Puesto si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<Puesto> getPuestoByNombrePuesto(String nombrePuesto) {
        return Optional.ofNullable(puestoRepository.findByNombrePuesto(nombrePuesto));
    }

    /**
     * Obtiene todos los puestos con un salario base mayor o igual al valor especificado.
     * @param salarioBase El salario base mínimo.
     * @return Una lista de puestos que cumplen con la condición.
     */
    @Override
    public List<Puesto> getPuestosBySalarioBaseGreaterThanEqual(BigDecimal salarioBase) {
        return puestoRepository.findBySalarioBaseGreaterThanEqual(salarioBase);
    }
}
