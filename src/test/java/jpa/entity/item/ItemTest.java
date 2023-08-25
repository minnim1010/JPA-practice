package jpa.entity.item;

import jpa.JpaExecutionInterface;
import jpa.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.Query;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest extends TestConfig {

    @AfterEach
    void tearDown() {
        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            Query memberDelete = em.createQuery("DELETE FROM Item i");
            memberDelete.executeUpdate();
        });
        em.clear();
    }

    @DisplayName("상품을 생성한다.")
    @Test
    void test1() {
        //given
        Album album = Album.builder()
            .name("album1")
            .price(10000)
            .stockQuantity(100)
            .artist("artist")
            .etc("etc")
            .build();

        Book book = Book.builder()
            .name("Book1")
            .price(20000)
            .stockQuantity(1000)
            .author("author")
            .isbn("2224-33s")
            .build();

        Movie movie = Movie.builder()
            .name("movie1")
            .price(8000)
            .stockQuantity(10)
            .actor("actor")
            .director("director")
            .build();

        //when
        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(album);
            em.persist(book);
            em.persist(movie);
        });

        //then
        String query = "SELECT i FROM Item i";
        List<Item> resultList = em.createQuery(query, Item.class).getResultList();

        assertThat(resultList).hasSize(3)
            .extracting("price")
            .containsExactly(10000, 20000, 8000);
    }
}