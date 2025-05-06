package HuellaDeCarbono;

import Factory.Entidades.EntidadesFactory;
import Factory.MedioTransporte.MedioTransporteFactory;
import Factory.MedioTransporte.TipoTramo;
import domain.modelo.entities.CargaDeMediciones.RegistrarMediciones;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;
import domain.modelo.entities.Mediciones.Unidad;
import domain.modelo.entities.MedioTransporte.MedioTransporte;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Trayecto;
import domain.modelo.entities.MedioTransporte.TransportePublico.Parada;
import domain.modelo.entities.MedioTransporte.TransportePublico.TransportePublico;
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
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class HCTrasladoMiembrosOrganizacionTest {
    private Trayecto trayecto1,trayecto2;
    private Direccion dISC, dFSC,dIS, dFS;
    private Tramo tramoServicioContratado, tramoTransportePublico, tramoTransporteEcologico,tramoVehiculoParticular;
    private TransportePublico transportePublico;
    private Parada pI,pInt,pF;
    private Miembro miembro,miembro2;
    private Organizacion organizacion;

    private Sector sector;
    private MedioTransporte transporteServicioParticular,transporteVehiculoParticular;

    private FE FE;
    private RegistrarMediciones registrarMediciones;
    private List<TipoActividad> tipoActividades = new ArrayList<>();

    ServicioDistancia servicioGeoDDS;
    APIDistanciaAdapter adapterMock;

    private Distancia distancia = new Distancia (300.0, "M");

    @BeforeEach
    public void init() throws IOException {
            // API Distancia
            adapterMock = mock(APIDistanciaAdapter.class);
            servicioGeoDDS = ServicioDistancia.instancia();
            servicioGeoDDS.setAdapter(adapterMock);
            when(adapterMock.distancia(Mockito.any(Direccion.class), Mockito.any(Direccion.class))).thenReturn(distancia);

            //MIEMBRO
            miembro = EntidadesFactory.crearMiembroJose();
            miembro2 = EntidadesFactory.crearMiembroMariela();
            //ORGANIZACION
            organizacion = EntidadesFactory.crearOrganizacionMostaza();
            //SECTOR
            sector = organizacion.getSectores().get(0);

            organizacion.vincularMiembro(miembro.solicitarAdhesion(sector, organizacion));
            organizacion.vincularMiembro(miembro2.solicitarAdhesion(sector,organizacion));

            //FE
            FE = new FE(5.0, Unidad.lts);

            //TRAYECTO1 y TRAMO SERVICIO CONTRATADO(INICIO) HC=44000.0 (1500 +0 + 9500) * 4 * 1
            trayecto1 = MedioTransporteFactory.instanciarTrayectoConUnTramo(TipoTramo.TRAMO_SERVICIO_CONTRATADO);
            tramoServicioContratado = trayecto1.getTramos().get(0);
            dISC = tramoServicioContratado.getDireccionInicio();
            dFSC = tramoServicioContratado.getDireccionFin();
            transporteServicioParticular = tramoServicioContratado.getMedioTransporteUtilizado();
            transporteServicioParticular.setServicioDistancia(servicioGeoDDS);
            transporteServicioParticular.setFe(FE);

            //TRANSPORTE PUBLICO
            tramoTransportePublico = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_TRANSPORTE_PUBLICO);
            transportePublico = (TransportePublico) tramoTransportePublico.getMedioTransporteUtilizado();
            transportePublico.setServicioDistancia(servicioGeoDDS);
            transportePublico.setFe(FE);

            pI = MedioTransporteFactory.instanciarParada(1);
            pInt = MedioTransporteFactory.instanciarParada(2);
            pF = MedioTransporteFactory.instanciarParada(3);

            dIS = pI.getDireccion();
            dFS = pF.getDireccion();

            transportePublico.agregarParada(pI);
            transportePublico.agregarParada(pInt);
            transportePublico.agregarParada(pF);
            tramoTransportePublico.setDireccionInicio(dIS);
            tramoTransportePublico.setDireccionFin(dFS);
            //AGREGO TRAMOS TP1 y TP2
            tramoTransporteEcologico = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_TRANSPORTE_ECOLOGICO);
            trayecto1.agregarTramo(tramoTransportePublico);
            trayecto1.agregarTramo(tramoTransporteEcologico);
            trayecto1.setFrecuenciaDeUsoSemanal(1);

            //Trayecto2 y Tramo Vehiculo Particular HC=18000.0 ( 1500.0 * 4 * 3)
            trayecto2 = MedioTransporteFactory.instanciarTrayectoConUnTramo(TipoTramo.TRAMO_VEHICULO_PARTICULAR);
            trayecto2.setFrecuenciaDeUsoSemanal(3);
            tramoVehiculoParticular = trayecto2.getTramos().get(0);
            transporteVehiculoParticular = tramoVehiculoParticular.getMedioTransporteUtilizado();
            transporteVehiculoParticular.setServicioDistancia(servicioGeoDDS);
            transporteVehiculoParticular.setFe(FE);
    }

    @Test
    @DisplayName("Se puede calcular la HC de un tramo recibido con Servicio Distancia")
    public void sePuedeCalcularLaHCDeUnTramoSDTest(){
        Assertions.assertEquals(1500,tramoServicioContratado.getHC());
    }
    @Test
    @DisplayName("Se puede calcular la HC de un tramo recibido con Transporte Publico")
    public void sePuedeCalcularLaHCDeUnTramoTPTest(){
        Assertions.assertEquals(9500.0,tramoTransportePublico.getHC());
    }

    @Test
    @DisplayName("La HC de un tramo ecologico es 0")
    public void hcTramoEcologicoTest(){
        Assertions.assertEquals(0,tramoTransporteEcologico.getHC());
    }

    @Test
    @DisplayName("Un trayecto puede devolver la sumatoria de la HC de todos sus Tramos")
    public void hcTotalDeLosTrayectosTest(){
        Assertions.assertEquals(44000.0, trayecto1.calcularHCTrayecto(Periodicidad.MENSUAL));
    }


    @Test
    @DisplayName("Un miembro, dada una Periodicidad y un periodo de imputación, puede devolver la sumatoria de la HC de los Trayectos afectados")
    public void hcTotalDeUnMiembroTest(){
        miembro.agregarTrayecto(trayecto1);
        miembro.agregarTrayecto(trayecto2);
        Assertions.assertEquals(806000.0,miembro.calcularHC(Periodicidad.ANUAL, "07/2022"));
    }

    @Test
    @DisplayName("Una Organizacion, dada una Periodicidad y un periodo de imputacion, puede devolver la sumatoria de la HC de todos los Trayectos afectados de sus Miembros")
    public void hcTotalDeUnaOrganizacionTest(){
        miembro2.agregarTrayecto(trayecto1); //HC: 44000.0
        miembro.agregarTrayecto(trayecto2); //HC: 18000.0
        Assertions.assertEquals(62000.0, organizacion.calcularHCMiembros(Periodicidad.MENSUAL, "07/2022"));
    }

    @Test
    @DisplayName("Cada sector de la Organización permite visualizar un indicador de HC sobre el total de sus miembros")
    public void indicadorDeHCDeUnSectorTest(){
        miembro.agregarTrayecto(trayecto1);
        Assertions.assertEquals(22000,organizacion.indicadorHCSector(sector, Periodicidad.MENSUAL, "07/2022")); //HC: 44000.0 / 2 (cant miembros)
    }
}
