package domain.modelo.repositories;

import db.EntityManagerHelper;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.repositories.daos.DAO;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public class RepositorioMiembro extends Repositorio<Miembro> {
    public RepositorioMiembro(DAO<Miembro> dao) {
        super(dao);
    }

    public Boolean existe(int idUsuario){
        return buscarMiembroPorUsuario(idUsuario) != null;
    }

    public Miembro buscarMiembroPorUsuario(int idUsuario) {
        return this.dao.buscar(condicionUsuario(idUsuario));
    }

    private BusquedaCondicional condicionUsuario(int idUsuario) {
        CriteriaBuilder criteriaBuilder = EntityManagerHelper.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Miembro> usuarioQuery = criteriaBuilder.createQuery(Miembro.class);

        Root<Miembro> condicionRaiz = usuarioQuery.from(Miembro.class);
        Predicate condicionIdUsuario = criteriaBuilder.equal(condicionRaiz.get("usuario"), idUsuario);
        usuarioQuery.where(condicionIdUsuario);

        return new BusquedaCondicional(null, usuarioQuery);
    }
}
