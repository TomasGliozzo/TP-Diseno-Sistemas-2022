package domain.modelo.repositories.factories;

import Config.Config;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.repositories.daos.DAOHibernate;
import domain.modelo.repositories.RepositorioOrganizacion;
import domain.modelo.repositories.daos.DAO;

public class FactoryRepositorioOrganizacion {
    private static RepositorioOrganizacion repo;

    static {
        repo = null;
    }

    public static RepositorioOrganizacion get(){
        if(repo == null){
            if(Config.useDataBase){
                DAO<Organizacion> dao = new DAOHibernate<>(Organizacion.class);
                repo = new RepositorioOrganizacion(dao);
            }
        }
        return repo;
    }
}
