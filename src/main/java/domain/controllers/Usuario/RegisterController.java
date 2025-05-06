package domain.controllers.Usuario;

import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Miembro.TipoDeDocumento;
import domain.modelo.entities.Entidades.Usuario.Rol;
import domain.modelo.entities.Entidades.Usuario.Usuario;

import domain.modelo.entities.ValidadorContrasenia.ValidadorContrasenia;
import domain.modelo.entities.ValidadorContrasenia.ValidarContraseniaSinNombreDeUsuario;
import domain.modelo.entities.ValidadorContrasenia.ValidarLongitudContrasenia;
import domain.modelo.entities.ValidadorContrasenia.ValidarTopContraseniasDebiles;
import domain.modelo.repositories.RepositorioMiembro;
import domain.modelo.repositories.RepositorioUsuarios;
import domain.modelo.repositories.factories.FactoryRepositorioMiembro;
import domain.modelo.repositories.factories.FactoryRepositorioRol;
import domain.modelo.repositories.factories.FactoryRepositorioUsuarios;
import domain.modelo.repositories.factories.RepositorioRol;
import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;


public class RegisterController {
    private RepositorioUsuarios repoUsuarios;
    private RepositorioMiembro repoMiembro;
    private RepositorioRol repositorioRol;
    private ValidadorContrasenia validadorContrasenia;

    private String mensajeSucces;
    private String mensajeDanger;


    public RegisterController() {
        validadorContrasenia = new ValidadorContrasenia();
        validadorContrasenia.agregarOpcionesValidacion(
                new ValidarTopContraseniasDebiles(),
                new ValidarLongitudContrasenia(),
                new ValidarContraseniaSinNombreDeUsuario(),
                new ValidarLongitudContrasenia()
        );
    }


    public ModelAndView mostrar(Request request, Response response){
        Map<String, Object> parametros = new HashMap<>();

        parametros.put("tipos", TipoDeDocumento.values());
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/login-register/registro-miembro.hbs");
    }

    public Response registrar(Request request, Response response) {
        mensajeSucces = "Registro exitoso";
        mensajeDanger = "No se ha podido registrar al miembro";

        repoUsuarios = FactoryRepositorioUsuarios.get();
        repoMiembro = FactoryRepositorioMiembro.get();
        repositorioRol = FactoryRepositorioRol.get();

        try {
            Miembro miembro = new Miembro();
            asignarAtributosAMiembro(miembro, request);
            repoMiembro.agregar(miembro);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/login");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/registro");
        }
        finally {
            return response;
        }
    }

    private void asignarAtributosAMiembro(Miembro miembro, Request request) {
        String nombre = request.queryParams("nombre");
        if( !nombre.isEmpty() ){
            miembro.setNombre(nombre);
        }
        else {
            mensajeDanger = "No se ha ingresado un nombre";
            throw new RuntimeException();
        }

        String apellido = request.queryParams("apellido");
        if( !apellido.isEmpty() ){
            miembro.setApellido(apellido);
        }
        else {
            mensajeDanger = "No se ha ingresado un apellido";
            throw new RuntimeException();
        }

        if(request.queryParams("tipoDocumento") != null) {
            TipoDeDocumento tipoDocumento = TipoDeDocumento.valueOf(request.queryParams("tipoDocumento"));
            miembro.setTipoDeDocumento(tipoDocumento);
        }
        else {
            mensajeDanger = "Tipo de documento invalido";
            throw new RuntimeException();
        }

        String documento = request.queryParams("documento");
        if( !documento.isEmpty() ){
            miembro.setNroDocumento(documento);
        }
        else {
            mensajeDanger = "No se ha ingresado numero de documento";
            throw new RuntimeException();
        }

        String nombreUsuario = request.queryParams("usuario");
        String contrasenia = request.queryParams("contrasenia");

        if(!nombreUsuario.isEmpty() && !contrasenia.isEmpty() && repoUsuarios.buscarUsuarioPorNombre(nombreUsuario)==null) {
            Rol rolMiembro = repositorioRol.buscarRolPorNombre("MIEMBRO");

            Usuario usuario = new Usuario(nombreUsuario, contrasenia);
            usuario.setRol(rolMiembro);
            miembro.setUsuario(usuario);

            if(!validadorContrasenia.validarContrasenia(usuario)) {
                mensajeDanger = "Contrasenia debil";
                throw new RuntimeException();
            }

            usuario.setContrasenia(contrasenia);
        }
        else {
            throw new RuntimeException();
        }
    }
}
