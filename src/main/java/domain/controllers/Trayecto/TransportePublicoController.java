package domain.controllers.Trayecto;

import db.EntityManagerHelper;
import domain.modelo.entities.Entidades.Estado;

import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.MedioTransporte.TransportePublico.TipoDeTransportePublico;
import domain.modelo.entities.MedioTransporte.TransportePublico.TransportePublico;

import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.factories.FactoryRepositorio;

import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TransportePublicoController {
    private Repositorio<TransportePublico> transportePublicoRepositorio;
    private Repositorio<FE> feRepositorio;

    private String mensajeSucces;
    private String mensajeDanger;


    public ModelAndView mostrarTodos(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        transportePublicoRepositorio = FactoryRepositorio.get(TransportePublico.class);

        List<TransportePublico> transportePublicos = transportePublicoRepositorio.buscarTodos()
                .stream().filter(t -> t.getEstado()== Estado.ACTIVO).collect(Collectors.toList());

        String url = "/transportes/publicos/eliminar/";

        parametros.put("admin", true);
        parametros.put("publicos", transportePublicos);
        parametros.put("eliminar", true);
        parametros.put("urlEliminar", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/medios_transporte/publicos.hbs");
    }

    public ModelAndView crear(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        feRepositorio = FactoryRepositorio.get(FE.class);

        String url = "/transportes/publicos/crear";

        parametros.put("admin", true);
        parametros.put("url", url);
        parametros.put("tipos", TipoDeTransportePublico.values());
        parametros.put("fes", feRepositorio.buscarTodos());
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/medios_transporte/publico.hbs");
    }


    public ModelAndView editar(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        feRepositorio = FactoryRepositorio.get(FE.class);
        transportePublicoRepositorio = FactoryRepositorio.get(TransportePublico.class);

        Integer id_transporte = Integer.valueOf(request.params("id_transporte"));
        TransportePublico trasporte = transportePublicoRepositorio.buscar(id_transporte);
        String url = "/transportes/publicos/" + id_transporte + "/editar";

        parametros.put("admin", true);
        parametros.put("publico", trasporte);
        parametros.put("fes", feRepositorio.buscarTodos());
        parametros.put("tipos", TipoDeTransportePublico.values());
        parametros.put("url", url);

        return new ModelAndView(parametros, "/medios_transporte/publico.hbs");
    }


    public Response modificar(Request request, Response response) {
        mensajeSucces = "Se ha modificado el transpote exitosamente";
        mensajeDanger = "No se ha podido modificar el transporte";

        feRepositorio = FactoryRepositorio.get(FE.class);
        transportePublicoRepositorio = FactoryRepositorio.get(TransportePublico.class);

        int id_transporte = Integer.parseInt(request.params("id_transporte"));

        try {
            TransportePublico transporte = transportePublicoRepositorio.buscar(id_transporte);

            asignarAtributos(transporte, request);
            transportePublicoRepositorio.modificar(transporte);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/transportes/publicos");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/transportes/publicos/" + id_transporte + "/editar");
        }
        finally {
            return response;
        }
    }


    public Response guardar(Request request, Response response) {
        mensajeSucces = "Se ha creado el transporte ecologico exitosamente";
        mensajeDanger = "No se ha creado el transporte ecologico";

        feRepositorio = FactoryRepositorio.get(FE.class);
        transportePublicoRepositorio = FactoryRepositorio.get(TransportePublico.class);

        try {
            TransportePublico transporte = new TransportePublico();
            asignarAtributos(transporte, request);

            transportePublicoRepositorio.agregar(transporte);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/transportes/publicos");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/transportes/publicos/crear");
        }
        finally {
            return response;
        }
    }


    private void asignarAtributos(TransportePublico transporte, Request request) {
        String nombre = request.queryParams("nombre");
        if( !nombre.isEmpty()) {
            transporte.setNombre(nombre);
        }
        else {
            mensajeDanger = "No ha ingresado un nombre";
            throw new RuntimeException();
        }

        String descripcion = request.queryParams("descripcion");
        if( !descripcion.isEmpty() ){
            transporte.setDescripcion(descripcion);
        }
        else {
            mensajeDanger = "No ha ingresado una descripcion";
            throw new RuntimeException();
        }

        String linea = request.queryParams("linea");
        if( !linea.isEmpty() ){
            transporte.setLinea(linea);
        }
        else {
            mensajeDanger = "No ha ingresado una linea";
            throw new RuntimeException();
        }

        String idFE = request.queryParams("id_fe");
        FE fe = feRepositorio.buscar(Integer.parseInt(idFE));
        if(!idFE.isEmpty() && fe!=null) {
            transporte.setFe(fe);
        }
        else {
            mensajeDanger = "FE invalida";
            throw new RuntimeException();
        }

        String tipoTransporte = request.queryParams("tipo_transporte");
        if( !tipoTransporte.isEmpty() ){
            transporte.setTipoTransporte(TipoDeTransportePublico.valueOf(tipoTransporte));
        }
        else {
            mensajeDanger = "Tipo transporte invalido";
            throw new RuntimeException();
        }
    }


    public Response eliminar(Request request, Response response) {
        String id_transporte = request.params("id_transporte");

        mensajeSucces = "Se ha podido eliminar el transporte";
        mensajeDanger = "No se ha podido eliminar el transporte";

        transportePublicoRepositorio = FactoryRepositorio.get(TransportePublico.class);

        if(id_transporte != null) {
            TransportePublico transporte = transportePublicoRepositorio.buscar(Integer.parseInt(id_transporte));

            if(transporte != null) {
                transporte.setEstado(Estado.INACTIVO);
                transportePublicoRepositorio.modificar(transporte);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);


                //response.redirect("/transportes/publicos");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        //response.redirect("/transportes/publicos");
        EntityManagerHelper.closeEntityManager();
        return response;
    }
}
