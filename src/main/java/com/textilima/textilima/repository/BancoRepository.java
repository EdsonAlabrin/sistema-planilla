package com.textilima.textilima.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.Banco;


@Repository // Marca esta interfaz como un componente de repositorio de Spring
public interface BancoRepository extends JpaRepository<Banco, Integer> {
     /**
     * Busca un banco por su nombre.
     * @param nombreBanco El nombre del banco a buscar.
     * @return El banco encontrado o null si no existe.
     */
    Banco findByNombreBanco(String nombreBanco);

    /**
     * Busca un banco por su código.
     * @param codigoBanco El código del banco a buscar.
     * @return El banco encontrado o null si no existe.
     */
    Banco findByCodigoBanco(String codigoBanco);
}


