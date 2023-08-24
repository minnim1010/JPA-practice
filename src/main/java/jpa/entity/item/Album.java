package jpa.entity.item;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@SuperBuilder
@NoArgsConstructor
@DiscriminatorValue("A")
@Entity
public class Album extends Item{
    private String artist;
    private String etc;
}
