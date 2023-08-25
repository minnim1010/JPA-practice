package jpa.entity;

import jpa.JpaExecutionInterface;
import jpa.TestConfig;
import jpa.entity.item.Album;
import jpa.entity.item.Book;
import jpa.entity.item.Item;
import jpa.entity.item.Movie;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest extends TestConfig {

    @AfterEach
    void tearDown() {
        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.createQuery("DELETE FROM OrderItem oi").executeUpdate();
            em.createQuery("DELETE FROM Order o").executeUpdate();
            em.createQuery("DELETE FROM Item i").executeUpdate();
        });
        em.clear();
    }

    @DisplayName("상품, 주문 상품 목록, 주문을 생성한다.")
    @Test
    void test1() {
        //given
        Member member = createAndSaveMember();

        createAndSaveAlbum();
        createAndSaveBook();
        createAndSaveMovie();

        String query = "SELECT i FROM Item i";
        List<Item> items = em.createQuery(query, Item.class).getResultList();
        Album album = (Album) items.get(0);
        Book book = (Book) items.get(1);
        Movie movie = (Movie) items.get(2);

        OrderItem orderItem1 = new OrderItem(album, 3);
        OrderItem orderItem2 = new OrderItem(book, 2);
        OrderItem orderItem3 = new OrderItem(movie, 1);

        Date orderDate = Date.valueOf(LocalDate.of(2023, 8, 24));
        Order order = new Order(member, orderDate, OrderStatus.ORDER);
        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);
        order.addOrderItem(orderItem3);

        //when
        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(order);
        });

        //then
        em.clear();
        query = "SELECT o FROM Order o";
        List<Order> orders = em.createQuery(query, Order.class).getResultList();

        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(2023, 8, 24, 0, 0, 0));
        assertThat(orders).hasSize(1)
            .extracting("member.name", "orderDate")
            .contains(
                Tuple.tuple(member.getName(), timestamp)
            );

        List<OrderItem> orderItems = orders.get(0).getOrderItems();
        assertThat(orderItems).hasSize(3)
            .extracting("item.name", "orderPrice")
            .containsExactlyInAnyOrder(
                Tuple.tuple("Album1", 30000),
                Tuple.tuple("Book1", 40000),
                Tuple.tuple("Movie1", 8000)
            );
    }

    @DisplayName("상품, 주문 상품 목록, 주문 여러 개를 생성하고, 여러 주문의 주문 상품을 가져올 시 N+1문제 발생")
    @Test
    void test2() {
        //given
        Member member = createAndSaveMember();

        createAndSaveAlbum();
        createAndSaveBook();
        createAndSaveMovie();

        String query = "SELECT i FROM Item i";
        List<Item> items = em.createQuery(query, Item.class).getResultList();
        Album album = (Album) items.get(0);
        Book book = (Book) items.get(1);
        Movie movie = (Movie) items.get(2);

        OrderItem orderItem1_1 = new OrderItem(album, 3);
        OrderItem orderItem1_2 = new OrderItem(book, 3);
        OrderItem orderItem1_3 = new OrderItem(book, 3);
        OrderItem orderItem2 = new OrderItem(book, 2);
        OrderItem orderItem3 = new OrderItem(movie, 1);
        OrderItem orderItem4 = new OrderItem(album, 1);

        Date orderDate = Date.valueOf(LocalDate.of(2023, 8, 24));
        Order order1 = new Order(member, orderDate, OrderStatus.ORDER);
        Order order2 = new Order(member, orderDate, OrderStatus.ORDER);
        Order order3 = new Order(member, orderDate, OrderStatus.ORDER);
        Order order4 = new Order(member, orderDate, OrderStatus.ORDER);
        order1.addOrderItem(orderItem1_1);
        order1.addOrderItem(orderItem1_2);
        order1.addOrderItem(orderItem1_3);
        order2.addOrderItem(orderItem2);
        order3.addOrderItem(orderItem3);
        order4.addOrderItem(orderItem4);

        //when
        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(order1);
            em.persist(order2);
            em.persist(order3);
            em.persist(order4);
        });

        //then
        em.clear();
        query = "SELECT o FROM Order o";
        List<Order> orders = em.createQuery(query, Order.class).getResultList();

        assertThat(orders).hasSize(4);

        int[] orderItemsSize = {3, 1, 1, 1};
        for (int i = 0; i < orders.size(); i++) {
            List<OrderItem> orderItems = orders.get(i).getOrderItems();
            assertThat(orderItems).hasSize(orderItemsSize[i]);
        }
    }


    /*
        select
            distinct
            order0_.ORDER_ID as order_id1_6_0_,
            orderitems1_.ORDER_ITEM_ID as order_it1_5_1_,
            order0_.createdDate as createdd2_6_0_,
            order0_.lastModifiedDate as lastmodi3_6_0_,
            order0_.DELIVERY_ID as delivery6_6_0_,
            order0_.MEMBER_ID as member_i7_6_0_,
            order0_.orderDate as orderdat4_6_0_,
            order0_.status as status5_6_0_,
            orderitems1_.count as count2_5_1_,
            orderitems1_.ITEM_ID as item_id4_5_1_,
            orderitems1_.ORDER_ID as order_id5_5_1_,
            orderitems1_.orderPrice as orderpri3_5_1_,
            orderitems1_.ORDER_ID as order_id5_5_0__,
            orderitems1_.ORDER_ITEM_ID as order_it1_5_0__
        from
            ORDERS order0_
        inner join
            ORDER_ITEM orderitems1_
                on order0_.ORDER_ID=orderitems1_.ORDER_ID
     */
    @DisplayName("상품, 주문 상품 목록, 주문 여러 개를 생성하고, 여러 주문의 주문 상품을 Fetch Join으로 가져온다.")
    @Test
    void test3() {
        //given
        Member member = createAndSaveMember();

        createAndSaveAlbum();
        createAndSaveBook();
        createAndSaveMovie();

        String query = "SELECT i FROM Item i";
        List<Item> items = em.createQuery(query, Item.class).getResultList();
        Album album = (Album) items.get(0);
        Book book = (Book) items.get(1);
        Movie movie = (Movie) items.get(2);

        OrderItem orderItem1_1 = new OrderItem(album, 3);
        OrderItem orderItem1_2 = new OrderItem(book, 3);
        OrderItem orderItem1_3 = new OrderItem(book, 3);
        OrderItem orderItem2 = new OrderItem(book, 2);
        OrderItem orderItem3 = new OrderItem(movie, 1);
        OrderItem orderItem4 = new OrderItem(album, 1);

        Date orderDate = Date.valueOf(LocalDate.of(2023, 8, 24));
        Order order1 = new Order(member, orderDate, OrderStatus.ORDER);
        Order order2 = new Order(member, orderDate, OrderStatus.ORDER);
        Order order3 = new Order(member, orderDate, OrderStatus.ORDER);
        Order order4 = new Order(member, orderDate, OrderStatus.ORDER);
        order1.addOrderItem(orderItem1_1);
        order1.addOrderItem(orderItem1_2);
        order1.addOrderItem(orderItem1_3);
        order2.addOrderItem(orderItem2);
        order3.addOrderItem(orderItem3);
        order4.addOrderItem(orderItem4);

        //when
        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(order1);
            em.persist(order2);
            em.persist(order3);
            em.persist(order4);
        });

        //then
        em.clear();
        query = "SELECT DISTINCT o FROM Order o JOIN FETCH o.orderItems";
        List<Order> orders = em.createQuery(query, Order.class).getResultList();

        assertThat(orders).hasSize(4);

        int[] orderItemsSize = {3, 1, 1, 1};
        for (int i = 0; i < orders.size(); i++) {
            List<OrderItem> orderItems = orders.get(i).getOrderItems();
            assertThat(orderItems).hasSize(orderItemsSize[i]);
        }
    }

    private void createAndSaveMovie() {
        Movie movie = Movie.builder()
            .name("Movie1")
            .price(8000)
            .stockQuantity(10)
            .actor("actor")
            .director("director")
            .build();

        JpaExecutionInterface.execute(em.getTransaction(), () -> em.persist(movie));
    }

    private void createAndSaveBook() {
        Book book = Book.builder()
            .name("Book1")
            .price(20000)
            .stockQuantity(1000)
            .author("author")
            .isbn("2224-33s")
            .build();

        JpaExecutionInterface.execute(em.getTransaction(), () -> em.persist(book));
    }

    private void createAndSaveAlbum() {
        Album album = Album.builder()
            .name("Album1")
            .price(10000)
            .stockQuantity(100)
            .artist("artist")
            .etc("etc")
            .build();

        JpaExecutionInterface.execute(em.getTransaction(), () -> em.persist(album));
    }

    private Member createAndSaveMember() {
        Member member = Member.builder()
            .name("member")
            .address(Address.builder()
                .city("서울")
                .street("ㅁㅁ로")
                .zipcode("13243")
                .build())
            .build();

        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(member);
        });

        return em.find(Member.class, 1L);
    }
}