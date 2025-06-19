package com.textilima.textilima.service.impl;

import com.textilima.textilima.entities.Bono;
import com.textilima.textilima.entities.Descuento;
import com.textilima.textilima.entities.DetallePlanilla;
import com.textilima.textilima.repository.BonoRepository;
import com.textilima.textilima.repository.DescuentoRepository;
import com.textilima.textilima.repository.DetallePlanillaRepository;
import com.textilima.textilima.service.DetallePlanillaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DetallePlanillaServiceImpl implements DetallePlanillaService {
    
   private final DetallePlanillaRepository detallePlanillaRepository;
    private final BonoRepository bonoRepository; // Inyectar BonoRepository
    private final DescuentoRepository descuentoRepository; // Inyectar DescuentoRepository

    @Autowired
    public DetallePlanillaServiceImpl(DetallePlanillaRepository detallePlanillaRepository,
                                      BonoRepository bonoRepository,
                                      DescuentoRepository descuentoRepository) {
        this.detallePlanillaRepository = detallePlanillaRepository;
        this.bonoRepository = bonoRepository;
        this.descuentoRepository = descuentoRepository;
    }

    @Override
    @Transactional
    public DetallePlanilla save(DetallePlanilla detallePlanilla) {
        return detallePlanillaRepository.save(detallePlanilla);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DetallePlanilla> findById(Integer id) {
        return detallePlanillaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePlanilla> findAll() {
        List<DetallePlanilla> detalles = detallePlanillaRepository.findAll();
        // Cargar explícitamente las relaciones (bonos, descuentos, empleado, planilla)
        detalles.forEach(detalle -> {
            if (detalle.getBonos() != null) detalle.getBonos().size(); // Forzar carga
            if (detalle.getDescuentos() != null) detalle.getDescuentos().size(); // Forzar carga
            if (detalle.getEmpleado() != null) detalle.getEmpleado().getNombres(); // Forzar carga
            if (detalle.getPlanilla() != null) detalle.getPlanilla().getMes(); // Forzar carga
        });
        return detalles;
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        detallePlanillaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePlanilla> findByPlanillaId(Integer idPlanilla) {
        List<DetallePlanilla> detalles = detallePlanillaRepository.findByPlanilla_IdPlanilla(idPlanilla);
        // Cargar explícitamente las relaciones
        detalles.forEach(detalle -> {
            if (detalle.getBonos() != null) detalle.getBonos().size();
            if (detalle.getDescuentos() != null) detalle.getDescuentos().size();
            if (detalle.getEmpleado() != null) detalle.getEmpleado().getNombres();
            if (detalle.getPlanilla() != null) detalle.getPlanilla().getMes();
        });
        return detalles;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePlanilla> findByEmpleadoId(Integer idEmpleado) {
        List<DetallePlanilla> detalles = detallePlanillaRepository.findByEmpleado_IdEmpleado(idEmpleado);
        // Cargar explícitamente las relaciones
        detalles.forEach(detalle -> {
            if (detalle.getBonos() != null) detalle.getBonos().size();
            if (detalle.getDescuentos() != null) detalle.getDescuentos().size();
            if (detalle.getEmpleado() != null) detalle.getEmpleado().getNombres();
            if (detalle.getPlanilla() != null) detalle.getPlanilla().getMes();
        });
        return detalles;
    }

    @Override
    @Transactional
    public DetallePlanilla agregarBonoADetalle(Integer idDetalle, Bono bono) {
        DetallePlanilla detalle = detallePlanillaRepository.findById(idDetalle)
            .orElseThrow(() -> new RuntimeException("DetallePlanilla no encontrado con ID: " + idDetalle));
        
        bono.setDetallePlanilla(detalle);
        bonoRepository.save(bono); // Guardar el bono
        
        // Vuelve a calcular el total de bonos en el detalle
        detalle.getBonos().add(bono); // Añadir el nuevo bono a la colección existente (si está mapeada)
        detalle.setTotalBonos(detalle.getBonos().stream()
                                .map(Bono::getMonto)
                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        
        // Recalcular sueldo neto si los bonos afectan
        detalle.setSueldoNeto(detalle.getSueldoBruto().subtract(detalle.getTotalDescuentos()).add(detalle.getTotalBonos()));
        
        return detallePlanillaRepository.save(detalle); // Guardar el detalle actualizado
    }

    @Override
    @Transactional
    public DetallePlanilla agregarDescuentoADetalle(Integer idDetalle, Descuento descuento) {
        DetallePlanilla detalle = detallePlanillaRepository.findById(idDetalle)
            .orElseThrow(() -> new RuntimeException("DetallePlanilla no encontrado con ID: " + idDetalle));
        
        descuento.setDetallePlanilla(detalle);
        descuentoRepository.save(descuento); // Guardar el descuento
        
        // Vuelve a calcular el total de descuentos en el detalle
        detalle.getDescuentos().add(descuento); // Añadir el nuevo descuento a la colección existente (si está mapeada)
        detalle.setTotalDescuentos(detalle.getDescuentos().stream()
                                .map(Descuento::getMonto)
                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));

        // Recalcular sueldo neto si los descuentos afectan
        detalle.setSueldoNeto(detalle.getSueldoBruto().subtract(detalle.getTotalDescuentos()).add(detalle.getTotalBonos()));

        return detallePlanillaRepository.save(detalle); // Guardar el detalle actualizado
    }

    @Override
    @Transactional
    public DetallePlanilla generarBoleta(Integer idDetalle) {
        DetallePlanilla detalle = detallePlanillaRepository.findById(idDetalle)
            .orElseThrow(() -> new RuntimeException("DetallePlanilla no encontrado con ID: " + idDetalle));
        
        // Aquí iría la lógica para generar el archivo PDF de la boleta.
        // Por ahora, solo marcamos que se ha "generado" (ej. en una propiedad de DetallePlanilla si existiera)
        // O simplemente retornamos el detalle, asumiendo que un servicio externo maneja la generación.
        // Si tienes una entidad Boleta, la crearías aquí y la asociarías al DetallePlanilla.
        System.out.println("Lógica para generar boleta para DetallePlanilla ID: " + idDetalle);
        // Puedes añadir una propiedad 'boletaGenerada' o similar en DetallePlanilla si la necesitas.

        return detalle; // Retorna el detalle, posiblemente actualizado con un estado de boleta generada.
    }
}
