package domain.controllers.Miembro;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Miembro.Miembro;

import domain.modelo.entities.Entidades.Miembro.TipoDeDocumento;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.entities.ValidadorContrasenia.ValidadorContrasenia;
import domain.modelo.entities.ValidadorContrasenia.ValidarContraseniaSinNombreDeUsuario;
import domain.modelo.entities.ValidadorContrasenia.ValidarLongitudContrasenia;
import domain.modelo.entities.ValidadorContrasenia.ValidarTopContraseniasDebiles;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.RepositorioMiembro;
import domain.modelo.repositories.factories.FactoryRepositorio;
import domain.modelo.repositories.factories.FactoryRepositorioMiembro;

import helpers.AlertaHelper;
import helpers.UsuarioLogueadoHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MiembroController {
    private RepositorioMiembro repositorioMiembro;
    private Repositorio<Usuario> usuarioRepositorio;
    private ValidadorContrasenia validadorContrasenia;

    private String mensajeSucces;
    private String mensajeDanger;


    public MiembroController() {
        validadorContrasenia = new ValidadorContrasenia();
        validadorContrasenia.agregarOpcionesValidacion(
                new ValidarTopContraseniasDebiles(),
                new ValidarLongitudContrasenia(),
                new ValidarContraseniaSinNombreDeUsuario(),
                new ValidarLongitudContrasenia()
        );
    }

    public ModelAndView mostrarTodos(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioMiembro = FactoryRepositorioMiembro.get();

        String url = "/miembros/eliminar/";

        List<Miembro> miembros = repositorioMiembro.buscarTodos().stream()
                .filter(m -> m.getEstado()== Estado.ACTIVO)
                .collect(Collectors.toList());

        parametros.put("admin", true);
        parametros.put("miembros", miembros);
        parametros.put("eliminar", true);
        parametros.put("urlEliminar", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/miembros/miembros.hbs");
    }


    public ModelAndView editar(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioMiembro = FactoryRepositorioMiembro.get();

        Integer id_miembro = Integer.valueOf(request.params("id"));
        Miembro miembro = repositorioMiembro.buscar(id_miembro);
        Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);
        String url;
        String urlCambio;

        if(usuario.getId() == miembro.getUsuario().getId()) {
            url = "/miembro/" + id_miembro + "/editar";
            urlCambio = "/miembro/" + id_miembro + "/editar/cambiar_contrasenia";
            parametros.put("miembro", miembro.getId());
        }
        else {
            url = "/miembros/" + id_miembro + "/editar";
            urlCambio = "/miembros/" + id_miembro + "/editar/cambiar_contrasenia";
            parametros.put("admin", true);
        }

        parametros.put("miembro-editar", miembro);
        parametros.put("tipos", TipoDeDocumento.values());
        parametros.put("url", url);
        parametros.put("urlCambio", urlCambio);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros, "/miembros/miembro.hbs");
    }

    public Response modificar(Request request, Response response) {
        mensajeSucces = "Se ha modificado al miembro exitosamente";
        mensajeDanger = "No se ha modificado al miembro";

        repositorioMiembro = FactoryRepositorioMiembro.get();

        try {
            Integer id_miembro = Integer.valueOf(request.params("id"));
            Miembro miembro = repositorioMiembro.buscar(id_miembro);

            asignarAtributos(miembro, request);
            repositorioMiembro.modificar(miembro);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {

            Integer id_miembro = Integer.valueOf(request.params("id"));
            Miembro miembro = repositorioMiembro.buscar(id_miembro);
            Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);

            if(usuario.getId() == miembro.getUsuario().getId())
                response.redirect("/miembro/" + id_miembro + "/editar");
            else
                response.redirect("/miembros");

            return response;
        }
    }

    private void asignarAtributos(Miembro miembro, Request request) {
        String nombre = request.queryParams("nombre");
        if( !nombre.isEmpty() ){
            miembro.setNombre(nombre);
        }
        else {
            mensajeDanger = "No se ha ingresado nombre";
            throw new RuntimeException();
        }

        String apellido = request.queryParams("apellido");
        if( !apellido.isEmpty() ){
            miembro.setApellido(apellido);
        }
        else {
            mensajeDanger = "No se ha ingresado apellido";
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
    }

    public Response eliminar(Request request, Response response) {
        String id_miembro = request.params("id_miembro");

        repositorioMiembro = FactoryRepositorioMiembro.get();

        mensajeSucces = "Se ha podido eliminar al miembro";
        mensajeDanger = "No se ha podido eliminar al miembro";

        if(!id_miembro.isEmpty()) {
            Miembro miembro = repositorioMiembro.buscar(Integer.parseInt(id_miembro));

            if(miembro != null) {
                miembro.setEstado(Estado.INACTIVO);
                miembro.getUsuario().setEstado(Estado.INACTIVO);

                repositorioMiembro.modificar(miembro);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        return response;
    }

    public ModelAndView cambiarContrasenia(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioMiembro = FactoryRepositorioMiembro.get();

        Integer id_miembro = Integer.valueOf(request.params("id"));
        Miembro miembro = repositorioMiembro.buscar(id_miembro);
        Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);
        String urlCambio;
        String urlVolver;

        if(usuario.getId() == miembro.getUsuario().getId()) {
            urlVolver = "/miembro/" + id_miembro + "/editar";
            urlCambio = "/miembro/" + id_miembro + "/editar/cambiar_contrasenia";
            parametros.put("miembro", miembro.getId());
        }
        else {
            urlVolver = "/miembros/" + id_miembro + "/editar";
            urlCambio = "/miembros/" + id_miembro + "/editar/cambiar_contrasenia";
            parametros.put("admin", true);
        }

        parametros.put("urlCambio", urlCambio);
        parametros.put("urlVolver", urlVolver);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/login-register/cambio_contrasenia.hbs");
    }

    public Response modificarContrasenia(Request request, Response response) {
        mensajeSucces = "Se ha modificado la contraseña";
        mensajeDanger = "No se ha modificado la contraseña";

        repositorioMiembro = FactoryRepositorioMiembro.get();
        usuarioRepositorio = FactoryRepositorio.get(Usuario.class);

        int id_miembro = Integer.parseInt(request.params("id"));
        Miembro miembro = repositorioMiembro.buscar(id_miembro);

        try {
            Usuario usuario =  usuarioRepositorio.buscar(miembro.getUsuario().getId());
            Usuario usuarioAux = new Usuario();

            String contraseniaVieja = request.queryParams("contrasenia-vieja");
            String contraseniaNueva = request.queryParams("contrasenia-nueva");

            usuarioAux.setContrasenia(contraseniaVieja);

            if(usuario!=null && !contraseniaNueva.isEmpty() && !contraseniaVieja.isEmpty() &&
                usuario.getContrasenia().equals(usuarioAux.getContrasenia())) {
                Usuario usuarioCCN = new Usuario(usuario.getNombre(), contraseniaNueva);

                if(!validadorContrasenia.validarContrasenia(usuarioCCN)) {
                    mensajeDanger = "Contrasenia debil";
                    throw new RuntimeException();
                }

                usuario.setContrasenia(contraseniaNueva);
                usuarioRepositorio.modificar(usuario);
            }
            else {
                throw new RuntimeException();
            }

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {
            Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);

            if(usuario.getId() == miembro.getUsuario().getId())
                response.redirect("/miembro/" + id_miembro + "/editar");
            else
                response.redirect("/miembros");

            return response;
        }
    }

    public ModelAndView mostrarOrganizaciones(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioMiembro = FactoryRepositorioMiembro.get();

        int id_miembro = Integer.parseInt(request.params("id"));
        Miembro miembro = repositorioMiembro.buscar(id_miembro);

        List<Sector> sectores = miembro.getOrganizaciones().stream().map(o -> o.sectorMiembro(miembro))
                .collect(Collectors.toList());

        parametros.put("miembro", id_miembro);
        parametros.put("sectores", sectores);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/miembros/mis_organizaciones.hbs");
    }
}
