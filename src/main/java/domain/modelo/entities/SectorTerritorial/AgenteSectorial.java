package domain.modelo.entities.SectorTerritorial;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "agente_sectorial")
@Setter @Getter
public class AgenteSectorial {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sector_territorial_id",referencedColumnName = "id")
    private SectorTerritorial sectorTerritorial;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;


    @Column(name = "nombre_apellido")
    private String nombreApellido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado = Estado.ACTIVO;

    public AgenteSectorial(SectorTerritorial sectorTerritorial) {
        this.sectorTerritorial = sectorTerritorial;
    }

    public AgenteSectorial() {}

    public void agregarSectorTerritorial(SectorTerritorial sectorTerritorial){
        this.setSectorTerritorial(sectorTerritorial);
        sectorTerritorial.setAgenteSectorial(this);
    }
}
