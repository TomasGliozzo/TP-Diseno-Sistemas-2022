package MedioTransporte;

import Factory.MedioTransporte.MedioTransporteFactory;
import Factory.MedioTransporte.TipoTramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.modelo.entities.MedioTransporte.TransporteEcologico.TransporteEcologico;


public class TransporteEcologicoTest {

    private TransporteEcologico tE;
    private Tramo t1;

    @BeforeEach public void init(){
        t1 = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_TRANSPORTE_ECOLOGICO);
        tE = (TransporteEcologico) t1.getMedioTransporteUtilizado();
    }

    @Test
    @DisplayName("Transporte Ecologico puede formar parte de un Tramo")
    public void transporteEcologicoFormaParteDeTramo(){
        Assertions.assertEquals(tE,t1.getMedioTransporteUtilizado());
    }


}
