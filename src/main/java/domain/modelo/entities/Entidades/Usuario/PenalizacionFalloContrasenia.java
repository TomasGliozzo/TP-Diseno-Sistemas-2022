package domain.modelo.entities.Entidades.Usuario;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Embeddable
@Getter @Setter
public class PenalizacionFalloContrasenia {
    @Column(name = "tiempo_espera")
    private int tiempoEspera = 0;

    @Column(name = "fecha_penalizacion", columnDefinition = "DATETIME")
    private LocalDateTime fechaPenalizacion = LocalDateTime.now();

    public PenalizacionFalloContrasenia(Integer tiempoEspera, LocalDateTime fechaPenalizacion) {
        this.tiempoEspera = tiempoEspera;
        this.fechaPenalizacion = fechaPenalizacion;
    }

    public PenalizacionFalloContrasenia() {
    }

    public boolean estaPenalizado() {
        return ChronoUnit.SECONDS.between(fechaPenalizacion, LocalDateTime.now()) < tiempoEspera;
    }

    public void penalizar(Integer tiempoEspera) {
        this.tiempoEspera = tiempoEspera;
        this.fechaPenalizacion = LocalDateTime.now();
    }
}
