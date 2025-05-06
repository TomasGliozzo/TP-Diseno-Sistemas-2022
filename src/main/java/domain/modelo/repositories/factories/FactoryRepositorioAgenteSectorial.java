package domain.modelo.repositories.factories;

import Config.Config;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.SectorTerritorial.AgenteSectorial;
import domain.modelo.repositories.RepositorioAgenteSectorial;
import domain.modelo.repositories.RepositorioMiembro;
import domain.modelo.repositories.daos.DAO;
import domain.modelo.repositories.daos.DAOHibernate;

public class FactoryRepositorioAgenteSectorial {
    private static RepositorioAgenteSectorial repo;

    static {
        repo = null;
    }

    public static RepositorioAgenteSectorial get(){
        if(repo == null){
            if(Config.useDataBase){
                DAO<AgenteSectorial> dao = new DAOHibernate<>(AgenteSectorial.class);
                repo = new RepositorioAgenteSectorial(dao);
            }
        }
        return repo;
    }
}
