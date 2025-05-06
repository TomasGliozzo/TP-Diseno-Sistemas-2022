package domain.modelo.entities.MediosDeComunicacion.Email;

import domain.modelo.entities.Entidades.Organizacion.Contacto;
import domain.modelo.entities.MediosDeComunicacion.MedioDeComunicacion;

import lombok.Setter;

public class Email implements MedioDeComunicacion {
    @Setter
    private AdapterServicioEmail servicioEmail;

    @Override
    public void enviarMensaje(Contacto contacto, String mensaje) {
        servicioEmail.enviarMensaje(contacto.getEmail(), mensaje);
    }
}
