package jpa.entity;

import jpa.JpaExecutionInterface;
import jpa.TestConfig;
import jpa.entity.item.Album;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.TypedQuery;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest extends TestConfig {

    @AfterEach
    void tearDown() {
        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.createQuery("DELETE FROM Category c").executeUpdate();
            em.createQuery("DELETE FROM Item i").executeUpdate();
        });
        em.clear();
    }

    /*
    select
            distinct category0_.id as id1_0_0_,
            category1_.id as id1_0_1_,
            category0_.name as name2_0_0_,
            category0_.PARENT_ID as parent_i3_0_0_,
            category1_.name as name2_0_1_,
            category1_.PARENT_ID as parent_i3_0_1_
        from
            Category category0_
        inner join
            Category category1_
                on category0_.PARENT_ID=category1_.id
     */
    @DisplayName("카테고리를 계층으로 생성하고, c.parent로 Fetch Join하면 parent가 존재하는 객체들에 parent 값을 넣어 가져온다.")
    @Test
    void test() {
        //given
        Category parent = new Category("parent1", null);
        Category child1 = new Category("child1", parent);
        Category child2 = new Category("child2", parent);

        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(parent);
            em.persist(child1);
            em.persist(child2);
        });

        //when
        TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c JOIN FETCH c.parent", Category.class);
        List<Category> result = query.getResultList();

        //then
        assertThat(result).hasSize(2)
            .extracting("name")
            .containsExactlyInAnyOrder("child1", "child2");
    }


    /*
        select
            distinct category0_.id as id1_0_0_,
            child1_.id as id1_0_1_,
            category0_.name as name2_0_0_,
            category0_.PARENT_ID as parent_i3_0_0_,
            child1_.name as name2_0_1_,
            child1_.PARENT_ID as parent_i3_0_1_,
            child1_.PARENT_ID as parent_i3_0_0__,
            child1_.id as id1_0_0__
        from
            Category category0_
        left outer join
            Category child1_
                on category0_.id=child1_.PARENT_ID
     */
    @DisplayName("카테고리를 계층으로 생성하고 c.child로 Fetch Join하면 child가 존재하는 객체들을 가져온다.")
    @Test
    void test1() {
        //given
        Category parent = new Category("parent", null);
        Category child1 = new Category("child1", parent);
        Category child2 = new Category("child2", parent);
        Category child3 = new Category("child3", parent);
        Category parent2 = new Category("parent2", null);

        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(parent);
            em.persist(child1);
            em.persist(child2);
            em.persist(child3);
            em.persist(parent2);
        });

        //when
        em.clear();
        TypedQuery<Category> query = em.createQuery("SELECT DISTINCT c FROM Category c JOIN FETCH c.children", Category.class);
        Category result = query.getSingleResult();

        //then
        assertThat(result.getName()).isEqualTo("parent");
        assertThat(result.getChildren()).hasSize(3);
    }


    @DisplayName("아이템과 카테고리를 생성하고, 키테고리와 카테고리의 아이템들을 Fetch Join하여 가져오면 카테고리 아이템들이 존재한다.")
    @Test
    void test2() {
        //given
        Album album1 = createAndSaveAlbum("Album1", 1L);
        Album album2 = createAndSaveAlbum("Album2", 2L);
        Album album3 = createAndSaveAlbum("Album3", 3L);

        Category category = new Category("category", null);
        category.addItem(album1);
        category.addItem(album2);
        category.addItem(album3);

        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(category);
        });

        //when
        TypedQuery<Category> query = em.createQuery("SELECT DISTINCT c FROM Category c JOIN FETCH c.items", Category.class);
        List<Category> result = query.getResultList();

        //then
        assertThat(result).hasSize(1);
    }

    private Album createAndSaveAlbum(String name, Long key) {
        Album album = Album.builder()
            .name(name)
            .build();

        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(album);
        });

        return em.find(Album.class, key);
    }
}