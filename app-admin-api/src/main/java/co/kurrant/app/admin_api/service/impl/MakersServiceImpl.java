package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.food.dto.LocationTestDto;
import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.mapper.MakersCapacityMapper;
import co.dalicious.domain.food.repository.MakersCapacityRepository;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.food.repository.QMakersCapacityRepository;

import co.dalicious.domain.food.repository.QMakersRepository;
import co.dalicious.system.enums.DiningType;
import co.kurrant.app.admin_api.dto.makers.SaveMakersRequestDto;
import co.kurrant.app.admin_api.dto.makers.SaveMakersRequestDtoList;
import co.kurrant.app.admin_api.mapper.MakersMapper;
import co.kurrant.app.admin_api.service.MakersService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.analysis.function.Add;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class MakersServiceImpl implements MakersService {

    private final MakersRepository makersRepository;
    private final QMakersCapacityRepository qMakersCapacityRepository;
    private final MakersCapacityRepository makersCapacityRepository;
    private final MakersMapper makersMapper;
    private final MakersCapacityMapper makersCapacityMapper;
    private final QMakersRepository qMakersRepository;

    @Override
    public Object findAllMakersInfo() {

        List<Makers> makersList = makersRepository.findAll();

        List<MakersInfoResponseDto> makersInfoResponseDtoList = new ArrayList<>();

        for (Makers makers : makersList) {
            //dailyCapacity 구하기
            List<MakersCapacity> makersCapacity = qMakersCapacityRepository.findByMakersId(makers.getId());
            Integer dailyCapacity = 0;
            List<String> diningTypes = new ArrayList<>();
            for (MakersCapacity capacity : makersCapacity) {
                dailyCapacity += capacity.getCapacity();
                diningTypes.add(capacity.getDiningType().getDiningType());
            }

            MakersInfoResponseDto makersInfo = makersMapper.toDto(makers, dailyCapacity, diningTypes);

            makersInfoResponseDtoList.add(makersInfo);

        }
        return makersInfoResponseDtoList;
    }

    @Override
    public void saveMakers(SaveMakersRequestDtoList saveMakersRequestDtoList) throws ParseException {

        for (SaveMakersRequestDto saveMakersRequestDto : saveMakersRequestDtoList.getSaveMakersRequestDto()) {

            //Address 생성
            Address address = new Address(saveMakersRequestDto.getZipCode(), saveMakersRequestDto.getAddress1(), saveMakersRequestDto.getAddress2(), saveMakersRequestDto.getLocation());

            Optional<Makers> optionalMakers = makersRepository.findById(saveMakersRequestDto.getId());
            //이미 존재하면 수정
            if (optionalMakers.isPresent()){

                //DailyCapacity 수정
                List<MakersCapacity> makersCapacityList = qMakersCapacityRepository.findByMakersId(saveMakersRequestDto.getId());
                //다이닝 타입별 가능수량을 계산해서 저장해준다.

                if (makersCapacityList.size() != 3){


                    for (int i = 0; i < makersCapacityList.size(); i++) {

                            Integer diningType = saveMakersRequestDto.getDiningTypes().get(i).getDiningType();
                            Integer capacity = saveMakersRequestDto.getDiningTypes().get(i).getCapacity();
                            System.out.println(capacity + " capa");
                            qMakersCapacityRepository.updateDailyCapacity(diningType, capacity, saveMakersRequestDto.getId());


                    }
                } else {
                    Integer divTen = saveMakersRequestDto.getDailyCapacity() / 10;
                    Integer morning = divTen * 3;
                    Integer lunch = divTen * 4;
                    Integer dinner = divTen * 3;

                    for (int i = 0; i < makersCapacityList.size(); i++) {
                       qMakersCapacityRepository.updateDailyCapacityDiningType(morning, lunch, dinner, makersCapacityList.get(i).getDiningType().getDiningType(), saveMakersRequestDto.getId());
                    }

                }


                //그 외 수정
                qMakersRepository.updateMakers(saveMakersRequestDto.getId(),saveMakersRequestDto.getCode(),saveMakersRequestDto.getName(),
                        saveMakersRequestDto.getCompanyName(), saveMakersRequestDto.getCeo(), saveMakersRequestDto.getCeoPhone(),
                        saveMakersRequestDto.getManagerName(), saveMakersRequestDto.getManagerPhone(), saveMakersRequestDto.getServiceType(),
                        saveMakersRequestDto.getServiceForm(), saveMakersRequestDto.getIsParentCompany(), saveMakersRequestDto.getParentCompanyId(),
                        address, saveMakersRequestDto.getCompanyRegistrationNumber(), saveMakersRequestDto.getContractStartDate(),
                        saveMakersRequestDto.getContractEndDate(), saveMakersRequestDto.getIsNutritionInformation(), saveMakersRequestDto.getOpenTime(),
                        saveMakersRequestDto.getCloseTime(), saveMakersRequestDto.getBank(), saveMakersRequestDto.getDepositHolder(), saveMakersRequestDto.getAccountNumber());
            }else {
                Makers makers = makersMapper.toEntity(saveMakersRequestDto, address);

                makersRepository.save(makers);
                /*
                makersRepository.savePoint(makers.getCEO(), makers.getCEOPhone(), makers.getAccountNumber(), makers.getAddress().getAddress1(), makers.getAddress().getAddress2(),
                        makers.getAddress().getLocation(), makers.getAddress().getZipCode(), makers.getBank(), makers.getCode(), makers.getCompanyName(), makers.getCompanyRegistrationNumber(),
                        makers.getContractEndDate(), makers.getContractStartDate(), makers.getDepositHolder(), makers.getIsNutritionInformation(), makers.getIsParentCompany(),
                        makers.getManagerName(), makers.getManagerPhone(), makers.getName(), makers.getOpenTime(), makers.getCloseTime(), makers.getParentCompanyId(), makers.getServiceForm().getCode(), makers.getServiceType().getCode());
                */

                //capacity 생성 및 저장
                for (int i = 0; i < saveMakersRequestDto.getDiningTypes().size(); i++) {
                    MakersCapacity makersCapacity = makersCapacityMapper.toEntity(makers, saveMakersRequestDto.getDiningTypes().get(i));
                    makersCapacityRepository.save(makersCapacity);
                }

//                if (result != 1) {
//                    throw new ApiException(ExceptionEnum.MAKERS_SAVE_FAILED);
//                }
            }
        }
    }

    @Override
    public void locationTest(LocationTestDto locationTestDto) throws ParseException {

        WKTReader wktReader = new WKTReader();
        Geometry location = wktReader.read(locationTestDto.getLocation());

        System.out.println(location + " location");

        qMakersRepository.updateLocation(location, locationTestDto);
    }
}