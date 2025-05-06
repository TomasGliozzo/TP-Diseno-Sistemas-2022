package domain.modelo.entities.MedioTransporte.VehiculoParticular;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.MedioTransporte.MedioTransporte;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@DiscriminatorValue("vehiculo_particular")
@Getter @Setter
public class VehiculoParticular extends MedioTransporte {
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_de_vehiculo")
    private TipoDeVehiculo tipoDeVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_de_combustible")
    private TipoDeCombustible tipoDeCombustible;

    public VehiculoParticular(TipoDeVehiculo tDV, TipoDeCombustible tDC, FE fe){
        this();
        this.tipoDeVehiculo = tDV;
        this.tipoDeCombustible = tDC;
        this.fe = fe;
    }

    public VehiculoParticular() {
        setEstado(Estado.ACTIVO);
    }

    @Override
    public boolean esCompartido() {
        return true;
    }
}
