package domain.controllers.Trayecto;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.TransportePublico.Parada;
import domain.modelo.entities.MedioTransporte.TransportePublico.TransportePublico;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Provincia;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
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


public class ParadaController {
    private Repositorio<TransportePublico> transportePublicoRepositorio;
    private Repositorio<Parada> paradaRepositorio;

    private String mensajeSucces;
    private String mensajeDanger;


    public ModelAndView mostrarTodos(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        transportePublicoRepositorio = FactoryRepositorio.get(TransportePublico.class);

        int idTransporte = Integer.parseInt(request.params("id_transporte"));
        TransportePublico transportePublico = transportePublicoRepositorio.buscar(idTransporte);
        List<Parada> paradas = transportePublico.getParadas().stream()
                .filter(t -> t.getEstado()== Estado.ACTIVO).collect(Collectors.toList());

        String url = "/transportes/publicos/" + idTransporte + "/paradas/eliminar/";
        String urlVolver = "/transportes/publicos";

        parametros.put("admin", true);
        parametros.put("paradas", paradas);
        parametros.put("eliminar", true);
        parametros.put("urlEliminar", url);
        parametros.put("urlVolver", urlVolver);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/medios_transporte/paradas.hbs");
    }

    public ModelAndView crear(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        int idTransporte = Integer.parseInt(request.params("id_transporte"));

        String url = "/transportes/publicos/" + idTransporte+ "/paradas/crear";
        String urlLocalidad = "/transportes/publicos/" + idTransporte + "/paradas/crear/localidad";
        String urlProvincia = "/transportes/publicos/" + idTransporte + "/paradas/crear/provincia";
        String urlMunicipio = "/transportes/publicos/" + idTransporte + "/paradas/crear/municipio";

        List<Provincia> provincias = ServicioDistancia.instancia().listaProvincias();

        parametros.put("admin", true);

        parametros.put("provincia", request.session().attribute("provincia"));
        parametros.put("municipio", request.session().attribute("municipio"));
        parametros.put("localidad", request.session().attribute("localidad"));

        parametros.put("provincias", provincias);
        parametros.put("municipios", request.session().attribute("municipios"));
        parametros.put("localidades", request.session().attribute("localidades"));

        parametros.put("url", url);
        parametros.put("urlLocalidad", urlLocalidad);
        parametros.put("urlMunicipio", urlMunicipio);
        parametros.put("urlProvincia", urlProvincia);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/medios_transporte/parada.hbs");
    }


    public Response actualizarProvincia(Request request, Response response) {
        int idProvincia = Integer.parseInt(request.params("id_provincia"));

        request.session().attribute("municipios", ServicioDistancia.instancia().listaMunicipios(idProvincia));
        request.session().attribute("provincia", ServicioDistancia.instancia().getProvincia(idProvincia));

        return response;
    }

    public Response actualizarMunicipio(Request request, Response response) {
        int id_municipio = Integer.parseInt(request.params("id_municipio"));
        int id_provincia = Integer.parseInt(request.params("id_provincia"));

        request.session().attribute("localidades", ServicioDistancia.instancia().listaLocalidades(id_municipio));
        request.session().attribute("municipio", ServicioDistancia.instancia().getMunicipio(id_provincia, id_municipio));

        return response;
    }


