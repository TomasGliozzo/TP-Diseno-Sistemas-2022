package domain.modelo.entities.Servicies.GeoDDS.Entities;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Provincia {
    public int id;
    public String nombre;

    public Provincia(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Provincia() {
    }
}
