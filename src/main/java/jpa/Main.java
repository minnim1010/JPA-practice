package jpa;

import jpa.entity.*;

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

        try{
            tx.begin();
            logic();
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }

        em.close();
        emf.close();
    }

    static private void logic(){
        initialLogic();
        Long orderId = 1L;
        Order order = em.find(Order.class, orderId);
        System.out.println(order.toString());

        Member member = order.getMember();
        System.out.println(member.toString());
    }

    static private void initialLogic(){
        Member member = new Member();
        member.setName("mj");
        em.persist(member);

        Item apple = new Item();
        apple.setName("apple");
        apple.setPrice(1000);
        apple.setStockQuantity(100);
        em.persist(apple);

        Item candy = new Item();
        candy.setName("candy");
        candy.setPrice(200);
        candy.setStockQuantity(1000);
        em.persist(candy);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setItem(apple);
        orderItem1.setCount(2);
        orderItem1.setOrderPrice(orderItem1.getItem().getPrice() * orderItem1.getCount());
        em.persist(orderItem1);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setItem(candy);
        orderItem2.setCount(10);
        orderItem2.setOrderPrice(orderItem2.getItem().getPrice() * orderItem2.getCount());
        em.persist(orderItem2);

        Order order = new Order();
        order.setMember(member);
        order.setOrderDate(new Date());
        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);
        order.setStatus(Enum.valueOf(OrderStatus.class, "ORDER"));
        em.persist(order);
    }

}