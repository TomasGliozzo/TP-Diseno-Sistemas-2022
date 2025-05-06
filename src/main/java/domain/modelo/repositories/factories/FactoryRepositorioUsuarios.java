package domain.modelo.repositories.factories;

import Config.Config;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.repositories.daos.DAO;
import domain.modelo.repositories.daos.DAOHibernate;
import domain.modelo.repositories.RepositorioUsuarios;


public class FactoryRepositorioUsuarios {
    private static RepositorioUsuarios repo;

    static {
        repo = null;
    }

    public static RepositorioUsuarios get(){
        if(repo == null){
            if(Config.useDataBase){
                DAO<Usuario> dao = new DAOHibernate<>(Usuario.class);
                repo = new RepositorioUsuarios(dao);
            }
        }
        return repo;
    }
}