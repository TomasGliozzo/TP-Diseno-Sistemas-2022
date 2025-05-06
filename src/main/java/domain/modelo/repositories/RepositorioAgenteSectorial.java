package domain.modelo.repositories;

import db.EntityManagerHelper;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.SectorTerritorial.AgenteSectorial;
import domain.modelo.repositories.daos.DAO;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class RepositorioAgenteSectorial extends Repositorio<AgenteSectorial>{

    public RepositorioAgenteSectorial(DAO<AgenteSectorial> dao) {
        super(dao);
    }

    public AgenteSectorial buscarPorNombre(String nombre) {
        return this.dao.buscar(condicionNombre(nombre));
    }

    private BusquedaCondicional condicionNombre(String nombre) {
        CriteriaBuilder criteriaBuilder = EntityManagerHelper.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AgenteSectorial> query = criteriaBuilder.createQuery(AgenteSectorial.class);

        Root<AgenteSectorial> condicionRaiz = query.from(AgenteSectorial.class);

        Predicate condicionNombre = criteriaBuilder.equal(condicionRaiz.get("nombreApellido"), nombre);
        query.where(condicionNombre);

        return new BusquedaCondicional(null, query);
    }

    public AgenteSectorial buscarAgentePorUsuario(int idUsuario) {
        return this.dao.buscar(condicionUsuario(idUsuario));
    }

    private BusquedaCondicional condicionUsuario(int idUsuario) {
        CriteriaBuilder criteriaBuilder = EntityManagerHelper.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AgenteSectorial> usuarioQuery = criteriaBuilder.createQuery(AgenteSectorial.class);

        Root<AgenteSectorial> condicionRaiz = usuarioQuery.from(AgenteSectorial.class);
        Predicate condicionIdUsuario = criteriaBuilder.equal(condicionRaiz.get("usuario"), idUsuario);
        usuarioQuery.where(condicionIdUsuario);

        return new BusquedaCondicional(null, usuarioQuery);
    }


    /*
    public List<AgenteSectorial> buscarAgentesNoAsignados() {

        List<AgenteSectorial> agentes = EntityManagerHelper.createQuery("FROM agente_sectorial WHERE sector_territorial_id = " + "null").getResultList();
        return agentes;
    }
 */

}
