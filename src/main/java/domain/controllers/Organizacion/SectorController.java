package domain.controllers.Organizacion;

import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.RepositorioMiembro;
import domain.modelo.repositories.RepositorioOrganizacion;
import domain.modelo.repositories.factories.FactoryRepositorio;
import domain.modelo.repositories.factories.FactoryRepositorioMiembro;
import domain.modelo.repositories.factories.FactoryRepositorioOrganizacion;
import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SectorController {
    private RepositorioOrganizacion repositorioOrganizacion;
    private RepositorioMiembro repositorioMiembro;
    private Repositorio<Sector> repositorioSector;


    public ModelAndView mostrarTodos(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        int id_organizacion = Integer.parseInt(request.params("id"));
        Organizacion organizacion = this.repositorioOrganizacion.buscar(id_organizacion);

        parametros.put("organizacion", id_organizacion);
        parametros.put("sectores", organizacion.getSectores());
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros, "organizaciones/sectores/sectores.hbs");
    }

    public ModelAndView editar(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioSector = FactoryRepositorio.get(Sector.class);

        Integer idOrganizacion = Integer.valueOf(request.params("id"));
        int idSector = Integer.parseInt(request.params("id_sector"));
        String url = "/organizacion/" + idOrganizacion + "/sectores/"+ idSector +"/editar";
        String urlMenu = "/organizacion/" + idOrganizacion + "/sectores";

        Sector sector = this.repositorioSector.buscar(idSector);
        List<Miembro> miembrosSector = sector.getMiembros();
        String nombreSector = sector.getNombre();

        parametros.put("organizacion", idOrganizacion);
        parametros.put("miembros", miembrosSector);
        parametros.put("nombre_sector", nombreSector);
        parametros.put("url", url);
        parametros.put("url_menu", urlMenu);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros, "organizaciones/sectores/sector.hbs");
    }

    public Response modificar(Request request, Response response) {
        String mensajeSucces = "Se ha podido eliminar al miembro";
        String mensajeDanger = "No se ha podido eliminar al miembro";

        repositorioSector = FactoryRepositorio.get(Sector.class);
        repositorioMiembro = FactoryRepositorioMiembro.get();

        String idMiembro = request.queryParams("eliminar");
        String idSector = request.params("id_sector");

        if(!idSector.isEmpty() && !idMiembro.isEmpty()) {
            Sector sector = repositorioSector.buscar(Integer.parseInt(idSector));
            Miembro miembro = repositorioMiembro.buscar(Integer.parseInt(idMiembro));

            if(sector != null && miembro != null) {
                sector.quitarMiembro(miembro);
                repositorioSector.modificar(sector);
                repositorioMiembro.modificar(miembro);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                response.redirect("/organizacion/" + request.params("id") + "/sectores");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        response.redirect("/organizacion/" + request.params("id") + "/sectores/"+ idSector +"/editar");
        return response;
    }

    public ModelAndView agregarSector(Request request, Response response) {
        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        int id_organizacion = Integer.parseInt(request.params("id"));
        Organizacion organizacion = this.repositorioOrganizacion.buscar(id_organizacion);

        return new ModelAndView(new HashMap<String, Object>(){{
            put("organizacion", id_organizacion);
        }}, "organizaciones/sectores/agregar-sector.hbs");
    }

    public Response crearSector(Request request, Response response) {
        String mensajeSucces = "Se ha podido crear el sector";
        String mensajeDanger = "No se ha podido crear el sector";

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        repositorioSector = FactoryRepositorio.get(Sector.class);

        int id_organizacion = Integer.parseInt(request.params("id"));

        try {
            Sector nuevoSector = new Sector();
            Organizacion organizacion = this.repositorioOrganizacion.buscar(id_organizacion);

            this.asignarParametros(nuevoSector, request);
            organizacion.agregarSector(nuevoSector);
            this.repositorioSector.agregar(nuevoSector);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/organizacion/" + request.params("id") + "/sectores");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/organizacion/" + request.params("id") + "/sectores");
        }
        finally {
            return response;
        }
    }

    private void asignarParametros(Sector sector, Request request) {
        String nombre = request.queryParams("nombre-sector");

        if( !nombre.isEmpty() ) {
            sector.setNombre(nombre);
        }
        else {
            throw new RuntimeException();
        }
    }
}
