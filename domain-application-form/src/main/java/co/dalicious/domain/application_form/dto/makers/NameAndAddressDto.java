package co.dalicious.domain.application_form.dto.makers;

import co.dalicious.domain.address.entity.embeddable.Address;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class NameAndAddressDto {
    private String name;
    private Address address;

    public NameAndAddressDto(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public boolean equals(Object obj) {
        if(obj instanceof NameAndAddressDto tmp) {
            return name.equals(tmp.name) && address.equals(tmp.address);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(name, address);
    }
}
