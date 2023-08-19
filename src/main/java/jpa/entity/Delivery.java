package jpa.entity;

import javax.persistence.*;

@Entity
@Table(name="DELIVERY")
public class Delivery {
    @Id @GeneratedValue
    private Long id;
    @OneToOne(mappedBy = "delivery")
    private Order order;
    @Embedded
    private Address address;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Delivery{" +
            "id=" + id +
            ", order=" + order +
            ", address=" + address +
            ", status=" + status +
            '}';
    }
}
