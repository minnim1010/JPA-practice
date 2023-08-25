package jpa;

import javax.persistence.EntityTransaction;

public interface JpaExecutionInterface<T> {

    public static void execute(EntityTransaction tx, Runnable function) {
        tx.begin();

        function.run();

        tx.commit();
    }
}
