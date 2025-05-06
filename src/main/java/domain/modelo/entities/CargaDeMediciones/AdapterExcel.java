package domain.modelo.entities.CargaDeMediciones;

import domain.modelo.entities.Mediciones.Medicion;

import java.io.InputStream;
import java.util.List;

public interface AdapterExcel {
    List<Medicion> obtenerMediciones(String nombreArchivo);
    List<Medicion> obtenerMediciones(InputStream inputStream);
}
