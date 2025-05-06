package domain.modelo.repositories.factories;

import Config.Config;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.repositories.daos.DAO;
import domain.modelo.repositories.daos.DAOHibernate;
import domain.modelo.repositories.RepositorioMiembro;


public class FactoryRepositorioMiembro {
    private static RepositorioMiembro repo;

    static {
        repo = null;
    }

    public static RepositorioMiembro get(){
        if(repo == null){
            if(Config.useDataBase){
                DAO<Miembro> dao = new DAOHibernate<>(Miembro.class);
                repo = new RepositorioMiembro(dao);
            }
        }
        return repo;
    }
}
