package jpa.entity.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@SuperBuilder
@NoArgsConstructor
@DiscriminatorValue("B")
@Entity
public class Book extends Item{
    private String author;
    private String isbn;
}
