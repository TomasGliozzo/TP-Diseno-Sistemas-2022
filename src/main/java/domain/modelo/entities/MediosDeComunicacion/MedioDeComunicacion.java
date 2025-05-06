package domain.modelo.entities.MediosDeComunicacion;

import domain.modelo.entities.Entidades.Organizacion.Contacto;

public interface MedioDeComunicacion {
    void enviarMensaje(Contacto contacto, String mensaje);
}
