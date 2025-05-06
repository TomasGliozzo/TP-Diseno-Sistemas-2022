package domain.modelo.entities.MediosDeComunicacion.Mensajeria;

import domain.modelo.entities.Entidades.Organizacion.Contacto;
import domain.modelo.entities.MediosDeComunicacion.MedioDeComunicacion;

import lombok.Setter;

public class Mensajeria implements MedioDeComunicacion {
    @Setter
    private AdapterServicioMensajeria servicioMensajeria;

    @Override
    public void enviarMensaje(Contacto contacto, String mensaje) {
        servicioMensajeria.enviarMensaje(contacto.getTelefono(), mensaje);
    }
}
