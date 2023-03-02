package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.mapper.MakersCapacityMapper;
import co.dalicious.domain.food.repository.MakersCapacityRepository;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.food.repository.QMakersCapacityRepository;

import co.dalicious.domain.food.repository.QMakersRepository;
import co.kurrant.app.admin_api.dto.makers.SaveMakersRequestDto;
import co.kurrant.app.admin_api.dto.makers.SaveMakersRequestDtoList;
import co.kurrant.app.admin_api.mapper.MakersMapper;
import co.kurrant.app.admin_api.service.MakersService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public void saveMakers(SaveMakersRequestDtoList saveMakersRequestDtoList) {

        for (SaveMakersRequestDto saveMakersRequestDto : saveMakersRequestDtoList.getSaveMakersRequestDto()) {

            //Address 생성
            Address address = makeAddress(saveMakersRequestDto);

            Optional<Makers> optionalMakers = makersRepository.findById(saveMakersRequestDto.getId());
            //이미 존재하면 수정
            if (optionalMakers.isPresent()){
                qMakersRepository.updateMakers(saveMakersRequestDto.getId(),saveMakersRequestDto.getCode(),saveMakersRequestDto.getName(),
                        saveMakersRequestDto.getCompanyName(), saveMakersRequestDto.getCeo(), saveMakersRequestDto.getCeoPhone(),
                        saveMakersRequestDto.getManagerName(), saveMakersRequestDto.getManagerPhone(), saveMakersRequestDto.getDiningTypes(),
                        saveMakersRequestDto.getDailyCapacity(), saveMakersRequestDto.getServiceType(), saveMakersRequestDto.getServiceForm(),
                        saveMakersRequestDto.getIsParentCompany(), saveMakersRequestDto.getParentCompanyId(),address, saveMakersRequestDto.getCompanyRegistrationNumber(),
                        saveMakersRequestDto.getContractStartDate(),saveMakersRequestDto.getContractEndDate(), saveMakersRequestDto.getIsNutritionInformation(),
                        saveMakersRequestDto.getOpenTime(), saveMakersRequestDto.getCloseTime(), saveMakersRequestDto.getBank(),
                        saveMakersRequestDto.getDepositHolder(), saveMakersRequestDto.getAccountNumber());


            }else {
                Makers makers = makersMapper.toEntity(saveMakersRequestDto, address);

                Makers save = makersRepository.save(makers);

                //capacity 생성 및 저장
                for (int i = 0; i < saveMakersRequestDto.getDiningTypes().size(); i++) {
                    MakersCapacity makersCapacity = makersCapacityMapper.toEntity(makers, saveMakersRequestDto.getDiningTypes().get(i));
                    makersCapacityRepository.save(makersCapacity);
                }

                if (save == null) {
                    throw new ApiException(ExceptionEnum.MAKERS_SAVE_FAILED);
                }
            }
        }
    }

    private Address makeAddress(SaveMakersRequestDto saveMakersRequestDto) {
        CreateAddressRequestDto createAddressRequestDto = new CreateAddressRequestDto();
        createAddressRequestDto.setAddress1(saveMakersRequestDto.getAddress1());
        createAddressRequestDto.setAddress2(saveMakersRequestDto.getAddress2());
        createAddressRequestDto.setZipCode(saveMakersRequestDto.getZipCode());
        String[] strings = saveMakersRequestDto.getLocation().split(",");
        createAddressRequestDto.setLatitude(strings[0]);
        createAddressRequestDto.setLongitude(strings[1]);

        return Address.builder()
                .createAddressRequestDto(createAddressRequestDto)
                .build();
    }
}
