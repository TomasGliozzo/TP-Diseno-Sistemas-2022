package domain.modelo.repositories;

import db.EntityManagerHelper;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.repositories.daos.DAO;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public class RepositorioUsuarios extends Repositorio<Usuario> {

    public RepositorioUsuarios(DAO<Usuario> dao) {
        super(dao);
    }

    public Usuario buscarUsuarioPorNombre(String nombre) {
        return this.dao.buscar(condicionNombre(nombre));
    }

    private BusquedaCondicional condicionNombre(String nombre) {
        CriteriaBuilder criteriaBuilder = EntityManagerHelper.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Usuario> query = criteriaBuilder.createQuery(Usuario.class);

        Root<Usuario> condicionRaiz = query.from(Usuario.class);

        Predicate condicionNombre = criteriaBuilder.equal(condicionRaiz.get("nombre"), nombre);
        query.where(condicionNombre);

        return new BusquedaCondicional(null, query);
    }

    public Boolean existe(String nombreDeUsuario, String contrasenia) {
        CriteriaBuilder qb = EntityManagerHelper.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root<Usuario> condicionRaiz = cq.from(Usuario.class);

        cq.select(qb.count(condicionRaiz));

        Predicate condicionNombreDeUsuario = qb.equal(condicionRaiz.get("nombre"), nombreDeUsuario);
        Predicate condicionContrasenia = qb.equal(condicionRaiz.get("contrasenia"), contrasenia);

        Predicate condicionExisteUsuario = qb.and(condicionNombreDeUsuario, condicionContrasenia);

        cq.where(condicionExisteUsuario);

        return EntityManagerHelper.getEntityManager().createQuery(cq).getSingleResult() > 0;
    }

    public Usuario buscarUsuario(String nombreDeUsuario, String contrasenia){
        System.out.println(this.dao.buscar(condicionUsuarioYContrasenia(nombreDeUsuario, contrasenia)));
        return this.dao.buscar(condicionUsuarioYContrasenia(nombreDeUsuario, contrasenia));
    }

    private BusquedaCondicional condicionUsuarioYContrasenia(String nombreDeUsuario, String contrasenia){
        CriteriaBuilder criteriaBuilder = EntityManagerHelper.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Usuario> usuarioQuery = criteriaBuilder.createQuery(Usuario.class);

        Root<Usuario> condicionRaiz = usuarioQuery.from(Usuario.class);

        Predicate condicionNombreDeUsuario = criteriaBuilder.equal(condicionRaiz.get("nombre"), nombreDeUsuario);
        Predicate condicionContrasenia = criteriaBuilder.equal(condicionRaiz.get("contrasenia"), contrasenia);

        Predicate condicionExisteUsuario = criteriaBuilder.and(condicionNombreDeUsuario, condicionContrasenia);

        usuarioQuery.where(condicionExisteUsuario);

        return new BusquedaCondicional(null, usuarioQuery);
    }
}