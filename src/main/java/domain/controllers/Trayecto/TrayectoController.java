package domain.controllers.Trayecto;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Miembro.Miembro;

import domain.modelo.entities.MedioTransporte.Recorrido.Trayecto;

import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.RepositorioMiembro;
import domain.modelo.repositories.factories.FactoryRepositorio;
import domain.modelo.repositories.factories.FactoryRepositorioMiembro;

import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TrayectoController {
    private RepositorioMiembro repositorioMiembro;
    private Repositorio<Trayecto> trayectoRepositorio;

    private String mensajeSucces;
    private String mensajeDanger;


    public ModelAndView mostrarTodos(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioMiembro = FactoryRepositorioMiembro.get();

        int id_miembro = Integer.parseInt(request.params("id"));
        Miembro miembro = repositorioMiembro.buscar(id_miembro);
        String url = "/miembro/" + id_miembro + "/trayectos/eliminar/";

        List<Trayecto> trayectos = miembro.getTrayectos().stream()
                .filter(t -> t.getEstado()== Estado.ACTIVO)
                .collect(Collectors.toList());

        parametros.put("miembro", id_miembro);
        parametros.put("trayectos", trayectos);
        parametros.put("eliminar", true);
        parametros.put("urlEliminar", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/trayectos/trayectos.hbs");
    }


    public ModelAndView crearTrayecto(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioMiembro = FactoryRepositorioMiembro.get();

        Integer id_miembro = Integer.valueOf(request.params("id"));
        Miembro miembro = repositorioMiembro.buscar(id_miembro);
        String url = "/miembro/" + id_miembro + "/trayectos/crear";

        parametros.put("miembro", id_miembro);
        parametros.put("url", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/trayectos/trayecto.hbs");
    }


    public ModelAndView editarTrayecto(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioMiembro = FactoryRepositorioMiembro.get();
        trayectoRepositorio = FactoryRepositorio.get(Trayecto.class);

        int id_miembro = Integer.parseInt(request.params("id"));
        int id_trayecto = Integer.parseInt(request.params("id_trayecto"));
        String url = "/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/editar";
        //String urlActualizar = "/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/editar";
        //String urlEliminar = "/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/tramos/eliminar";

        Miembro miembro = repositorioMiembro.buscar(id_miembro);
        Trayecto trayecto = trayectoRepositorio.buscar(id_trayecto);
        /*
        List<Tramo> tramos = trayecto.getTramos().stream()
                .filter(t -> t.getEstado()== Estado.ACTIVO)
                .collect(Collectors.toList());
         */

        parametros.put("miembro", id_miembro);
        parametros.put("trayecto", trayecto);
        //parametros.put("tramos", tramos);
        parametros.put("url", url);
        //parametros.put("url_actualizar", urlActualizar);
        //parametros.put("url_eliminar", urlEliminar);

        return new ModelAndView(parametros, "/trayectos/trayecto.hbs");
    }

    public Response modificarTrayecto(Request request, Response response) {
        mensajeSucces = "Se ha modificado el Trayecto exitosamente";
        mensajeDanger = "No se ha podido modificar el Trayecto";

        trayectoRepositorio = FactoryRepositorio.get(Trayecto.class);

        int id_miembro = Integer.parseInt(request.params("id"));
        int id_trayecto = Integer.parseInt(request.params("id_trayecto"));

        try {
            Trayecto trayecto = trayectoRepositorio.buscar(id_trayecto);

            asignarAtributos(trayecto, request);
            trayectoRepositorio.modificar(trayecto);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/miembro/" + id_miembro + "/trayectos");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/editar");
        }
        finally {
            return response;
        }
    }


    public Response guardarTrayecto(Request request, Response response) {
        mensajeSucces = "Se ha creado el trayecto exitosamente";
        mensajeDanger = "No se ha podido crear el trayecto";

        repositorioMiembro = FactoryRepositorioMiembro.get();
        trayectoRepositorio = FactoryRepositorio.get(Trayecto.class);

        int id_miembro = Integer.parseInt(request.params("id"));

        try {
            Trayecto trayecto = new Trayecto();
            asignarAtributos(trayecto, request);
            repositorioMiembro.buscar(id_miembro).agregarTrayecto(trayecto);

            trayectoRepositorio.agregar(trayecto);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/miembro/" + id_miembro + "/trayectos");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/miembro/" + id_miembro + "/trayectos/crear");
        }
        finally {
            return response;
        }
    }


    private void asignarAtributos(Trayecto trayecto, Request request) {
        String nombre = request.queryParams("nombre");
        if( !nombre.isEmpty()) {
            trayecto.setNombre(nombre);
        }
        else {
            mensajeDanger = "No se ha ingresado un nombre";
            throw new RuntimeException();
        }

        String descripcion = request.queryParams("descripcion");
        if( !descripcion.isEmpty() ){
            trayecto.setDescripcion(descripcion);
        }
        else {
            mensajeDanger = "No se ha ingresado una descripcion";
            throw new RuntimeException();
        }

        String frecuenciaUso = request.queryParams("frecuencia");
        if( !frecuenciaUso.isEmpty() ){
            trayecto.setFrecuenciaDeUsoSemanal(Integer.parseInt(frecuenciaUso));
        }
        else {
            mensajeDanger = "No se ha ingresado una Frecuencia";
            throw new RuntimeException();
        }
    }


    public Response eliminarTrayecto(Request request, Response response) {
        //int id_miembro = Integer.parseInt(request.params("id"));
        String id_trayecto = request.params("id_trayecto");

        trayectoRepositorio = FactoryRepositorio.get(Trayecto.class);

        mensajeSucces = "Se ha podido eliminar el trayecto";
        mensajeDanger = "No se ha podido eliminar el trayecto";

        if(id_trayecto != null) {
            Trayecto trayecto = trayectoRepositorio.buscar(Integer.parseInt(id_trayecto));

            if(trayecto != null) {
                trayecto.setEstado(Estado.INACTIVO);
                trayectoRepositorio.modificar(trayecto);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                //response.redirect("/miembro/" + id_miembro + "/trayectos");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        //response.redirect("/miembro/" + id_miembro + "/trayectos");
        return response;
    }
}
