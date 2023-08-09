package jpa.entity;

import jpa.entity.base.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="ORDERS")
public class Order extends BaseEntity {
    @Id @GeneratedValue
    @Column(name="ORDER_ID")
    private Long id;
    @ManyToOne
    @JoinColumn(name="MEMBER_ID")
    private Member member;
    @OneToMany(mappedBy = "order")
    private List<OrderItem> OrderItems = new ArrayList<>();
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    // 1대1 관계이지만, 주문 -> 배송 접근이 잦으므로 외래키는 여기에 두었음.
    @OneToOne
    @JoinColumn(name="DELIVERY_ID")
    private Delivery delivery;

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    // 다대일 관계이므로 객체 그래프 탐색을 위한 연관 객체 관리
    public void setMember(Member member) {
        if(this.member != null)
            this.member.getOrders().remove(this);
        this.member = member;
        member.getOrders().add(this);
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItems() {
        return OrderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        OrderItems = orderItems;
    }

    public void addOrderItem(OrderItem orderItem){
        this.getOrderItems().add(orderItem);
        orderItem.setOrder(this);
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", OrderItems=" + OrderItems +
                ", orderDate=" + orderDate +
                ", status=" + status +
                '}';
    }
}
