package co.dalicious.domain.client.service;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.ApartmentRequestDto;
import co.dalicious.domain.client.dto.ApartmentResponseDto;
import co.dalicious.domain.client.dto.CorporationRequestDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.mapper.ApartmentListMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DiningType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{
    private final ApartmentRepository apartmentRepository;
    private final ApartmentMealInfoRepository apartmentMealInfoRepository;
    private final ApartmentSpotRepository apartmentSpotRepository;
    private final CorporationRepository corporationRepository;
    private final CorporationMealInfoRepository corporationMealInfoRepository;
    private final CorporationSpotRepository corporationSpotRepository;

    @Override
    // TODO: 백오피스 구현시 추후 수정 필요
    public void createApartment(ApartmentRequestDto apartmentRequestDto) {
//        CreateAddressRequestDto addressDto = apartmentRequestDto.getAddress();
//        ApartmentRequestDto.ApartmentInfo apartmentDto = apartmentRequestDto.getApartmentInfo();
//        List<ApartmentRequestDto.Meal> meals = apartmentRequestDto.getMeals();
//
//        Address address = Address.builder()
//                .createAddressRequestDto(addressDto)
//                .build();
//
//        Apartment apartment = Apartment.builder()
//                .diningTypes(convertToEntityAttribute(apartmentDto.getDiningTypes()))
//                .name(apartmentDto.getName())
//                .familyCount(apartmentDto.getFamilyCount())
//                .address(address)
//                .build();
//
//        apartmentRepository.save(apartment);
//
//        for (ApartmentRequestDto.Meal meal : meals) {
//            ApartmentMealInfo apartmentMealInfo = (ApartmentMealInfo) MealInfo.builder()
//                    .group(apartment)
//                    .deliveryTime(DateUtils.stringToTime(meal.getDeliveryTime(), ":"))
//                    .lastOrderTime(DateUtils.stringToTime(meal.getLastOrderTime(), ":"))
//                    .diningType(DiningType.ofCode(meal.getDiningType()))
//                    .serviceDays(meal.getServiceDays())
//                    .build();
//            apartmentMealInfoRepository.save(apartmentMealInfo);
//        }
//
//        for (int i = 0; i < 3; i++) {
//            CreateAddressRequestDto spotAddressDto = new CreateAddressRequestDto();
//            spotAddressDto.setZipCode(String.valueOf(10000 + i));
//            spotAddressDto.setAddress1(addressDto.getAddress1());
//            spotAddressDto.setAddress2("스팟" + (i + 1));
//            Address spotAddress = Address.builder()
//                    .createAddressRequestDto(spotAddressDto)
//                    .build();
//            ApartmentSpot spot = (ApartmentSpot) Spot.builder()
//                    .group(apartment)
//                    .diningTypes(convertToEntityAttribute(apartmentDto.getDiningTypes()))
//                    .address(spotAddress)
//                    .name("스팟" + (i + 1))
//                    .build();
//            apartmentSpotRepository.save(spot);
//        }
    }

    // TODO: 백오피스 구현시 추후 삭제
    public void createCorporation(CorporationRequestDto corporationRequestDto) {
//        CreateAddressRequestDto addressDto = corporationRequestDto.getAddress();
//        CorporationRequestDto.CorporationInfo corporationInfo = corporationRequestDto.getCorporationInfo();
//        List<CorporationRequestDto.Meal> meals = corporationRequestDto.getMeals();
//
//        Address address = Address.builder()
//                .createAddressRequestDto(addressDto)
//                .build();
//
//        Corporation corporation = Corporation.builder()
//                .name(corporationInfo.getName())
//                .employeeCount(corporationInfo.getEmployeeCount())
//                .address(address)
//                .diningTypes(convertToEntityAttribute(corporationInfo.getDiningTypes()))
//                .build();
//
//        corporationRepository.save(corporation);
//
//        for(CorporationRequestDto.Meal meal : meals) {
//            CorporationMealInfo corporationMealInfo = CorporationMealInfo.builder()
//                    .group(corporation)
//                    .deliveryTime(DateUtils.stringToTime(meal.getDeliveryTime(), ":"))
//                    .lastOrderTime(DateUtils.stringToTime(meal.getLastOrderTime(), ":"))
//                    .diningType(DiningType.ofCode(meal.getDiningType()))
//                    .supportPrice(meal.getSupportPrice())
//                    .serviceDays(meal.getServiceDays())
//                    .build();
//            corporationMealInfoRepository.save(corporationMealInfo);
//        }
//
//        for (int i = 0; i < 3; i++) {
//            CreateAddressRequestDto spotAddressDto = new CreateAddressRequestDto();
//            spotAddressDto.setZipCode(String.valueOf(10000 + i));
//            spotAddressDto.setAddress1(addressDto.getAddress1());
//            spotAddressDto.setAddress2(String.valueOf(i + 1) + "층");
//            Address spotAddress = Address.builder()
//                    .createAddressRequestDto(spotAddressDto)
//                    .build();
//            CorporationSpot spot = (CorporationSpot) Spot.builder()
//                    .name((i + 1) + "층")
//                    .group(corporation)
//                    .diningTypes(convertToEntityAttribute(corporationInfo.getDiningTypes()))
//                    .address(spotAddress)
//                    .build();
//            corporationSpotRepository.save(spot);
//        }
    }

    public List<DiningType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] diningTypeStrings = dbData.split(", ");
        List<DiningType> diningTypes = new ArrayList<>();

        for(String diningTypeString : diningTypeStrings) {
            diningTypes.add(DiningType.ofCode(Integer.parseInt(diningTypeString)));
        }
        return diningTypes;
    }
}
