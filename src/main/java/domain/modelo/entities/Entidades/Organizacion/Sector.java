package domain.modelo.entities.Entidades.Organizacion;

import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sector")
@Getter @Setter
public class Sector {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_organizacion",referencedColumnName = "id")
    private Organizacion organizacion;

    @Column(name = "nombre")
    private String nombre;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Miembro> miembros;

    public Sector(String nombre) {
        this();
        this.nombre = nombre;
    }

    public Sector() {
        this.miembros = new ArrayList<>();
    }

    public void agregarMiembro(Miembro miembro){
        this.miembros.add(miembro);
    }

    public Double calcularHCSector(Periodicidad periodicidad, String periodo) {
        return this.getMiembros().stream().mapToDouble(m -> m.calcularHC(periodicidad, periodo)).sum();
    }

    public void quitarMiembro(Miembro miembro) {
        this.getMiembros().remove(miembro);
        miembro.eliminarOrganizacion(this.organizacion);
    }
}
