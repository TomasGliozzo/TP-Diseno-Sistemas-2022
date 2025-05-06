package ValidarContraseniaTests;


import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.exception.ContraseniaInvalidaExcepcion;
import domain.modelo.entities.ValidadorContrasenia.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class ValidarContraseniaTest {
    // Usuario
    private String nombreUsuario;
    // Contrasenias
    private String contraseniaValida;
    private String contraseniaInvalida;
    // Opciones Validacion
    private ValidarContraseniaSinNombreDeUsuario opcionSinNombreDeUsuario;
    private ValidarExistenciaLetraMayuscula opcionMayuscula;
    private ValidarLongitudContrasenia opcionLongitud;
    private ValidarTopContraseniasDebiles opcionTopDebiles;
    // Validador
    private ValidadorContrasenia validador;

    @BeforeEach
    public void init() {
        nombreUsuario = "Usuario";
        contraseniaInvalida = "usuario";
        contraseniaValida="p2Pass29";

        // Validaciones
        opcionSinNombreDeUsuario = new ValidarContraseniaSinNombreDeUsuario();
        opcionMayuscula = new ValidarExistenciaLetraMayuscula();
        opcionLongitud = new ValidarLongitudContrasenia();
        opcionTopDebiles = new ValidarTopContraseniasDebiles();

        // Validador
        validador = new ValidadorContrasenia();
        validador.agregarOpcionesValidacion(opcionSinNombreDeUsuario,opcionMayuscula,opcionLongitud,opcionTopDebiles);
    }

    @Test
    @DisplayName("Una contrasenia no debe tener el nombre del usuario")
    public void validarContraseniaSinNombreDeUsuarioTest() {
        ContraseniaInvalidaExcepcion excepcion = Assertions.assertThrows(ContraseniaInvalidaExcepcion.class,
                ()-> opcionSinNombreDeUsuario.validarContrasenia(nombreUsuario, contraseniaInvalida));
        Assertions.assertEquals(opcionSinNombreDeUsuario.getMensajeExcepcion(), excepcion.getMessage());
    }

    @Test
    @DisplayName("Una contrasenia valida debe contener una letra mayuscula")
    public void validarExistenciaLetraMayusculaTest() {
        ContraseniaInvalidaExcepcion excepcion = Assertions.assertThrows(ContraseniaInvalidaExcepcion.class,
                ()-> opcionMayuscula.validarContrasenia(nombreUsuario, contraseniaInvalida));
        Assertions.assertEquals(opcionMayuscula.getMensajeExcepcion(), excepcion.getMessage());
    }

    @Test
    @DisplayName("La contrasenia de tener una longitud de 8, como minimo")
    public void validarLongitudContraseniaTest() {
        ContraseniaInvalidaExcepcion excepcion = Assertions.assertThrows(ContraseniaInvalidaExcepcion.class,
                ()-> opcionLongitud.validarContrasenia(nombreUsuario, contraseniaInvalida));
        Assertions.assertEquals(opcionLongitud.getMensajeExcepcion(), excepcion.getMessage());
    }

    @Test
    @DisplayName("La contrasenia no debe estar en el top perores contrasenias")
    public void validarTopContraseniasDebilesTest() {
        contraseniaInvalida= "123456";

        ContraseniaInvalidaExcepcion excepcion = Assertions.assertThrows(ContraseniaInvalidaExcepcion.class,
                ()-> opcionTopDebiles.validarContrasenia(nombreUsuario,contraseniaInvalida));
        Assertions.assertEquals(opcionTopDebiles.getMensajeExcepcion(), excepcion.getMessage());
    }

    @Test
    @DisplayName("Una contrasenia valida tiene que cumplir todas las validaciones")
    public void contraseniaValida() {
        Usuario usuario = new Usuario(nombreUsuario, contraseniaValida);
        Assertions.assertTrue(validador.validarContrasenia(usuario));

        Usuario usuario2 = new Usuario(nombreUsuario, "123456");
        Assertions.assertFalse(validador.validarContrasenia(usuario2));
    }
}
