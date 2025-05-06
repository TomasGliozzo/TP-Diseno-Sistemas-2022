package domain.modelo.entities.Mediciones.MedicionCompuesta;

import domain.modelo.entities.Mediciones.Consumo.Consumo;
import domain.modelo.entities.Mediciones.Medicion;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("medicion_compuesta")
public class MedicionCompuesta extends Medicion {
    @Column(name = "distancia")
    @Getter @Setter private Double distancia;

    @Column(name = "peso")
    @Getter @Setter private Double peso;

    public MedicionCompuesta(TipoActividad tipoActividad, Consumo consumo, Double distancia, Double peso) {
        super(tipoActividad, consumo);
        this.distancia = distancia;
        this.peso = peso;
    }
    public MedicionCompuesta() {}

    @Override
    public Double calcularHC() {
        return distancia * peso * this.getTipoActividad().getFE().getValor() * (getTipoActividad().getTipoDeConsumo()).getFk();
    }
}
