package SectorTerritorial;

import Factory.Entidades.EntidadesFactory;
import Factory.MedioTransporte.MedioTransporteFactory;
import Factory.MedioTransporte.TipoTramo;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.Mediciones.Unidad;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Trayecto;
import domain.modelo.entities.MedioTransporte.ServicioContratado.ServicioContratado;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.VehiculoParticular;
import domain.modelo.entities.SectorTerritorial.AgenteSectorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialMunicipal;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialProvincial;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Distancia;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
import domain.modelo.entities.Servicies.GeoDDS.adapters.APIDistanciaAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SectorTerritorialTest {
    private SectorTerritorialMunicipal sTM1,sTM2;
    private SectorTerritorialProvincial sTP;
    private AgenteSectorial aSM1,aSM2,aSP;
    private Organizacion org1,org2;

    private Miembro m1,m2;
    private Trayecto trayecto;
    private Tramo tramo1,tramo2;
    private VehiculoParticular transporteVP;
    private ServicioContratado transporteSC;
    private FE fe;
    private Distancia distancia = new Distancia (300.0, "M");
    ServicioDistancia servicioGeoDDS;
    APIDistanciaAdapter adapterMock;

    @BeforeEach
    public void init(){
        // API Distancia
        adapterMock = mock(APIDistanciaAdapter.class);
        servicioGeoDDS = ServicioDistancia.instancia();
        servicioGeoDDS.setAdapter(adapterMock);
        try {
            when(adapterMock.distancia(Mockito.any(Direccion.class), Mockito.any(Direccion.class))).thenReturn(distancia);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //MIEMBROS (con Trayectos, Tramos y MDT)
        trayecto = MedioTransporteFactory.instanciarTrayectoConUnTramo(TipoTramo.TRAMO_VEHICULO_PARTICULAR);
        tramo1 = trayecto.getTramos().get(0);
        transporteVP = (VehiculoParticular) tramo1.getMedioTransporteUtilizado();
        fe = new FE(5.0, Unidad.lts);
        transporteVP.setServicioDistancia(servicioGeoDDS);
        transporteVP.setFe(fe);

        tramo2 = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_SERVICIO_CONTRATADO);
        transporteSC = (ServicioContratado) tramo2.getMedioTransporteUtilizado();
        transporteSC.setServicioDistancia(servicioGeoDDS);
        trayecto.agregarTramo(tramo2);

        m1 = EntidadesFactory.crearMiembroJose();
        m2 = EntidadesFactory.crearMiembroMariela();

        m1.agregarTrayecto(trayecto);
        m2.agregarTrayecto(trayecto);

        //DOS ORGANIZACIONES
        org1 = EntidadesFactory.crearOrganizacionMostaza();
        org1.vincularMiembro(m1.solicitarAdhesion(org1.getSectores().get(0),org1));

        org2 = EntidadesFactory.crearOrganizacionBurgerKing();
        org2.vincularMiembro(m1.solicitarAdhesion(org2.getSectores().get(0),org2));
        org2.vincularMiembro(m2.solicitarAdhesion(org2.getSectores().get(1),org2));

        //DOS SECTORES DEPARTAMENTALES Y UNO PROVINCIAL -> 3 AGENTES SECTORIALES
        sTM1 = new SectorTerritorialMunicipal("Municipio A");
        sTM1.agregarOrganizacion(org1);
        sTM2 = new SectorTerritorialMunicipal("Municipio B");
        sTM2.agregarOrganizacion(org2);
        sTP = new SectorTerritorialProvincial("Abarca los Municipios de A-Z");
        sTP.agregarSector(sTM1);
        sTP.agregarSector(sTM2);
        aSM1 = new AgenteSectorial(sTM1);
        aSM2 = new AgenteSectorial(sTM2);
        aSP = new AgenteSectorial(sTP);
    }
    @Test
    @DisplayName("Un Agente Sectorial Pertenece a un Sector Territorial")
    public void a(){
        Assertions.assertNotNull(aSM1.getSectorTerritorial());
        Assertions.assertNotNull(aSP.getSectorTerritorial());
    }
    @Test
    @DisplayName("Un Sector Territorial Departamental contiene una lista de organizaciones afectadas por el sector")
    public void b(){
        Assertions.assertNotNull(sTM1.getOrganizaciones());
        Assertions.assertNotNull(sTM2.getOrganizaciones());
    }
    @Test
    @DisplayName("Un Sector Territorial Provincial contiene una lista de Sectores Departamentales afectadas por el sector")
    public void c(){
        List<SectorTerritorialMunicipal> sectoresMunicipalesAfectados = new ArrayList<>();
        Collections.addAll(sectoresMunicipalesAfectados,sTM1,sTM2);

        Assertions.assertEquals(sectoresMunicipalesAfectados,sTP.getSectoresTerritoriales());
    }
    @Test
    @DisplayName("Un Sector Territorial Departamental puede obtener la HC de las Organizaciones afectadas")
    public void d(){
        Assertions.assertEquals(28800.0,sTM1.calcularHC(Periodicidad.MENSUAL,"07/2022"));
        Assertions.assertEquals(57600.0,sTM2.calcularHC(Periodicidad.MENSUAL,"07/2022"));
    }
    @Test
    @DisplayName("Un Sector Territorial Provincial puede obtener la HC de los Sectores Territoriales Departamentales afectados")
    public void e(){
        Assertions.assertEquals(86400.0,sTP.calcularHC(Periodicidad.MENSUAL,"07/2022"));

    }
}
