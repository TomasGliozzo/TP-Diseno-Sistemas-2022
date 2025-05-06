package GeoDDSTests;

import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Distancia;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Municipio;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Pais;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Provincia;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
import domain.modelo.entities.Servicies.GeoDDS.adapters.APIDistanciaAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class GeoDDSServiceTest {
    private ServicioDistancia servicioGeoDDS;
    private APIDistanciaAdapter adapterMock;

    @BeforeEach
    public void init() {
        this.adapterMock = mock(APIDistanciaAdapter.class);
        this.servicioGeoDDS = ServicioDistancia.instancia();
        this.servicioGeoDDS.setAdapter(this.adapterMock);
    }

    @Test
    public void servicioDDSProveeListadoPaisesTest() throws IOException {
        List<Pais> paises = this.paisesMock();
        when(this.adapterMock.listaPaises()).thenReturn(paises);
        Assertions.assertEquals(2, this.servicioGeoDDS.listaPaises().size());
    }

    private List<Pais> paisesMock() {
        List<Pais> paises = new ArrayList<>();
        paises.add(new Pais(1, "Argentina"));
        paises.add(new Pais(2, "Chile"));
        return paises;
    }

    @Test
    public void servicioDDSProveeListadoProvinciasTest() throws IOException {
        Pais argentina = new Pais(1, "Argentina");
        List<Provincia> provinciasMock = this.provinciasMock();
        when(this.adapterMock.listaProvincias(argentina.id)).thenReturn(provinciasMock);
        Assertions.assertEquals(2, this.servicioGeoDDS.listaProvincias(argentina.id).size());
    }

    private List<Provincia> provinciasMock() {
        List<Provincia> provincias = new ArrayList<>();
        provincias.add(new Provincia(1, "Bs As"));
        provincias.add(new Provincia(2, "Cordoba"));
        return provincias;
    }

    @Test
    public void servicioDDSProveeListadoMunicipiosTest() throws IOException {
        Provincia bsas = new Provincia(1, "Bs As");
        List<Municipio> municipiosMock = this.municipiosMock();
        when(this.adapterMock.listaMunicipios(bsas.id)).thenReturn(municipiosMock);
        Assertions.assertEquals(2, this.servicioGeoDDS.listaMunicipios(bsas.id).size());
    }

    private List<Municipio> municipiosMock() {
        Provincia bsas = new Provincia(1, "Bs As");
        List<Municipio> municipios = new ArrayList<>();
        municipios.add(new Municipio("La Plata", 1, bsas));
        municipios.add(new Municipio("Quilmes", 2, bsas));
        return municipios;
    }

    @Test
    public void servicioDDSProveeDistanciaTest() throws IOException {
        Direccion d1 = new Direccion("Av. Juan Bautista Alberdi","900",1,"CABA");
        Direccion d2 = new Direccion("Av. Corrientes","3500",1,"CABA");

        Distancia distanciaMock = this.distanciaMock();
        when(this.adapterMock.distancia(d1, d2)).thenReturn(distanciaMock);

        Assertions.assertEquals(10.0, this.servicioGeoDDS.distancia(d1, d2).valor);
        Assertions.assertEquals("KM", servicioGeoDDS.distancia(d1, d2).unidad);
    }

    private Distancia distanciaMock (){
        return new Distancia(10.0, "KM");
    }
}
