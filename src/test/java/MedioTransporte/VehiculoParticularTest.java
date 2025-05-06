package MedioTransporte;

import Factory.MedioTransporte.MedioTransporteFactory;
import Factory.MedioTransporte.TipoTramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.TipoDeCombustible;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.TipoDeVehiculo;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.VehiculoParticular;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Distancia;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
import domain.modelo.entities.Servicies.GeoDDS.adapters.APIDistanciaAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;

public class VehiculoParticularTest {
    private VehiculoParticular vehiculoParticular;
    private Tramo tramo;
    ServicioDistancia servicioGeoDDS;
    APIDistanciaAdapter adapterMock;
    private Distancia distancia = new Distancia (300.0, "M");
    
    @BeforeEach
    public void init() throws IOException {


        tramo = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_VEHICULO_PARTICULAR);
        vehiculoParticular = (VehiculoParticular) tramo.getMedioTransporteUtilizado();

    }

    @Test
    @DisplayName("Vehiculo Particular puede formar parte de un Tramo")
    public void vehiculoParticularFormaParteDeTramo(){
        Assertions.assertEquals(vehiculoParticular, tramo.getMedioTransporteUtilizado());
    }

    @Test
    @DisplayName("Un Vehiculo Particular tiene un tipo de Vehiculo y de Combustible")
    public void vehiculoParticularTieneTipoDeVehYComb(){
        Assertions.assertEquals(vehiculoParticular.getTipoDeVehiculo(),TipoDeVehiculo.AUTO);
        Assertions.assertEquals(vehiculoParticular.getTipoDeCombustible(),TipoDeCombustible.NAFTA);
    }
}
