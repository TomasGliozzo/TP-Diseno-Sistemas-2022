package domain.modelo.entities.Entidades.Usuario;

import com.google.common.hash.Hashing;
import domain.modelo.entities.Entidades.Estado;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue
    @Column(name = "id")
    @Getter @Setter
    private int id;

    @Column(name = "nombre")
    @Getter @Setter
    private String nombre;

    @Column(name = "contrasenia")
    @Getter
    private String contrasenia;

    @ManyToOne
    @JoinColumn(name = "rol_id", referencedColumnName = "id")
    @Getter @Setter
    private Rol rol;

    @Embedded
    @Getter @Setter
    private PenalizacionFalloContrasenia penalizacion;

    @Transient
    @Getter @Setter
    private Integer tiempoPenalizacion = 20;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    @Getter @Setter
    private Estado estado = Estado.ACTIVO;


    public Usuario(String nombre, String contrasenia) {
        this();
        this.nombre = nombre;
        this.contrasenia = contrasenia;
    }

    public Usuario() {
        this.penalizacion = new PenalizacionFalloContrasenia();
    }

    public void penalizar(Integer tiempoPenalizacion) {
        penalizacion.penalizar(tiempoPenalizacion);
    }

    public void penalizar() {
        penalizacion.penalizar(tiempoPenalizacion);
    }

    public boolean estaPenalizado() {
        return penalizacion.estaPenalizado();
    }

    public void setContrasenia(String password2) {
        String sha256hex = Hashing.sha256()
                .hashString(password2, StandardCharsets.UTF_8)
                .toString();

        this.contrasenia = sha256hex;
    }
}
