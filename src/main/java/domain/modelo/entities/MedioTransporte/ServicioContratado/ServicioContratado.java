package domain.modelo.entities.MedioTransporte.ServicioContratado;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.MedioTransporte.MedioTransporte;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@DiscriminatorValue("servicio_contratado")
public class ServicioContratado extends MedioTransporte {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_tipo_transporte_contratado",referencedColumnName = "id")
    @Getter @Setter
    private TipoTransporteContratado tipo;

    public  ServicioContratado(TipoTransporteContratado tipoTransporte, FE fe){
        this();
        this.tipo = tipoTransporte;
        this.fe = fe;
    }

    public ServicioContratado() {
        setEstado(Estado.ACTIVO);
    }

    @Override
    public boolean esCompartido() {
        return true;
    }
}
