package domain.modelo.entities.GuiaDeRecomendaciones;

import domain.modelo.entities.Entidades.Organizacion.Contacto;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.MediosDeComunicacion.MedioDeComunicacion;

import java.util.*;
import java.util.stream.Collectors;

public class TareaGuiaRecomendaciones extends TimerTask {

    private List<MedioDeComunicacion> mediosDeComunicacion;
    private Integer frecuenciaDeEnvio;
    private String mensaje; // link a pag de recomendaciones
    private List<Organizacion> organizaciones;

    public TareaGuiaRecomendaciones(Integer frecuenciaDeEnvio, String mensaje) {
        this.mediosDeComunicacion = new ArrayList<>();
        this.frecuenciaDeEnvio = frecuenciaDeEnvio;
        this.mensaje = mensaje;
        this.organizaciones = new ArrayList<>();
    }

    public void agregarOrganizaciones(Organizacion ... organizaciones) {
        Collections.addAll(this.organizaciones, organizaciones);
    }

    @Override
    public void run() {
        this.organizaciones.stream().map(Organizacion::getContactos).
                flatMap(List::stream).collect(Collectors.toList()).
                parallelStream().forEach(contacto -> this.enviarMensajesContacto(contacto));
    }

    private void enviarMensajesContacto(Contacto contacto) {
        mediosDeComunicacion.parallelStream().forEach(mC -> mC.enviarMensaje(contacto, mensaje));
    }

    private void agregarMediosDeComunicacion(MedioDeComunicacion ... mediosDeComunicacion){
        Collections.addAll(this.mediosDeComunicacion, mediosDeComunicacion);
    }
}

/*
Timer timer;
    timer = new Timer();
timer.schedule(task, 0, 1000);
// scheduleAtFixedRate
 */

// Milisegundos