package domain.controllers;

import com.google.gson.Gson;

import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.HuellaCarbono.*;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.SectorTerritorial.AgenteSectorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialMunicipal;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialProvincial;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.RepositorioAgenteSectorial;
import domain.modelo.repositories.RepositorioOrganizacion;
import domain.modelo.repositories.factories.FactoryRepositorio;
import domain.modelo.repositories.factories.FactoryRepositorioAgenteSectorial;
import domain.modelo.repositories.factories.FactoryRepositorioOrganizacion;
import org.hibernate.Hibernate;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class ReporteController {
    private RepositorioOrganizacion repositorioOrganizacion;

    public String mostrarReporteOrganizacion(Request request, Response response) {
        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        int id_organizacion = Integer.parseInt(request.params("id_organizacion"));
        Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);

        try {
            HistorialHC historialHC = new HistorialHC();

            organizacion.getHistorialHC().stream()
                    .filter(hc -> hc.getPeriodicidad()== Periodicidad.ANUAL)
                    .forEach(historialHC::agregar);

            Gson gson = new Gson();

            String json = gson.toJson(historialHC);
            response.type("application/json");

            return json;
        }
        catch (RuntimeException e){
            return null;
        }
    }

    public String mostrarReporteComposicionOrganizacion(Request request, Response response) {
        CalculadoraComposicionHCOrganizacion calculadoraComposicionHCOrganizacion = new CalculadoraComposicionHCOrganizacion();
        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        int id_organizacion = Integer.parseInt(request.params("id_organizacion"));
        Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);

        String periodo = request.params("periodo").replace('-','/');

        try {
            ListadoComponentesST listadoComponentes = new ListadoComponentesST();
            List<ComponenteHCOrganizacion> componentes =
                    calculadoraComposicionHCOrganizacion.calcularComposicionHC(organizacion, Periodicidad.ANUAL, periodo);
            componentes.forEach(listadoComponentes::agregarComponenteOrganizacion);

            Gson gson = new Gson();
            String json = gson.toJson(listadoComponentes);
            response.type("application/json");

            return json;
        }
        catch (RuntimeException e){
            return null;
        }
    }

    public String mostrarReporteEvolucionAgente(Request request, Response response) {
        RepositorioAgenteSectorial repositorioAS = FactoryRepositorioAgenteSectorial.get();

        AgenteSectorial agente = repositorioAS.buscar(Integer.parseInt(request.params("id_agente")));
        SectorTerritorial sectorTerritorial = agente.getSectorTerritorial();

        Repositorio<HC> repoHC = FactoryRepositorio.get(HC.class);
        List<HC> huellasCarbono = repoHC.buscarTodos();
        List<HC> hcAgente = huellasCarbono.stream().filter(hc -> hc.getSectorTerritorial().equals(sectorTerritorial)).collect(Collectors.toList());

        List<HC> hcAnuales = hcAgente.stream()
                .filter(hc -> hc.getPeriodicidad().equals(Periodicidad.ANUAL))
                .collect(Collectors.toList());
        Collections.sort(hcAnuales, Comparator.comparing(HC::getPeriodo));

        try {
            HistorialHC historialHC = new HistorialHC();
            hcAnuales.stream().forEach(historialHC::agregar);

            Gson gson = new Gson();

            String json = gson.toJson(historialHC);
            response.type("application/json");

            return json;
        }
        catch (RuntimeException e){
            return null;
        }
    }

    public String mostrarReporteComposicionAgente(Request request, Response response) {
        RepositorioAgenteSectorial repositorioAS = FactoryRepositorioAgenteSectorial.get();

        AgenteSectorial agente = repositorioAS.buscar(Integer.parseInt(request.params("id_agente")));
        SectorTerritorial sectorTerritorial = agente.getSectorTerritorial();

        Periodicidad periodicidad = Periodicidad.valueOf(request.params("periodicidad"));
        String periodo = request.params("periodo").replace('-','/');

        //Pruebo valores harcodeados porque no me lee los de la calculadora
/*        String mostaza = "Mostaza";
        String bk = "BK";
        String mcdonalds = "mcdonalds";

        List<String> orgs = new ArrayList<>();
        orgs.add(mostaza);
        orgs.add(bk);
        orgs.add(mcdonalds);

        List<Double> val = new ArrayList<>();
        val.add(3.0);
        val.add(20.4);
        val.add(13.2);

        ComposicionST componentes = new ComposicionST(orgs, val);*/

        try {
            ListadoComponentesST listadoComponentes = new ListadoComponentesST();

            //creo que el problema esta en que al momento de llamar al metodo hibernate no llega a popular las herencias y los deja en un proxy
            //Hibernate.unproxy(sectorTerritorial);
            List<ComponenteHCSectorTerritorial> componentes = sectorTerritorial.calcularComposicionHC(periodicidad, periodo);
            componentes.stream().forEach(listadoComponentes::agregar);
            Gson gson = new Gson();

            String json = gson.toJson(listadoComponentes);
            response.type("application/json");

            return json;
        }
        catch (RuntimeException e){
            return null;
        }
    }
}

//Para testear
class ComposicionST {
    private List<String> composiciones = new ArrayList();
    private List<Double> valores = new ArrayList();

    public ComposicionST(List<String> composiciones, List<Double> valores) {
        this.composiciones = composiciones;
        this.valores = valores;
    }
}
