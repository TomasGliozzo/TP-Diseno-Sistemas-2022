package Persistencia;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import org.uqbarproject.jpa.java8.extras.test.AbstractPersistenceTest;

public class ContextTest extends AbstractPersistenceTest implements WithGlobalEntityManager {
    @Test
    public void ContextUp(){
        Assertions.assertNotNull(entityManager());
    }
    @Test
    public void ContextUpWithTransaction(){
        withTransaction(()->{});
    }
}
