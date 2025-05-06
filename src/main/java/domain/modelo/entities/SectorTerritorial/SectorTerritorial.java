package domain.modelo.entities.SectorTerritorial;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.HuellaCarbono.ComponenteHCSectorTerritorial;
import domain.modelo.entities.HuellaCarbono.HC;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity(name = "sector_territorial")
@Inheritance(strategy= InheritanceType.JOINED)
@Setter @Getter
public abstract class SectorTerritorial {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_sector_territorial",referencedColumnName = "id")
    protected List<HC> historialHC;

    @OneToOne(mappedBy = "sectorTerritorial",fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private AgenteSectorial agenteSectorial;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado = Estado.ACTIVO;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_direccion", referencedColumnName = "id")
    private Direccion direccion;

    public SectorTerritorial() {
        this.historialHC = new ArrayList<>();
    }


    public Double calcularHC(Periodicidad periodicidad, String periodo) {
        Optional<HC> huellaCarbono = historialHC.stream().filter(h -> h.esDe(periodicidad, periodo)).findFirst();
        Double valorHC;

        if(!huellaCarbono.isPresent())
            valorHC = this.nuevaHC(periodicidad, periodo);
        else
            valorHC = huellaCarbono.get().getValor();

        return valorHC;
    }

    public abstract Double nuevaHC(Periodicidad periodicidad, String periodo);

    public abstract List<ComponenteHCSectorTerritorial> calcularComposicionHC(Periodicidad periodicidad, String periodo);

    public void agregarHC(HC hc){
        historialHC.add(hc);
        hc.setSectorTerritorial(this);
    }

    public void agregarAgenteSectorial(AgenteSectorial agenteSectorial){
        agenteSectorial.setSectorTerritorial(this);
        this.setAgenteSectorial(agenteSectorial);
    }
}
