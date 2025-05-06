package domain.controllers.Usuario;

import db.EntityManagerHelper;
import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.entities.SectorTerritorial.AgenteSectorial;
import domain.modelo.repositories.RepositorioAgenteSectorial;
import domain.modelo.repositories.RepositorioMiembro;
import domain.modelo.repositories.RepositorioOrganizacion;
import domain.modelo.repositories.RepositorioUsuarios;
import domain.modelo.repositories.factories.FactoryRepositorioAgenteSectorial;
import domain.modelo.repositories.factories.FactoryRepositorioMiembro;
import domain.modelo.repositories.factories.FactoryRepositorioOrganizacion;
import domain.modelo.repositories.factories.FactoryRepositorioUsuarios;
import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;


public class LoginController {
    private RepositorioUsuarios repoUsuarios;
    private RepositorioMiembro repositorioMiembro;
    private RepositorioOrganizacion repositorioOrganizacion;

    RepositorioAgenteSectorial repositorioAgenteSectorial;


    public ModelAndView pantallaDeLogin(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        AlertaHelper.agregarAlerta(request, parametros);

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        return new ModelAndView(parametros, "/login-register/login.hbs");
    }


    public Response login(Request request, Response response) {
        String mensajeDanger = "Contrasenia o Usuario incorrecto";

        repoUsuarios = FactoryRepositorioUsuarios.get();
        repositorioMiembro = FactoryRepositorioMiembro.get();
        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        repositorioAgenteSectorial = FactoryRepositorioAgenteSectorial.get();

        try {
            Usuario solicitante = new Usuario();
            solicitante.setContrasenia(request.queryParams("contrasenia"));
            solicitante.setNombre(request.queryParams("nombreUsuario"));

            validarAtributos(solicitante);

            Usuario usuario = repoUsuarios.buscarUsuarioPorNombre(solicitante.getNombre());

            if (usuario != null && usuario.getEstado()!=Estado.INACTIVO) {
                if(usuario.getContrasenia().equals(solicitante.getContrasenia())) {
                    if(usuario.estaPenalizado()) {
                        mensajeDanger = "Debe esperar " + usuario.getTiempoPenalizacion() + " segundos para ingresar";
                        throw new RuntimeException();
                    }

                    request.session(true);
                    request.session().attribute("id", usuario.getId());


                    Miembro miembro = repositorioMiembro.buscarMiembroPorUsuario(usuario.getId());
                    Organizacion organizacion = repositorioOrganizacion.buscarOrganizacionPorUsuario(usuario.getId());
                    AgenteSectorial agente = repositorioAgenteSectorial.buscarAgentePorUsuario(usuario.getId());

                    if (miembro != null)
                        response.redirect("/miembro/" + miembro.getId() + "/index");
                    else if(organizacion != null)
                        response.redirect("/organizacion/" + organizacion.getId() + "/index");
                    else if(agente != null)
                        response.redirect("/agente-sectorial/" + agente.getId() + "/index");
                    else
                        response.redirect("/transportes/contratados");
                }
                else {
                    usuario.penalizar();
                    repoUsuarios.modificar(usuario);
                    throw new RuntimeException();
                }
            }
            else
                throw new RuntimeException();

        } catch (Exception e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/login");
        } finally {
            EntityManagerHelper.closeEntityManager();
            return response;
        }
    }

    public Response logout(Request request, Response response) {
        request.session().invalidate();
        response.redirect("/login");
        return response;
    }

    public void validarAtributos(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getContrasenia() == null) {
            throw new RuntimeException();
        }
    }

    public ModelAndView prohibido(Request request, Response response) {
        request.session().invalidate();
        return new ModelAndView(null, "/login-register/prohibido.hbs");
    }
}
