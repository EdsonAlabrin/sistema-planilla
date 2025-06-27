package com.textilima.textilima.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.textilima.textilima.model.Boleta;
import com.textilima.textilima.model.DetallePlanilla;
import com.textilima.textilima.repository.BoletaRepository;
import com.textilima.textilima.service.BoletaService;

@Service
public class BoletaServiceImpl implements BoletaService {
    @Autowired // Injects the BoletaRepository dependency
    private BoletaRepository boletaRepository;

    /**
     * Retrieves a list of all payslips.
     * @return A list of Boleta objects.
     */
    @Override
    public List<Boleta> getAllBoletas() {
        return boletaRepository.findAll();
    }

    /**
     * Retrieves a payslip by its ID.
     * @param id The ID of the payslip.
     * @return An Optional containing the Boleta if found, or empty if not found.
     */
    @Override
    public Optional<Boleta> getBoletaById(Integer idBoleta) {
        return boletaRepository.findById(idBoleta);
    }

    /**
     * Saves a new payslip or updates an existing one.
     * @param boleta The Boleta object to save/update.
     * @return The saved/updated Boleta.
     */
    @Override
    public Boleta saveBoleta(Boleta boleta) {
        // Here you could add additional business logic before saving,
        // for example, generating the PDF and saving the path, or setting initial signed/sent status.
        return boletaRepository.save(boleta);
    }

    /**
     * Deletes a payslip by its ID.
     * @param id The ID of the payslip to delete.
     */
    @Override
    public void deleteBoleta(Integer idBoleta) {
        boletaRepository.deleteById(idBoleta);
    }

    /**
     * Searches for a payslip by its associated payroll detail.
     * @param detallePlanilla The payroll detail to which the payslip is associated.
     * @return An Optional containing the found Boleta, or empty if not found.
     */
    @Override
    public Optional<Boleta> getBoletaByDetallePlanilla(DetallePlanilla detallePlanilla) {
        return boletaRepository.findByDetallePlanilla(detallePlanilla);
    }

    /**
     * Searches for payslips issued within a date range.
     * @param fechaInicio The start date of the range (inclusive).
     * @param fechaFin The end date of the range (inclusive).
     * @return A list of Boleta issued within the specified range.
     */
    @Override
    public List<Boleta> getBoletasByFechaEmisionBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        return boletaRepository.findByFechaEmisionBetween(fechaInicio, fechaFin);
    }

    /**
     * Searches for payslips that have been signed.
     * @param firmada Boolean indicating whether the payslip is signed (true) or not (false).
     * @return A list of Boleta that meet the signed condition.
     */
    @Override
    public List<Boleta> getBoletasByFirmadaStatus(Boolean firmada) {
        return boletaRepository.findByFirmada(firmada);
    }

    /**
     * Searches for payslips that have been sent.
     * @param enviada Boolean indicating whether the payslip has been sent (true) or not (false).
     * @return A list of Boleta that meet the sent condition.
     */
    @Override
    public List<Boleta> getBoletasByEnviadaStatus(Boolean enviada) {
        return boletaRepository.findByEnviada(enviada);
    }
}
