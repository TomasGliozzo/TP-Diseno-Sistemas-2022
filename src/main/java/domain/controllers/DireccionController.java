package domain.controllers;

import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Trayecto;
import domain.modelo.entities.MedioTransporte.TransportePublico.Parada;
import domain.modelo.entities.MedioTransporte.TransportePublico.TransportePublico;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Municipio;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Provincia;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.factories.FactoryRepositorio;
import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DireccionController {

    public ModelAndView mostrarCargaDireccionTramo(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        int idMiembro = Integer.parseInt(request.params("id"));
        int idTrayecto = Integer.parseInt(request.params("id_trayecto"));

        String url = "/miembro/" + idMiembro + "/trayectos/" + idTrayecto + "/tramos/crear/guardar";
        String urlProvincia = "/miembro/" + idMiembro + "/trayectos/" + idTrayecto + "/tramos/crear/provincia";
        String urlMunicipio = "/miembro/" + idMiembro + "/trayectos/" + idTrayecto + "/tramos/crear/municipio";
        String urlLocalidad = "/miembro/" + idMiembro + "/trayectos/" + idTrayecto + "/tramos/crear/localidad";
        String urlCalle = "/miembro/" + idMiembro + "/trayectos/" + idTrayecto + "/tramos/crear/calle_inicio";

        List<Provincia> provincias = ServicioDistancia.instancia().listaProvincias();

        parametros.put("miembro", idMiembro);

        parametros.put("provincia_inicio", request.session().attribute("provincia_inicio"));
        parametros.put("municipio_inicio", request.session().attribute("municipio_inicio"));
        parametros.put("localidad_inicio", request.session().attribute("localidad_inicio"));

        parametros.put("calle_inicio", request.session().attribute("calle_inicio"));
        parametros.put("numero_inicio", request.session().attribute("numero_inicio"));

        parametros.put("provincia_fin", request.session().attribute("provincia_fin"));
        parametros.put("municipio_fin", request.session().attribute("municipio_fin"));
        parametros.put("localidad_fin", request.session().attribute("localidad_fin"));

        parametros.put("calle_fin", request.session().attribute("calle_fin"));
        parametros.put("numero_fin", request.session().attribute("numero_fin"));

        parametros.put("provincias", provincias);
        parametros.put("municipios", request.session().attribute("municipios"));
        parametros.put("localidades", request.session().attribute("localidades"));

        parametros.put("url", url);
        parametros.put("urlMunicipio", urlMunicipio);
        parametros.put("urlProvincia", urlProvincia);
        parametros.put("urlLocalidad", urlLocalidad);
        parametros.put("urlCalle", urlCalle);

        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/trayectos/tramos/direccion.hbs");
    }


    public Response actualizarProvinciaInicio(Request request, Response response) {
        return this.actualizarProvincia(request, response, "inicio");
    }

    public Response actualizarProvinciaFin(Request request, Response response) {
        return this.actualizarProvincia(request, response, "fin");
    }

    public Response actualizarMunicipioInicio(Request request, Response response) {
        return this.actualizarMunicipio(request, response, "inicio");
    }

    public Response actualizarMunicipioFin(Request request, Response response) {
        return this.actualizarMunicipio(request, response, "fin");
    }

    public Response actualizarLocalidadInicio(Request request, Response response) {
        return this.actualizarLocalidad(request, response, "inicio");
    }

    public Response actualizarLocalidadFin(Request request, Response response) {
        return this.actualizarLocalidad(request, response, "fin");
    }

    public Response actualizarCalleInicio(Request request, Response response) {
        return this.actualizarCalle(request, response, "inicio");
    }

    public Response actualizarCalleFin(Request request, Response response) {
        return this.actualizarCalle(request, response, "fin");
    }

    public Response actualizarProvincia(Request request, Response response, String prefijo) {
        Integer idProvincia = Integer.valueOf(request.params("id_provincia"));

        request.session().attribute("municipios", ServicioDistancia.instancia().listaMunicipios(idProvincia));
        request.session().attribute("provincia_" + prefijo,
                ServicioDistancia.instancia().getProvincia(idProvincia));

        return response;
    }

    public Response actualizarMunicipio(Request request, Response response, String prefijo) {
        Provincia provincia = request.session().attribute("provincia_" + prefijo);
        int id_municipio = Integer.parseInt(request.params("id_municipio"));

        request.session().attribute("localidades", ServicioDistancia.instancia().listaLocalidades(id_municipio));
        request.session().attribute("municipio_" + prefijo,
                ServicioDistancia.instancia().getMunicipio(provincia.getId(), id_municipio));

        return response;
    }

    public Response actualizarLocalidad(Request request, Response response, String prefijo) {
        Integer id_localidad = Integer.valueOf(request.params("id_localidad"));
        Municipio municipio = request.session().attribute("municipio_"+prefijo);

        request.session().attribute("localidad_" + prefijo,
                ServicioDistancia.instancia().getLocalidad(municipio.getId(),id_localidad));

        return response;
    }

    public Response actualizarCalle(Request request, Response response, String prefijo) {
        String calle = request.params("calle");
        String numero = request.params("numero");

        request.session().attribute("calle_" + prefijo, calle);
        request.session().attribute("numero_" + prefijo, numero);

        return response;
    }
}
