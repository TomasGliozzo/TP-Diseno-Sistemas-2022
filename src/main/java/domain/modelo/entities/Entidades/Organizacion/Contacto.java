package domain.modelo.entities.Entidades.Organizacion;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "contacto")
@Getter @Setter
public class Contacto {
    @Id @GeneratedValue @Column(name="id") private int id;

    @Column(name="nombre")
    private String nombre;

    @Column(name="email")
    private String email;

    @Column(name="telefono")
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_organizacion",referencedColumnName = "id")
    private Organizacion organizacion;

    @Column(name="estado")
    @Enumerated(EnumType.STRING)
    private EstadoContacto estado;


    public Contacto(String nombre, String email, String telefono) {
        this();
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    public Contacto() {
        this.estado=EstadoContacto.ACTIVO;
    }
}
