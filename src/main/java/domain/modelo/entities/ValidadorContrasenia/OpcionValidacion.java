package domain.modelo.entities.ValidadorContrasenia;

import domain.exception.ContraseniaInvalidaExcepcion;

public abstract class OpcionValidacion {

    public void validarContrasenia(String usuario, String contrasenia) {
        if(!contraseniaValida(usuario, contrasenia)) {
            String mensajeExcepcion = getMensajeExcepcion();
            lanzarExepcion(mensajeExcepcion);
        }
    }

    protected abstract boolean contraseniaValida(String usuario, String contrasenia);
    protected abstract String getMensajeExcepcion();

    protected void lanzarExepcion(String mensaje) {
        throw new ContraseniaInvalidaExcepcion(mensaje);
    }
}
