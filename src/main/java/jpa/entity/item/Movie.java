package jpa.entity.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@SuperBuilder
@NoArgsConstructor
@DiscriminatorValue("M")
@Entity
public class Movie extends Item{
    private String director;
    private String actor;
}