    public Response guardar(Request request, Response response) {
        mensajeSucces = "Se ha creado la parada exitosamente";
        mensajeDanger = "No se ha creado la parada";

        transportePublicoRepositorio = FactoryRepositorio.get(TransportePublico.class);
        paradaRepositorio = FactoryRepositorio.get(Parada.class);

        int idTransporte = Integer.parseInt(request.params("id_transporte"));
        TransportePublico transportePublico = transportePublicoRepositorio.buscar(idTransporte);

        try {
            Parada parada = new Parada();
            asignarAtributos(parada, request);
            transportePublico.agregarParada(parada);

            paradaRepositorio.agregar(parada);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/transportes/publicos/" + idTransporte+ "/paradas");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/transportes/publicos/" + idTransporte+ "/paradas/crear");
        }
        finally {
            request.session().removeAttribute("provincia");
            request.session().removeAttribute("municipio");
            request.session().removeAttribute("localidad");
            request.session().removeAttribute("municipios");
            request.session().removeAttribute("localidades");

            return response;
        }
    }

    private void asignarAtributos(Parada parada, Request request) {
        String nombre = request.queryParams("nombre");
        if( !nombre.isEmpty()) {
            parada.setNombre(nombre);
        }
        else {
            mensajeDanger = "No se ha ingresado un nombre";
            throw new RuntimeException();
        }

        String descripcion = request.queryParams("descripcion");
        if( !descripcion.isEmpty() ){
            parada.setDescripcion(descripcion);
        }
        else {
            mensajeDanger = "No se ha ingresado una descripcion";
            throw new RuntimeException();
        }

        String distanciaAnterior = request.queryParams("distancia_anterior");
        if( !distanciaAnterior.isEmpty() ){
            parada.setDistanciaDireccionAnterior(Double.valueOf(distanciaAnterior));
        }
        else {
            mensajeDanger = "No se ha ingresado una distancia";
            throw new RuntimeException();
        }

        String distanciaSiguiente = request.queryParams("distancia_siguiente");
        if( !distanciaSiguiente.isEmpty() ){
            parada.setDistanciaDireccionSiguiente(Double.valueOf(distanciaSiguiente));
        }
        else {
            mensajeDanger = "No se ha ingresado una distancia";
            throw new RuntimeException();
        }


        Direccion direccion = new Direccion();
        String localidad = request.queryParams("localidad");
        if( !localidad.isEmpty() ){
            direccion.setLocalidad(Integer.parseInt(localidad));
        }
        else {
            mensajeDanger = "Direccion invalida";
            throw new RuntimeException();
        }

        String municipio = request.queryParams("municipio");
        if( !municipio.isEmpty() ){
            direccion.setMunicipio(Integer.parseInt(municipio));
        }
        else {
            mensajeDanger = "Direccion invalida";
            throw new RuntimeException();
        }

        String provincia = request.queryParams("provincia");
        if( !provincia.isEmpty() ){
            direccion.setProvincia(Integer.parseInt(provincia));
        }
        else {
            mensajeDanger = "Direccion invalida";
            throw new RuntimeException();
        }

        String calle = request.queryParams("calle");
        if( !calle.isEmpty() ){
            direccion.setNombreCalle(calle);
        }
        else {
            mensajeDanger = "Direccion invalida";
            throw new RuntimeException();
        }

        String numero = request.queryParams("numero");
        if( !numero.isEmpty() ){
            direccion.setNumeroCalle(numero);
        }
        else {
            mensajeDanger = "Direccion invalida";
            throw new RuntimeException();
        }

        parada.setDireccion(direccion);
    }


    public Response eliminar(Request request, Response response) {
        mensajeSucces = "Se ha podido eliminar la parada";
        mensajeDanger = "No se ha podido eliminar la parada";

        paradaRepositorio = FactoryRepositorio.get(Parada.class);

        String id_parada = request.params("id_parada");
        //int idTransporte = Integer.parseInt(request.params("id_transporte"));

        if(id_parada != null) {
            Parada parada = paradaRepositorio.buscar(Integer.parseInt(id_parada));

            if(parada != null) {
                parada.setEstado(Estado.INACTIVO);
                paradaRepositorio.modificar(parada);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                //response.redirect("/transportes/publicos/" + idTransporte + "/paradas");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        //response.redirect("/transportes/publicos/" + idTransporte + "/paradas");
        return response;
    }


    public ModelAndView ver(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        paradaRepositorio = FactoryRepositorio.get(Parada.class);

        int idTransporte = Integer.parseInt(request.params("id_transporte"));
        int idParada = Integer.parseInt(request.params("id_parada"));
        Parada parada = paradaRepositorio.buscar(idParada);

        String url = "/transportes/publicos/" + idTransporte + "/paradas";

        parametros.put("admin", true);
        parametros.put("parada", parada);
        parametros.put("ver", url);
        parametros.put("provincia", ServicioDistancia.instancia().getProvincia(parada.getDireccion().getProvincia()));
        parametros.put("municipio", ServicioDistancia.instancia().getMunicipio(parada.getDireccion().getProvincia(), parada.getDireccion().getMunicipio()));
        parametros.put("localidad", ServicioDistancia.instancia().getLocalidad(parada.getDireccion().getMunicipio(), parada.getDireccion().getLocalidad()));


        return new ModelAndView(parametros,"/medios_transporte/parada.hbs");
    }
}
