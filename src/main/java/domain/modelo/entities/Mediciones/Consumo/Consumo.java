package domain.modelo.entities.Mediciones.Consumo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Embeddable
@Getter @Setter
public class Consumo {

    @Enumerated(value = EnumType.STRING)
    @Column(name = "periodicidad")
    private Periodicidad periodicidad;

    @Column(name = "periodo_imputacion")
    private String periodoImputacion;


    public Consumo(Periodicidad periodicidad, String periodoImputacion) {
        this.periodicidad = periodicidad;
        this.periodoImputacion = periodoImputacion;
    }

    public Consumo() {
    }
}
