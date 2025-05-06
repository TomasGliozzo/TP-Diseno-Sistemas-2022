package domain.modelo.entities.MedioTransporte.ServicioContratado;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity @Table(name = "tipo_transporte_contratado")
@Getter @Setter
public class TipoTransporteContratado {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "descripcion")
    private String descripcion;


    public TipoTransporteContratado(String tipo, String descripcion){
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public TipoTransporteContratado() {}
}
