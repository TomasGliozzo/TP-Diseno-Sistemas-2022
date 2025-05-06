

package GeoDDSTests;

import domain.modelo.entities.Servicies.GeoDDS.Entities.*;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
import domain.modelo.entities.Servicies.GeoDDS.adapters.ServicioGeoDDSRetrofitAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class GeoTest {
    List<Pais> lP;
    List<Provincia> listaProvinciasArgentina;
    List<Municipio> listaMunicipiosDeBuenosAires;
    List<Localidad> listaLocalidadesCampana;
    Distancia distanciaCalleMaipuACalleOHiggings;

    @BeforeEach
    public void init() throws IOException {
        ServicioDistancia servicioGeoDDS = ServicioDistancia.instancia();
        servicioGeoDDS.setAdapter(new ServicioGeoDDSRetrofitAdapter());
        lP = servicioGeoDDS.listaPaises();
        listaProvinciasArgentina = servicioGeoDDS.listaProvincias(9);
        listaMunicipiosDeBuenosAires = servicioGeoDDS.listaMunicipios(168);
        listaLocalidadesCampana = servicioGeoDDS.listaLocalidades(347);
    }

    @Test
    @DisplayName("Listado Paises")
    public void listadoPaises() {
        for (Pais pais : lP) {
            System.out.println(pais.nombre);
        }
        Assertions.assertTrue(true);

    }

    @Test
    @DisplayName("Listado Provincias de Argentina")
    public void listadoProvincias() {
        for (Provincia provincia : listaProvinciasArgentina) {
            System.out.println(provincia.nombre);
        }
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Listado Municipios de Buenos Aires")
    public void listadoMunicipiosBsAs() {
        for (Municipio municipio : listaMunicipiosDeBuenosAires) {
            System.out.println(municipio.nombre);
        }
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Listado localidades del municipio Campana de Buenos Aires")
    public void listadoLocalidadesCampana() {
        for (Localidad localidad : listaLocalidadesCampana) {
            System.out.println(localidad.nombre);
        }
        Assertions.assertTrue(true);
    }
}
