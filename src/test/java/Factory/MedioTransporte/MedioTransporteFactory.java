package Factory.MedioTransporte;

import Factory.Entidades.EntidadesFactory;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.Mediciones.Unidad;
import domain.modelo.entities.MedioTransporte.MedioTransporte;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Trayecto;
import domain.modelo.entities.MedioTransporte.ServicioContratado.ServicioContratado;
import domain.modelo.entities.MedioTransporte.ServicioContratado.TipoTransporteContratado;
import domain.modelo.entities.MedioTransporte.TransporteEcologico.TransporteEcologico;
import domain.modelo.entities.MedioTransporte.TransportePublico.Parada;
import domain.modelo.entities.MedioTransporte.TransportePublico.TipoDeTransportePublico;
import domain.modelo.entities.MedioTransporte.TransportePublico.TransportePublico;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.TipoDeCombustible;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.TipoDeVehiculo;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.VehiculoParticular;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Distancia;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
import domain.modelo.entities.Servicies.GeoDDS.adapters.APIDistanciaAdapter;

import static org.mockito.Mockito.mock;

public class MedioTransporteFactory {
    private static ServicioDistancia servicioGeoDDS;
    private static APIDistanciaAdapter adapterMock;
    private static Distancia distancia = new Distancia (300.0, "M");

    public static Trayecto instanciarTrayectoConUnTramo(TipoTramo tipoTramo){
        Trayecto trayecto;
        Tramo tramo;
            switch (tipoTramo){
                case TRAMO_TRANSPORTE_PUBLICO: tramo = instanciarTramo(TipoTramo.TRAMO_TRANSPORTE_PUBLICO);
                break;
                case TRAMO_VEHICULO_PARTICULAR: tramo = instanciarTramo(TipoTramo.TRAMO_VEHICULO_PARTICULAR);
                break;
                case TRAMO_SERVICIO_CONTRATADO: tramo = instanciarTramo(TipoTramo.TRAMO_SERVICIO_CONTRATADO);
                break;
                default: tramo = instanciarTramo(TipoTramo.TRAMO_TRANSPORTE_ECOLOGICO);
            }

        return trayecto = new Trayecto(4,tramo);
    }
    public static Tramo instanciarTramo(TipoTramo tipoTramo){
        Tramo tramo;
        switch (tipoTramo){
            case TRAMO_TRANSPORTE_PUBLICO: return tramo = new Tramo(instanciarMedioDeTransporte(TipoMedioTransporte.TRANSPORTE_PUBLICO),"Subte E",instanciarDireccion(1),instanciarDireccion(3), EntidadesFactory.crearMiembroJose());
            case TRAMO_VEHICULO_PARTICULAR: return tramo = new Tramo(instanciarMedioDeTransporte(TipoMedioTransporte.VEHICULO_PARTICULAR),"Auto de Pepe",instanciarDireccion(1),instanciarDireccion(3), EntidadesFactory.crearMiembroJose());
            case TRAMO_SERVICIO_CONTRATADO: return tramo = new Tramo(instanciarMedioDeTransporte(TipoMedioTransporte.SERVICIO_CONTRATADO),"UBER",instanciarDireccion(1),instanciarDireccion(3), EntidadesFactory.crearMiembroJose());
            default: return tramo = new Tramo(instanciarMedioDeTransporte(TipoMedioTransporte.TRANSPORTE_ECOLOGICO),"Caminar",instanciarDireccion(1),instanciarDireccion(3), EntidadesFactory.crearMiembroJose());
        }
    }
    public static Direccion instanciarDireccion(int selector){

        Direccion direccion;

        switch (selector){
            case 1 : return direccion = new Direccion("Av. Jose Maria Moreno","800",119,"CABA");
            case 2 : return direccion = new Direccion("Av Emilio Mitre","700",119,"CABA");
            default: return direccion = new Direccion("Pumacahua","900",119,"CABA");
        }
    }

    public static MedioTransporte instanciarMedioDeTransporte(TipoMedioTransporte tipoMedioTransporte) {
        MedioTransporte medioTransporte;
        FE fe = new FE(1.0, Unidad.lts);
        switch (tipoMedioTransporte){
            case TRANSPORTE_PUBLICO: medioTransporte = new TransportePublico(TipoDeTransportePublico.SUBTE,"E",fe);break;
            case VEHICULO_PARTICULAR: medioTransporte = new VehiculoParticular(TipoDeVehiculo.AUTO,TipoDeCombustible.NAFTA,fe);break;
            case SERVICIO_CONTRATADO:
                TipoTransporteContratado tipoTransporteContratado = new TipoTransporteContratado("UBER","Tu buen amigo");
                medioTransporte = new ServicioContratado(tipoTransporteContratado,fe);break;
            default: medioTransporte = new TransporteEcologico("Caminar","Mover los pies con sincronicidad",fe);
        }

        return medioTransporte;
    }

    public static Parada instanciarParada(int selector){
        Parada parada;
        switch(selector){
            case 1: return parada = new Parada("Jose Maria Moreno","Parada Subte E",instanciarDireccion(1),650.00,900.00);
            case 2: return parada = new Parada("Emilio Mitre","Parada Subte E",instanciarDireccion(2),900.00,1000.00);
            default: return parada = new Parada("Medalla Milagrosa","Parada Subte E",instanciarDireccion(3),1000.00,900.00);
        }

    }
}

