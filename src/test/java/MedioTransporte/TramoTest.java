package MedioTransporte;

import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import Factory.Entidades.EntidadesFactory;
import Factory.MedioTransporte.MedioTransporteFactory;
import Factory.MedioTransporte.TipoTramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.ServicioContratado.ServicioContratado;
import domain.modelo.entities.MedioTransporte.TransportePublico.Parada;
import domain.modelo.entities.MedioTransporte.TransportePublico.TransportePublico;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Distancia;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
import domain.modelo.entities.Servicies.GeoDDS.adapters.APIDistanciaAdapter;
import domain.exception.Excepcion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class TramoTest {

    private Miembro jose = EntidadesFactory.crearMiembroJose();
    private Organizacion mostazaCentro = EntidadesFactory.crearOrganizacionMostaza();
    private Tramo tramoCompartido;
    private Tramo tramoNoCompartido;
    private ServicioContratado transporteCompartido;
    private TransportePublico transporteNoCompartido;
    private Direccion d1,d2,dI,dF;
    private Parada pI,pInt,pF;


    @BeforeEach
    public void init() {

        d1 = MedioTransporteFactory.instanciarDireccion(1);
        d2 = MedioTransporteFactory.instanciarDireccion(2);

        tramoCompartido = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_SERVICIO_CONTRATADO);
        transporteCompartido = (ServicioContratado) tramoCompartido.getMedioTransporteUtilizado();

        tramoNoCompartido = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_TRANSPORTE_PUBLICO);
        transporteNoCompartido = (TransportePublico) tramoNoCompartido.getMedioTransporteUtilizado();

    }

    @Test
    @DisplayName("Puedo agregar miembro a Tramo si se cumplen todas las condiciones")
    public void agregarMiembroSiCumpleCondiciones(){

        tramoCompartido.agregarMiembro(jose);
        Assertions.assertDoesNotThrow(()->tramoCompartido.agregarMiembro(jose));
        Assertions.assertTrue(tramoCompartido.getMiembrosVinculados().contains(jose));
    }

   @Test
   @DisplayName("Si intento vincular un miembro con un medio de transporte no compartido, tira excepcion")
   public void excepcionMedioTransporteNoCompartido(){

       Assertions.assertThrows(Excepcion.class,()->tramoNoCompartido.agregarMiembro(jose));
       Assertions.assertFalse(tramoNoCompartido.getMiembrosVinculados().contains(jose));
   }

   @Test
   @DisplayName("Se puede obtener la distancia entre dos direcciones utilizando la API")
    public void sePuedeObtenerDistanciaDeDosDireccionesAPI () throws IOException {
       ServicioDistancia servicioGeoDDS;
       APIDistanciaAdapter adapterMock;

       adapterMock = mock(APIDistanciaAdapter.class);
       servicioGeoDDS = ServicioDistancia.instancia();
       servicioGeoDDS.setAdapter(adapterMock);

       when(adapterMock.distancia(d1, d2)).thenReturn(distanciaMock());
       Assertions.assertEquals(3.0, servicioGeoDDS.distancia(d1, d2).valor);
       Assertions.assertEquals("KM", servicioGeoDDS.distancia(d1, d2).unidad);
   }
    @Test
    @DisplayName("Se puede obtener la distancia entre dos direcciones utilizando Transporte Publico")
    public void sePuedeObtenerDistanciaDeDosDireccionesTP (){
        pI = MedioTransporteFactory.instanciarParada(1);
        pInt = MedioTransporteFactory.instanciarParada(2);
        pF = MedioTransporteFactory.instanciarParada(3);

        dI = pI.getDireccion();
        dF = pF.getDireccion();

        transporteNoCompartido.agregarParada(pI);
        transporteNoCompartido.agregarParada(pInt);
        transporteNoCompartido.agregarParada(pF);

        tramoNoCompartido.setDireccionInicio(dI);
        tramoNoCompartido.setDireccionFin(dF);

        Assertions.assertEquals(1900.0,tramoNoCompartido.getDistancia());
    }

   private Distancia distanciaMock (){
        return new Distancia(3.0, "KM");
    }

}
