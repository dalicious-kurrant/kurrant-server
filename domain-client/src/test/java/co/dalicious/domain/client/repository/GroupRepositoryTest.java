package co.dalicious.domain.client.repository;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.ApartmentSpot;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.util.DiningType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DataJpaTest
class GroupRepositoryTest {
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private ApartmentSpotRepository apartmentSpotRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private SpotRepository spotRepository;

    @BeforeEach
    void beforeEach(){
        CreateAddressRequestDto addressDto = new CreateAddressRequestDto();
        addressDto.setZipCode("12312");
        addressDto.setAddress1("자이아파트");
        addressDto.setAddress2("403동");

        Address address = Address.builder()
                .createAddressRequestDto(addressDto)
                .build();

        Apartment apartment = new Apartment(address,
                convertToEntityAttribute("1, 2"),
                addressDto.getAddress1(),
                BigInteger.ONE,
                200);
        // when
        apartmentRepository.save(apartment);
    }

    @Test
    @DisplayName("아파트가 그룹에 추가되었을 때, 그룹 테이블 값 확인")
    void test1() {
        // given
        CreateAddressRequestDto addressDto = new CreateAddressRequestDto();
        addressDto.setZipCode("12312");
        addressDto.setAddress1("자이아파트");
        addressDto.setAddress2("403동");

        Address address = Address.builder()
                .createAddressRequestDto(addressDto)
                .build();

        Apartment apartment = new Apartment(address,
                convertToEntityAttribute("1, 2"),
                addressDto.getAddress1(),
                BigInteger.ONE,
                200);
        // when
        Apartment savedApartment = apartmentRepository.save(apartment);

        // then
        System.out.println("savedApartment = " + savedApartment);
        List<Group> groups = groupRepository.findAll();
        for (Group group : groups) {
            System.out.println("group = " + group);
            System.out.println("group.getName() = " + group.getName());
        }
        int apartmentCount = apartmentRepository.findAll().size();
        int groupCount = groupRepository.findAll().size();
        Assertions.assertEquals(apartmentCount, 1);
        Assertions.assertEquals(groupCount, 1);
    }

    @Test
    @DisplayName("스팟에서 dtype 꺼내오기")
    void test2() {
        // given
        CreateAddressRequestDto addressDto = new CreateAddressRequestDto();
        addressDto.setZipCode("12312");
        addressDto.setAddress1("자이아파트");
        addressDto.setAddress2("403동");

        Address address = Address.builder()
                .createAddressRequestDto(addressDto)
                .build();
        ApartmentSpot apartmentSpot = ApartmentSpot.builder()
                .address(address)
                .group(apartmentRepository.findById(BigInteger.ONE).orElseThrow(() -> new IllegalArgumentException("error")))
                .diningTypes(convertToEntityAttribute("1, 2"))
                .name("101동")
                .build();
        apartmentSpotRepository.save(apartmentSpot);
        Spot spot = spotRepository.findById(BigInteger.ONE).orElseThrow(
                () -> new IllegalArgumentException("spot error")
        );

        System.out.println("spot.getClass() = " + spot.getClass());

    }

    public static List<DiningType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] diningTypeStrings = dbData.split(", ");
        List<DiningType> diningTypes = new ArrayList<>();

        for (String diningTypeString : diningTypeStrings) {
            diningTypes.add(DiningType.ofCode(Integer.parseInt(diningTypeString)));
        }
        return diningTypes;
    }
}