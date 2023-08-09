package jpa.entity.item;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@DiscriminatorValue("A")
@Getter
@Setter
@Entity
public class Album extends Item{
    private String artist;
    private String etc;
}
