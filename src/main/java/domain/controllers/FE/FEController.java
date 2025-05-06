package domain.controllers.FE;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.Mediciones.Unidad;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.factories.FactoryRepositorio;

import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FEController {
    private Repositorio<FE> feRepositorio;

    private String mensajeSucces;
    private String mensajeDanger;


    public ModelAndView mostrarTodos(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        feRepositorio = FactoryRepositorio.get(FE.class);

        String url = "/fes/eliminar/";

        List<FE> fes = feRepositorio.buscarTodos().stream()
                .filter(f -> f.getEstado()==Estado.ACTIVO)
                .collect(Collectors.toList());

        parametros.put("admin", true);
        parametros.put("fes", fes);
        parametros.put("eliminar", true);
        parametros.put("urlEliminar", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/fe/fes.hbs");
    }

    public ModelAndView crear(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        String url = "/fes/crear";

        parametros.put("admin", true);
        parametros.put("url", url);
        parametros.put("unidades", Arrays.stream(Unidad.values()).collect(Collectors.toList()));
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/fe/fe.hbs");
    }

    public ModelAndView editar(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        feRepositorio = FactoryRepositorio.get(FE.class);

        Integer id_fe = Integer.valueOf(request.params("id_fe"));
        FE fe = feRepositorio.buscar(id_fe);
        String url = "/fes/"+ id_fe +"/editar";

        parametros.put("admin", true);
        parametros.put("fe", fe);
        parametros.put("url", url);

        return new ModelAndView(parametros, "/fe/fe.hbs");
    }

    public Response modificar(Request request, Response response) {
        mensajeSucces = "Se ha modificado el FE exitosamente";

        feRepositorio = FactoryRepositorio.get(FE.class);

        try {
            int id_fe = Integer.parseInt(request.params("id_fe"));
            FE fe = feRepositorio.buscar(id_fe);
            asignarAtributos(fe, request);
            feRepositorio.modificar(fe);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {
            response.redirect("/fes");
            return response;
        }
    }

    public Response guardar(Request request, Response response) {
        mensajeSucces = "Se ha creado el FE exitosamente";

        feRepositorio = FactoryRepositorio.get(FE.class);

        try {
            FE fe = new FE();
            asignarAtributos(fe, request);

            feRepositorio.agregar(fe);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/fes");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/fes/crear");
        }
        finally {
            return response;
        }
    }

    private void asignarAtributos(FE fe, Request request) {
        String nombre = request.queryParams("nombre");
        if(!nombre.isEmpty()) {
            fe.setNombre(request.queryParams("nombre"));
        }
        else {
            mensajeDanger = "No ha ingresado un nombre";
            throw new RuntimeException();
        }

        String descripcion = request.queryParams("descripcion");
        if( !descripcion.isEmpty() ){
            fe.setDescripcion(descripcion);
        }
        else {
            mensajeDanger = "No ha ingresado una descripcion";
            throw new RuntimeException();
        }

        String valor = request.queryParams("valor");
        if( !valor.isEmpty() ){
            fe.setValor(Double.valueOf(request.queryParams("valor")));
        }
        else {
            mensajeDanger = "No ha ingresado un valor";
            throw new RuntimeException();
        }

        Unidad unidad = Unidad.valueOf(request.queryParams("unidad"));
        if( unidad!= null ){
            fe.setUnidad(unidad);
        }
        else {
            mensajeDanger = "Unidad invalida";
            throw new RuntimeException();
        }

        fe.setEstado(Estado.ACTIVO);
    }

    public Response eliminar(Request request, Response response) {
        String id_fe = request.params("id_fe");

        feRepositorio = FactoryRepositorio.get(FE.class);

        mensajeSucces = "Se ha podido eliminar el FE";
        mensajeDanger = "No se ha podido eliminar el FE";

        if(id_fe != null) {
            FE fe = feRepositorio.buscar(Integer.parseInt(id_fe));

            if(fe != null) {
                fe.setEstado(Estado.INACTIVO);
                feRepositorio.modificar(fe);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                //response.redirect("/fes");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        //response.redirect("/fes");
        return response;
    }

}
