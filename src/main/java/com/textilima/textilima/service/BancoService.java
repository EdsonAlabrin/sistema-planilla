package com.textilima.textilima.service;

import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.Banco;

public interface BancoService {
    /**
     * Obtiene una lista de todos los bancos.
     * @return Una lista de objetos Banco.
     */
    List<Banco> getAllBancos();

    /**
     * Obtiene un banco por su ID.
     * @param id El ID del banco.
     * @return Un Optional que contiene el Banco si se encuentra, o vacío si no existe.
     */
    Optional<Banco> getBancoById(Integer idBanco);

    /**
     * Guarda un nuevo banco o actualiza uno existente.
     * @param banco El objeto Banco a guardar/actualizar.
     * @return El Banco guardado/actualizado.
     */
    Banco saveBanco(Banco banco);

    /**
     * Elimina un banco por su ID.
     * @param id El ID del banco a eliminar.
     */
    void deleteBanco(Integer idBanco);

    /**
     * Busca un banco por su nombre.
     * @param nombreBanco El nombre del banco.
     * @return Un Optional que contiene el Banco si se encuentra, o vacío si no existe.
     */
    Optional<Banco> getBancoByNombreBanco(String nombreBanco);

    /**
     * Busca un banco por su código.
     * @param codigoBanco El código del banco.
     * @return Un Optional que contiene el Banco si se encuentra, o vacío si no existe.
     */
    Optional<Banco> getBancoByCodigoBanco(String codigoBanco);
}
