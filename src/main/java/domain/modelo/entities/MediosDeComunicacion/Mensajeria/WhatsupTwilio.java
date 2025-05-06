package domain.modelo.entities.MediosDeComunicacion.Mensajeria;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import domain.Properties.PropertiesMediosComunicacion;


public class WhatsupTwilio implements AdapterServicioMensajeria {
    private PropertiesMediosComunicacion propertiesMediosComunicacion = PropertiesMediosComunicacion.getInstance();

    @Override
    public void enviarMensaje(String telefono, String mensaje) {
        Twilio.init(propertiesMediosComunicacion.getACCOUNT_SID(), propertiesMediosComunicacion.getAUTH_TOKEN());

        Message message = Message.creator(
                new com.twilio.type.PhoneNumber("whatsapp:"+ propertiesMediosComunicacion.getNuestroTelefono()),
                new com.twilio.type.PhoneNumber("whatsapp:"+telefono),
                mensaje).create();
    }
}
