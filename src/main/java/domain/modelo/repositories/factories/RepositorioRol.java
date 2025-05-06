package domain.modelo.repositories.factories;

import db.EntityManagerHelper;
import domain.modelo.entities.Entidades.Usuario.Rol;
import domain.modelo.repositories.BusquedaCondicional;
import domain.modelo.repositories.daos.DAO;
import domain.modelo.repositories.Repositorio;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public class RepositorioRol extends Repositorio<Rol> {
    public RepositorioRol(DAO<Rol> dao) {
        super(dao);
    }

    public Rol buscarRolPorNombre(String nombre) {
        return this.dao.buscar(condicionNombre(nombre));
    }

    private BusquedaCondicional condicionNombre(String nombre) {
        CriteriaBuilder criteriaBuilder = EntityManagerHelper.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Rol> query = criteriaBuilder.createQuery(Rol.class);

        Root<Rol> condicionRaiz = query.from(Rol.class);

        Predicate condicionNombre = criteriaBuilder.equal(condicionRaiz.get("nombre"), nombre);
        query.where(condicionNombre);

        return new BusquedaCondicional(null, query);
    }
}
