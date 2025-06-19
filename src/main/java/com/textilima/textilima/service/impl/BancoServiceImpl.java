package com.textilima.textilima.service.impl; // Paquete para las implementaciones de servicio

import com.textilima.textilima.entities.Banco;
import com.textilima.textilima.repository.BancoRepository;
import com.textilima.textilima.service.BancoService; // Importa la interfaz del servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Para manejar transacciones de base de datos

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class BancoServiceImpl implements BancoService { // Implementa la interfaz BancoService

    private final BancoRepository bancoRepository; // Inyección del repositorio de Banco

    @Autowired // Constructor para inyección de dependencias
    public BancoServiceImpl(BancoRepository bancoRepository) {
        this.bancoRepository = bancoRepository;
    }

    @Override
    @Transactional(readOnly = true) // La operación de lectura no modifica la base de datos
    public List<Banco> findAll() {
        return bancoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Banco> findById(Integer id) {
        return bancoRepository.findById(id);
    }

    @Override
    @Transactional // La operación de guardar/actualizar modifica la base de datos
    public Banco save(Banco banco) {
        // En una aplicación real, aquí podrías añadir validaciones adicionales
        // Por ejemplo, verificar si el nombre del banco o código ya existen antes de guardar
        return bancoRepository.save(banco);
    }

    @Override
    @Transactional // La operación de eliminar modifica la base de datos
    public void deleteById(Integer id) {
        bancoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Banco> findByNombreBanco(String nombreBanco) {
        return bancoRepository.findByNombreBanco(nombreBanco);
    }
}
