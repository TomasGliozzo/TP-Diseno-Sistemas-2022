package domain.modelo.repositories;

import db.EntityManagerHelper;
import domain.modelo.entities.Entidades.Organizacion.ClasificacionOrganizacion;
import domain.modelo.repositories.daos.DAO;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class RepositorioClasificacionOrganizacion extends Repositorio<ClasificacionOrganizacion> {
    public RepositorioClasificacionOrganizacion(DAO<ClasificacionOrganizacion> dao) {
        super(dao);
    }

    public Boolean existe(String nombre){
        return buscar(nombre) != null;
    }

    public ClasificacionOrganizacion buscar(String nombre){
        return this.dao.buscar(condicionNombre(nombre));
    }

    private BusquedaCondicional condicionNombre(String nombre) {
        CriteriaBuilder criteriaBuilder = EntityManagerHelper.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ClasificacionOrganizacion> query = criteriaBuilder.createQuery(ClasificacionOrganizacion.class);

        Root<ClasificacionOrganizacion> condicionRaiz = query.from(ClasificacionOrganizacion.class);

        Predicate condicionNombre = criteriaBuilder.equal(condicionRaiz.get("nombre"), nombre);
        query.where(condicionNombre);

        return new BusquedaCondicional(null, query);
    }
}
