package com.textilima.textilima.service;

import java.util.List;
import java.util.Optional;

import com.textilima.textilima.entities.Banco;

public interface BancoService {
    List<Banco> findAll(); // Obtiene todos los bancos

    Optional<Banco> findById(Integer id); // Busca un banco por su ID

    Banco save(Banco banco); // Guarda un nuevo banco o actualiza uno existente

    void deleteById(Integer id); // Elimina un banco por su ID

    Optional<Banco> findByNombreBanco(String nombreBanco); // Busca un banco por su nombre
}
