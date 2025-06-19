package com.textilima.textilima.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.textilima.textilima.entities.Descuento;
import com.textilima.textilima.repository.DescuentoRepository;

@Service
public class DescuentoService {

    @Autowired
    private DescuentoRepository descuentosRepository;

    public List<Descuento> listar() {
        return descuentosRepository.findAll();
    }

    public Descuento buscar(Integer id) {
        return descuentosRepository.findById(id).orElse(null);
    }

    public Descuento guardar(Descuento descuentos) {
        return descuentosRepository.save(descuentos);
    }

    public void eliminar(Integer id) {
        descuentosRepository.deleteById(id);
    }
}
