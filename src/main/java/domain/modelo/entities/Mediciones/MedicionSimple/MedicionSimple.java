package domain.modelo.entities.Mediciones.MedicionSimple;

import domain.modelo.entities.Mediciones.Consumo.Consumo;
import domain.modelo.entities.Mediciones.Medicion;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("medicion_simple")
public class MedicionSimple extends Medicion {
    @Column(name = "valor")
    @Getter @Setter private Double valor;

    public MedicionSimple(TipoActividad tipoActividad, Consumo consumo, Double valor) {
        super(tipoActividad,consumo);
        this.valor = valor;
    }

    public MedicionSimple() {}

    @Override
    public Double calcularHC() {
        return this.getTipoActividad().getFE().getValor() * valor;
    }

}
