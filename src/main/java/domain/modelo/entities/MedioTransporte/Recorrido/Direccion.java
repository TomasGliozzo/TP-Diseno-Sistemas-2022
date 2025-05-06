package domain.modelo.entities.MedioTransporte.Recorrido;

import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "direccion")
@Getter @Setter
public class Direccion {
    @Id @GeneratedValue @Column(name = "id") private int id;

    @Column(name = "nombre_calle")
    private String nombreCalle;

    @Column(name = "numero_calle")
    private String numeroCalle;

    @Column(name = "localidad")
    private int localidad;

    @Column(name = "municipio")
    private int municipio;

    @Column(name = "provincia")
    private int provincia;

    @Column(name = "ciudad")
    private String ciudad;


    public Direccion(String nombreCalle, String numeroCalle, int localidad, String ciudad) {
        this.nombreCalle = nombreCalle;
        this.numeroCalle = numeroCalle;
        this.localidad = localidad;
        this.ciudad = ciudad;
    }

    public Direccion() {}


    public String toString() {
        return ServicioDistancia.instancia().getDetalleDireccion(this);
    }


    public DireccionDTO convertirADTO() {
        return new DireccionDTO(this);
    }

    public class DireccionDTO {
        @Getter @Setter
        public String direccion;

        public DireccionDTO(Direccion direccion) {
            this.direccion = direccion.toString();
        }
    }
}
