package domain.modelo.entities.Mediciones.TipoActividad;

import domain.modelo.entities.Mediciones.Consumo.TipoConsumo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "tipo_actividad")
public class TipoActividad {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "nombreActividad")
    @Getter @Setter private String nombreActividad;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_tipo_consumo", referencedColumnName = "id")
    @Getter @Setter private TipoConsumo tipoDeConsumo;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "alcance")
    @Getter @Setter private Alcance alcance;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_fe", referencedColumnName = "id")
    @Getter @Setter private FE FE;

    public TipoActividad (String nombreActividad, TipoConsumo tipoDeConsumo, Alcance alcance) {
        this.nombreActividad = nombreActividad;
        this.tipoDeConsumo = tipoDeConsumo;
        this.alcance = alcance;
    }

    public TipoActividad() {
    }
}