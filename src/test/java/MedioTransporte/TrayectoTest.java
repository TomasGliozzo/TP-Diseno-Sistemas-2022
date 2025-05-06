package MedioTransporte;

import Factory.MedioTransporte.MedioTransporteFactory;
import Factory.MedioTransporte.TipoTramo;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.Mediciones.Unidad;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Trayecto;
import domain.modelo.entities.MedioTransporte.TransportePublico.Parada;
import domain.modelo.entities.MedioTransporte.TransportePublico.TipoDeTransportePublico;
import domain.modelo.entities.MedioTransporte.TransportePublico.TransportePublico;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class TrayectoTest {
    private Trayecto trayecto;
    private Direccion dIE,dFE,dIS, dFS, dIC, dFC;
    private Tramo tramoEcologico,tramoTransportePublico1,tramoTransportePublico2;
    private TransportePublico transportePublico1,transportePublico2;
    private Parada pI,pInt,pF,pI7,pF7;
    private Direccion d3;
    private Direccion d4;
    private Direccion d5;
    private FE fe;


    @BeforeEach
    public void init() {
        //TRAYECTO y TRAMO ECOLOGICO(INICIO)
        trayecto = MedioTransporteFactory.instanciarTrayectoConUnTramo(TipoTramo.TRAMO_TRANSPORTE_ECOLOGICO);
        tramoEcologico = trayecto.getTramos().get(0);
        dIE = tramoEcologico.getDireccionInicio();
        dFE = tramoEcologico.getDireccionFin();

        //TRANSPORTE PUBLICO 1
        tramoTransportePublico1 = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_TRANSPORTE_PUBLICO);
        transportePublico1 = (TransportePublico) tramoTransportePublico1.getMedioTransporteUtilizado();

        pI = MedioTransporteFactory.instanciarParada(1);
        pInt = MedioTransporteFactory.instanciarParada(2);
        pF = MedioTransporteFactory.instanciarParada(3);

        dIS = pI.getDireccion();
        dFS = pF.getDireccion();

        transportePublico1.agregarParada(pI);
        transportePublico1.agregarParada(pInt);
        transportePublico1.agregarParada(pF);
        tramoTransportePublico1.setDireccionInicio(dIS);
        tramoTransportePublico1.setDireccionFin(dFS);

        //TRANSPORTE PUBLICO 2
        tramoTransportePublico2 = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_TRANSPORTE_PUBLICO);
        transportePublico2 = new TransportePublico(TipoDeTransportePublico.COLECTIVO,"7",fe = new FE(1.0, Unidad.lts));
        tramoTransportePublico2.setMedioTransporteUtilizado(transportePublico2);

        dIC = new Direccion("Av Eva Peron","2600",119,"CABA");
        dFC = new Direccion("Mozart","2500",1,"CABA");

        pI7 = new Parada("Parada 7","Varela y Eva Peron", dIC,900.00,3000.00);
        pF7 = new Parada("Parada 7","Campus UTN", dFC,3000.00,300.00);

        transportePublico2.agregarParada(pI7);
        transportePublico2.agregarParada(pF7);
        tramoTransportePublico2.setDireccionInicio(dIC);
        tramoTransportePublico2.setDireccionFin(dFC);

        //AGREGO TRAMOS TP1 y TP2

        trayecto.agregarTramo(tramoTransportePublico1);
        trayecto.agregarTramo(tramoTransportePublico2);
    }

    @Test
    @DisplayName("Luego de agregar tramos a trayecto, me devuelve la cantidad correspondiente")
    public void cantidadDeTramos(){Assertions.assertEquals(3,trayecto.getTramos().size());}

    @Test
    @DisplayName("Trayecto puedo devolver la direccion de inicio del tramo inicial")
    public void obtenerPuntoPartidaTramo() {
        Assertions.assertEquals(dIE, trayecto.puntoDePartida());
    }

    @Test
    @DisplayName("Trayecto puede devolver la direccion de fin del tramo final")
    public void obtenerPuntoLlegadaTramo() {
        Assertions.assertEquals(dFC, trayecto.puntoDeLlegada());
    }

    @Test
    @DisplayName("Se puede calcular la distancia entre tramos intermedios de un trayecto utilizando Transporte Publico")
    public void sePuedeCalcularLaDistanciaEntreDosTramos(){
        Double distanciaEntreTramos2y3 = trayecto.calcularDistanciaTramosIntermedios(tramoTransportePublico1,tramoTransportePublico2);
        Assertions.assertEquals(4900.00,distanciaEntreTramos2y3);
    }
}
