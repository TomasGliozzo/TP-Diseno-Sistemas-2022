package domain.modelo.entities.HuellaCarbono;

import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.SectorTerritorial.SectorTerritorial;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "HC")
@Getter @Setter
public class HC {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "periodicidad")
    private Periodicidad periodicidad;

    @Column(name = "periodo")
    private String periodo;

    @Column(name = "valor")
    private Double valor;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_sector_territorial",referencedColumnName = "id")
    private SectorTerritorial sectorTerritorial;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_organizacion",referencedColumnName = "id")
    private Organizacion organizacion;

    public HC(Periodicidad periodicidad, String periodo, Double valor) {
        this.periodicidad = periodicidad;
        this.periodo = periodo;
        this.valor = valor;
    }

    public HC() {}

    public boolean esDe(Periodicidad periodicidad, String periodo) {
        return this.periodicidad==periodicidad && this.periodo.equals(periodo);
    }
}
