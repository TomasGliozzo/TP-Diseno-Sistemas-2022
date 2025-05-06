package domain.modelo.repositories.factories;

import Config.Config;
import domain.modelo.entities.Entidades.Usuario.Rol;
import domain.modelo.repositories.daos.DAO;
import domain.modelo.repositories.daos.DAOHibernate;

public class FactoryRepositorioRol {
    private static RepositorioRol repo;

    static {
        repo = null;
    }

    public static RepositorioRol get(){
        if(repo == null){
            if(Config.useDataBase){
                DAO<Rol> dao = new DAOHibernate<>(Rol.class);
                repo = new RepositorioRol(dao);
            }
        }
        return repo;
    }
}
