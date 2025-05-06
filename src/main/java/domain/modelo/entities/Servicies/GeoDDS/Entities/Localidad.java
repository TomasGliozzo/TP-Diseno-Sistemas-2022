package domain.modelo.entities.Servicies.GeoDDS.Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Localidad {
    public int id;
    public String nombre;
    public Municipio municipio;
}
