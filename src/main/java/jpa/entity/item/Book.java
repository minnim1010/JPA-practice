package jpa.entity.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@DiscriminatorValue("B")
@Getter
@Setter
@Entity
public class Book extends Item{
    private String author;
    private String isbn;
}
