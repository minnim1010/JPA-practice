package jpa.entity;

import jpa.entity.base.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends BaseEntity {
    @Id @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;
    private String name;
    @Embedded
    private Address address;
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Member{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", address=" + address +
            ", orders=" + orders +
            '}';
    }
}
