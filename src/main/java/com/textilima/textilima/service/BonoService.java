package com.textilima.textilima.service;

import java.util.List;
import com.textilima.textilima.entities.Bono;

public interface BonoService {
    List<Bono> listarBonos();
    Bono obtenerBonoPorId(Integer idBono);
    Bono guardarBono(Bono bonos);
    void eliminarBono(Integer idBono);
}
