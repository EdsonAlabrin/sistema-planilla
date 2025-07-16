package com.textilima.textilima.service.impl; // Paquete para las implementaciones de servicio

import com.textilima.textilima.model.Banco;
import com.textilima.textilima.repository.BancoRepository;
import com.textilima.textilima.service.BancoService; // Importa la interfaz del servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class BancoServiceImpl implements BancoService { // Implementa la interfaz BancoService

    @Autowired // Inyección de dependencia del repositorio de Banco
    private BancoRepository bancoRepository;

    /**
     * Obtiene una lista de todos los bancos.
     * @return Una lista de objetos Banco.
     */
    @Override
    public List<Banco> getAllBancos() {
        return bancoRepository.findAll();
    }

    /**
     * Obtiene un banco por su ID.
     * @param id El ID del banco.
     * @return Un Optional que contiene el Banco si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<Banco> getBancoById(Integer id) {
        return bancoRepository.findById(id);
    }

    /**
     * Guarda un nuevo banco o actualiza uno existente.
     * @param banco El objeto Banco a guardar/actualizar.
     * @return El Banco guardado/actualizado.
     */
    @Override
    public Banco saveBanco(Banco banco) {
        // Aquí se podría añadir lógica de negocio adicional antes de guardar,
        // por ejemplo, validaciones de unicidad de nombre o código.
        return bancoRepository.save(banco);
    }

    /**
     * Elimina un banco por su ID.
     * @param id El ID del banco a eliminar.
     */
    @Override
    public void deleteBanco(Integer idBanco) {
        bancoRepository.deleteById(idBanco);
    }

    /**
     * Busca un banco por su nombre.
     * @param nombreBanco El nombre del banco.
     * @return Un Optional que contiene el Banco si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<Banco> getBancoByNombreBanco(String nombreBanco) {
        return bancoRepository.findByNombreBanco(nombreBanco);
    }

    /**
     * Busca un banco por su código.
     * @param codigoBanco El código del banco.
     * @return Un Optional que contiene el Banco si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<Banco> getBancoByCodigoBanco(String codigoBanco) {
        return Optional.ofNullable(bancoRepository.findByCodigoBanco(codigoBanco));
    }
}
