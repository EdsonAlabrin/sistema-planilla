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

    @Autowired // Inyección de dependencia del repositorio de Puesto
    private PuestoRepository puestoRepository;

    /**
     * Obtiene una lista de todas los puestos.
     * @return Una lista de objetos Puesto.
     */
    @Override
    public List<Puesto> getAllPuestos() {
        return puestoRepository.findAll();
    }

    /**
     * Obtiene un puesto por su ID.
     * @param id El ID del puesto.
     * @return Un Optional que contiene el Puesto si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<Puesto> getPuestoById(Integer idPuesto) {
        return puestoRepository.findById(idPuesto);
    }

    /**
     * Guarda un nuevo puesto o actualiza uno existente.
     * @param puesto El objeto Puesto a guardar/actualizar.
     * @return El Puesto guardado/actualizado.
     */
    @Override
    public Puesto savePuesto(Puesto puesto) {
        // Aquí se podría añadir lógica de negocio adicional antes de guardar,
        // por ejemplo, validaciones de salario o jornada laboral.
        return puestoRepository.save(puesto);
    }

    /**
     * Elimina un puesto por su ID.
     * @param id El ID del puesto a eliminar.
     */
    @Override
    public void deletePuesto(Integer idPuesto) {
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
