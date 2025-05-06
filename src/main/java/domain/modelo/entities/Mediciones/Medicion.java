package domain.modelo.entities.Mediciones;

import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Mediciones.Consumo.Consumo;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "medicion")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_medicion")
@Getter @Setter
public abstract class Medicion {

    @Id @GeneratedValue
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_actividad", referencedColumnName = "id")
    private TipoActividad tipoActividad;

    @Embedded
    private Consumo consumo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizacion", referencedColumnName = "id")
    private Organizacion organizacion;

    public Medicion(TipoActividad tipoActividad, Consumo consumo){
        this.tipoActividad = tipoActividad;
        this.consumo = consumo;
    }

    public Medicion() {}

    public abstract Double calcularHC();

    public boolean fueTomadaEn(Periodicidad periodicidad, String periodoImputacion) {
        return this.consumo.getPeriodicidad().equals(periodicidad) && this.consumo.getPeriodoImputacion().equals(periodoImputacion);
    }
}
