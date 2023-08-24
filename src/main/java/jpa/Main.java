package jpa;

import jpa.entity.Member;
import jpa.entity.Order;
import jpa.entity.OrderItem;
import jpa.entity.OrderStatus;
import jpa.entity.item.Item;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Date;

public class Main {

    static EntityManagerFactory emf =
        Persistence.createEntityManagerFactory("jpaexample");
    static EntityManager em = emf.createEntityManager();


    public static void main(String[] args) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            logic();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        em.close();
        emf.close();
    }

    static private void logic() {
        initialLogic();
        Long orderId = 1L;
        Order order = em.find(Order.class, orderId);
        System.out.println(order.toString());

        Member member = order.getMember();
        System.out.println(member.toString());
    }

    static private void initialLogic() {
        Member member = Member.builder()
            .name("mj")
            .build();
        em.persist(member);

        Item apple = createItem("apple", 1000, 100);
        em.persist(apple);
        Item candy = createItem("candy", 200, 1000);
        em.persist(candy);

        OrderItem orderItem1 = createOrderItem(apple, 2);
//        em.persist(orderItem1);

        OrderItem orderItem2 = createOrderItem(candy, 10);
//        em.persist(orderItem2);

        Order order = new Order();
        order.setMember(member);
        order.setOrderDate(new Date());
        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);
        order.setStatus(Enum.valueOf(OrderStatus.class, "ORDER"));
        em.persist(order);
    }

    static private Item createItem(String name, int price, int quantity) {
        Item apple = new Item();
        apple.setName(name);
        apple.setPrice(price);
        apple.setStockQuantity(quantity);
        return apple;
    }

    static private OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice() * count);
        return orderItem;
    }

}