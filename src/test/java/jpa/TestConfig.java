package jpa;

import org.junit.jupiter.api.BeforeEach;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class TestConfig {
    protected static EntityManagerFactory emf;
    protected static EntityManager em;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpaexample");
        em = emf.createEntityManager();
    }
}
