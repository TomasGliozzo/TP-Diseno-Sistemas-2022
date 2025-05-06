package domain.modelo.repositories;

import db.EntityManagerHelper;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Usuario.Rol;
import domain.modelo.repositories.daos.DAO;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class RepositorioOrganizacion extends Repositorio<Organizacion> {

    public RepositorioOrganizacion(DAO<Organizacion> dao) {
        super(dao);
    }

    public Boolean existe(int id) {
        return buscar(id) != null;
    }

    public Organizacion buscarOrganizacionPorUsuario(int id) {
        return this.dao.buscar(condicionUsuario(id));
    }

    private BusquedaCondicional condicionUsuario(int id) {
        CriteriaBuilder criteriaBuilder = EntityManagerHelper.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Organizacion> query = criteriaBuilder.createQuery(Organizacion.class);

        Root<Organizacion> condicionRaiz = query.from(Organizacion.class);

        Predicate condicionNombre = criteriaBuilder.equal(condicionRaiz.get("usuario"), id);
        query.where(condicionNombre);

        return new BusquedaCondicional(null, query);
    }

    public Organizacion buscar(int id) {
        return this.dao.buscar(condicion(id));
    }

    public Organizacion buscarOrganizacionPorNombre(String nombre) {
        return this.dao.buscar(condicionNombre(nombre));
    }

    private BusquedaCondicional condicionNombre(String nombre) {
        CriteriaBuilder criteriaBuilder = EntityManagerHelper.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Organizacion> query = criteriaBuilder.createQuery(Organizacion.class);

        Root<Organizacion> condicionRaiz = query.from(Organizacion.class);

        Predicate condicionNombre = criteriaBuilder.equal(condicionRaiz.get("razonSocial"), nombre);
        query.where(condicionNombre);

        return new BusquedaCondicional(null, query);
    }

    private BusquedaCondicional condicion(int id) {
        CriteriaBuilder criteriaBuilder = EntityManagerHelper.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Organizacion> query = criteriaBuilder.createQuery(Organizacion.class);

        Root<Organizacion> condicionRaiz = query.from(Organizacion.class);

        Predicate condicionNombre = criteriaBuilder.equal(condicionRaiz.get("id"), id);
        query.where(condicionNombre);

        return new BusquedaCondicional(null, query);
    }
}
