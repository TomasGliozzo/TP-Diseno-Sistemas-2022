package domain.modelo.entities.Entidades.Miembro;

import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "solicitud_miembro")
@Getter @Setter
public class SolicitudMiembro {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "fecha", columnDefinition = "DATE")
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_miembro", referencedColumnName = "id")
    private Miembro miembro;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_sector")
    private Sector sector;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_organizacion")
    private Organizacion organizacion;

    @Column(name = "estado_solicitud")
    @Enumerated(value = EnumType.STRING)
    private EstadoSolicitud estadoSolicitud;


    public SolicitudMiembro(Miembro miembro, Sector sector) {
        this.estadoSolicitud = EstadoSolicitud.EN_CURSO;
        this.fecha = LocalDate.now();
        this.miembro = miembro;
        this.sector = sector;
    }

    public SolicitudMiembro() {}
}
