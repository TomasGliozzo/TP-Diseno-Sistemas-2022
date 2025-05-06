package MedioTransporte;

import Factory.MedioTransporte.MedioTransporteFactory;
import Factory.MedioTransporte.TipoTramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.ServicioContratado.ServicioContratado;
import domain.modelo.entities.MedioTransporte.ServicioContratado.TipoTransporteContratado;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServicioContratadoTest {
    private Tramo t1;
    private ServicioContratado sC;

    @BeforeEach
    public void init(){
        t1 = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_SERVICIO_CONTRATADO);
        sC = (ServicioContratado) t1.getMedioTransporteUtilizado();
    }
    @Test
    @DisplayName("Servicio Contratado puede formar parte de un Tramo")
    public void servicioContratadoFormaParteDeTramo(){

        Assertions.assertEquals(sC,t1.getMedioTransporteUtilizado());
    }

    @Test
    @DisplayName("Se permite el alta de nuevos Servicios Contratados")
    public void altaNuevosServiciosContratados(){
        TipoTransporteContratado sC2 = new TipoTransporteContratado("Taxi","Aguante Uber");
        sC.setTipo(sC2);
        Assertions.assertEquals(sC2,sC.getTipo());
    }

}
