package domain.modelo.entities.Servicies.GeoDDS.adapters;

import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.Servicies.GeoDDS.Entities.*;

import java.io.IOException;
import java.util.List;

public interface APIDistanciaAdapter {
    List<Pais> listaPaises() throws IOException;
    List<Provincia> listaProvincias(int paisId) throws IOException;
    List<Municipio> listaMunicipios(int provinciaId) throws IOException;
    List<Localidad> listaLocalidades(int municipioId) throws IOException;
    Distancia distancia(Direccion direccionO, Direccion direccionD) throws IOException;
}
