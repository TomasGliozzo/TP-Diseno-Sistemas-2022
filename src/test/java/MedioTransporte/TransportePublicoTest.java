package MedioTransporte;

import Factory.MedioTransporte.MedioTransporteFactory;
import Factory.MedioTransporte.TipoTramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.TransportePublico.Parada;
import domain.modelo.entities.MedioTransporte.TransportePublico.TipoDeTransportePublico;
import domain.modelo.entities.MedioTransporte.TransportePublico.TransportePublico;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TransportePublicoTest {

    private TransportePublico transportePublico;
    private Tramo tramo;
    private Parada pI;
    private Parada pInt;
    private Parada pF;
    private Direccion dI;
    private Direccion dF;


    @BeforeEach
    public void init(){


        pI = MedioTransporteFactory.instanciarParada(1);
        pInt = MedioTransporteFactory.instanciarParada(2);
        pF = MedioTransporteFactory.instanciarParada(3);

        dI = pI.getDireccion();
        dF = pF.getDireccion();

        tramo = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_TRANSPORTE_PUBLICO);
        transportePublico = (TransportePublico) tramo.getMedioTransporteUtilizado();
        transportePublico.agregarParada(pI);
        transportePublico.agregarParada(pInt);
        transportePublico.agregarParada(pF);
    }
    @Test
    @DisplayName("Transporte Publico puede formar parte de un Tramo")
    public void transpotePublicoFormaParteDeTramo(){
        Assertions.assertEquals(transportePublico, tramo.getMedioTransporteUtilizado());
    }

    @Test
    @DisplayName("Transporte Publico tiene parada de inicio y fin")
    public void transportePublicoTieneParadasDeIncioyFinDeTramo(){

        Assertions.assertEquals(pI, transportePublico.getParadas().get(0)); //Primera
        Assertions.assertEquals(pF, transportePublico.getParadas().get(transportePublico.getParadas().size() - 1)); //Ultima
    }

    @Test
    @DisplayName("Transporte Publico especifica el tipo de transpote y la linea utilizada")
    public void transportePublicoTieneTipoyLinea(){
        Assertions.assertEquals(TipoDeTransportePublico.SUBTE, transportePublico.getTipoTransporte());
        Assertions.assertEquals("E", transportePublico.getLinea());
    }
    @Test
    @DisplayName("Dada una direccion devuelve la parada correspondiente")
    public void paradaDadaDireccion(){

        Assertions.assertEquals(pI, transportePublico.paradaSegunDireccion(dI));
    }

    @Test
    @DisplayName("Dada una direccion inicial y final calcula la distancia entre las paradas")
    public void calcularDistanciaEntreParadasDadasDosDirecciones(){
        Assertions.assertEquals(1900.0, transportePublico.calcularDistancia(dI, dF));
    }

    @Test
    @DisplayName("Dada una dirección inicial y final pasadas en orden inversa respecto a las paradas calcula la distancia")
    public void calcularDistanciaEntreParadasDadasDosDireccionesIvertidad(){
        Assertions.assertEquals(1900.0,transportePublico.calcularDistancia(dF,dI));
    }

}
