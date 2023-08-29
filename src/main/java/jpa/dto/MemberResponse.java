package jpa.dto;

import jpa.entity.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponse {
    private Long id;
    private String name;
    private Address address;

    public MemberResponse(Long id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
}
