package domain.controllers.Organizacion;

import domain.modelo.entities.Entidades.Organizacion.Contacto;
import domain.modelo.entities.Entidades.Organizacion.EstadoContacto;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.RepositorioOrganizacion;
import domain.modelo.repositories.factories.FactoryRepositorio;
import domain.modelo.repositories.factories.FactoryRepositorioOrganizacion;
import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ContactoController {
    private RepositorioOrganizacion repositorioOrganizacion;
    private Repositorio<Contacto> repositorioContacto;

    private String mensajeSucces;
    private String mensajeDanger;

    private String patronCoreo = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private String patronTelefono = "^[0-9]{10}$";


    public ModelAndView mostrarTodos(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        Integer id_organizacion = Integer.valueOf(request.params("id"));
        Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);
        String url = "/organizacion/" + id_organizacion + "/contactos/eliminar/";

        List<Contacto> contactos = organizacion.getContactos().stream()
                                    .filter(c -> c.getEstado()==EstadoContacto.ACTIVO)
                                    .collect(Collectors.toList());

        parametros.put("organizacion", id_organizacion);
        parametros.put("contactos", contactos);
        parametros.put("eliminar", true);
        parametros.put("urlEliminar", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/contactos/contactos.hbs");
    }

    public ModelAndView crear(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        Integer id_organizacion = Integer.valueOf(request.params("id"));
        String url = "/organizacion/" + id_organizacion + "/contactos/crear";

        parametros.put("organizacion", id_organizacion);
        parametros.put("url", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/contactos/contacto.hbs");
    }

    public ModelAndView editar(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioContacto = FactoryRepositorio.get(Contacto.class);

        Integer id_organizacion = Integer.valueOf(request.params("id"));
        int id_contacto = Integer.parseInt(request.params("id_contacto"));
        Contacto contacto = repositorioContacto.buscar(id_contacto);
        String url = "/organizacion/" + id_organizacion + "/contactos/"+ id_contacto+"/editar";

        parametros.put("organizacion", id_organizacion);
        parametros.put("contacto", contacto);
        parametros.put("url", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros, "/contactos/contacto.hbs");
    }

    public Response modificar(Request request, Response response) {
        mensajeSucces = "Se ha modificado el contacto exitosamente";

        repositorioContacto = FactoryRepositorio.get(Contacto.class);

        try {
            int id_contacto = Integer.parseInt(request.params("id_contacto"));

            Contacto contacto = repositorioContacto.buscar(id_contacto);
            asignarAtributosContacto(contacto, request);
            repositorioContacto.modificar(contacto);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {
            response.redirect("/organizacion/" + request.params("id") + "/contactos");
            return response;
        }
    }

    public Response guardar(Request request, Response response) {
        mensajeSucces = "Se ha creado el contacto exitosamente";

        repositorioContacto = FactoryRepositorio.get(Contacto.class);
        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        try {
            Contacto contacto = new Contacto();
            asignarAtributosContacto(contacto, request);

            Organizacion organizacion = repositorioOrganizacion.buscar(Integer.parseInt(request.params("id")));
            organizacion.agregarContacto(contacto);
            repositorioContacto.agregar(contacto);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/organizacion/" + request.params("id") + "/contactos");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/organizacion/" + request.params("id") + "/contactos/crear");
        }
        finally {
            return response;
        }
    }

    private void asignarAtributosContacto(Contacto contacto, Request request) {
        Pattern patternCoreo = Pattern.compile(patronCoreo);
        Pattern patternTelefono = Pattern.compile(patronTelefono);

        String nombre = request.queryParams("nombre");
        if( !nombre.isEmpty() ){
            contacto.setNombre(nombre);
        }
        else {
            mensajeDanger = "No ha ingresado un nombre";
            throw new RuntimeException();
        }

        String email = request.queryParams("email");
        if( !email.isEmpty() && patternCoreo.matcher(email).find()){
            contacto.setEmail(email);
        }
        else {
            mensajeDanger = "Email invalido";
            throw new RuntimeException();
        }

        String telefono = request.queryParams("telefono");
        if( !telefono.isEmpty() && patternTelefono.matcher(telefono).matches()) {
            contacto.setTelefono(telefono);
        }
        else {
            mensajeDanger = "Telefono invalido";
            throw new RuntimeException();
        }
    }

    public Response eliminar(Request request, Response response) {
        String id_contacto = request.params("id_contacto");

        mensajeSucces = "Se ha podido eliminar el contacto";
        mensajeDanger = "No se ha podido eliminar el contacto";

        repositorioContacto = FactoryRepositorio.get(Contacto.class);

        if(id_contacto != null) {
            Contacto contacto = repositorioContacto.buscar(Integer.parseInt(id_contacto));

            if(contacto != null) {
                contacto.setEstado(EstadoContacto.INACTIVO);
                repositorioContacto.modificar(contacto);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                //response.redirect("/organizacion/" + request.params("id") + "/contactos");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        //response.redirect("/organizacion/" + request.params("id") + "/contactos");
        return response;
    }
}
