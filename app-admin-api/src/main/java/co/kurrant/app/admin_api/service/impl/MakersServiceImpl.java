package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.food.dto.*;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.mapper.MakersCapacityMapper;
import co.dalicious.domain.food.repository.*;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.mapper.MakersMapper;
import co.kurrant.app.admin_api.service.MakersService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class MakersServiceImpl implements MakersService {

    private final MakersRepository makersRepository;
    private final MakersCapacityRepository makersCapacityRepository;
    private final MakersMapper makersMapper;
    private final MakersCapacityMapper makersCapacityMapper;
    private final QMakersRepository qMakersRepository;

    @Override
    @Transactional
    public List<MakersInfoResponseDto> findAllMakersInfo() {
        List<Makers> makersList = makersRepository.findAll();
        return makersList.stream()
                .map(makersMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void saveMakers(SaveMakersRequestDtoList saveMakersRequestDtoList) throws ParseException {

        for (SaveMakersRequestDto saveMakersRequestDto : saveMakersRequestDtoList.getSaveMakersRequestDto()) {
            Address address = new Address(saveMakersRequestDto.getZipCode(), saveMakersRequestDto.getAddress1(), saveMakersRequestDto.getAddress2(), saveMakersRequestDto.getLocation());

            Optional<Makers> optionalMakers = makersRepository.findById(saveMakersRequestDto.getId());
            //이미 존재하면 수정
            if (optionalMakers.isPresent()) {
                Makers makers = optionalMakers.get();
                // MakersCapacity 수정
                updateAllMakersCapacities(makers, saveMakersRequestDto);
                makers.updateMakers(saveMakersRequestDto);
                makers.updateAddress(address);
            } else {
                Makers makers = makersRepository.save(makersMapper.toEntity(saveMakersRequestDto, address));

                //capacity 생성 및 저장
                for (MakersCapacityDto makersCapacityDto : saveMakersRequestDto.getDiningTypes()) {
                    MakersCapacity makersCapacity = makersCapacityMapper.toEntity(makers, makersCapacityDto);
                    makersCapacityRepository.save(makersCapacity);
                }
            }
        }
    }

    @Override
    public void locationTest(LocationTestDto locationTestDto) throws ParseException {
        //Only For LocationTest
        WKTReader wktReader = new WKTReader();
        Geometry location = wktReader.read(locationTestDto.getLocation());

        System.out.println(location + " location");

        qMakersRepository.updateLocation(location, locationTestDto);
    }

    @Override
    @Transactional
    public void updateMakers(SaveMakersRequestDto updateMakersReqDto) throws ParseException {
        //존재하는 Makers인지 확인
        Makers makers = makersRepository.findById(updateMakersReqDto.getId()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));
        //위치값 반영
        Address address = new Address(updateMakersReqDto.getZipCode(), updateMakersReqDto.getAddress1(), updateMakersReqDto.getAddress2(), updateMakersReqDto.getLocation());
        makers.updateMakers(updateMakersReqDto);
        makers.updateAddress(address);
        updateAllMakersCapacities(makers, updateMakersReqDto);
    }

    private void updateMakersCapacities(Makers makers, SaveMakersRequestDto saveMakersRequestDto, DiningType diningType) {
        Optional<MakersCapacityDto> makersCapacityDtoOptional = saveMakersRequestDto.getDiningTypes().stream()
                .filter(v -> v.getDiningType().equals(diningType.getCode()))
                .findAny();

        MakersCapacity makersCapacity = makers.getMakersCapacity(diningType);

        if (makersCapacityDtoOptional.isEmpty() && makersCapacity != null) {
            makersCapacityRepository.delete(makersCapacity);
            return;
        }

        if (makersCapacityDtoOptional.isPresent()) {
            MakersCapacityDto makersCapacityDto = makersCapacityDtoOptional.get();
            Integer capacity = makersCapacityDto.getCapacity();
            LocalTime minTime = DateUtils.stringToLocalTime(makersCapacityDto.getMinTime());
            LocalTime maxTime = DateUtils.stringToLocalTime(makersCapacityDto.getMaxTime());
            DayAndTime lastOrderTime = DayAndTime.stringToDayAndTime(makersCapacityDto.getLastOrderTime());

            if (makersCapacity == null) {
                makersCapacity = MakersCapacity.builder()
                        .makers(makers)
                        .diningType(diningType)
                        .capacity(capacity)
                        .minTime(minTime)
                        .maxTime(maxTime)
                        .lastOrderTime(lastOrderTime)
                        .build();
                makersCapacityRepository.save(makersCapacity);
            } else {
                makersCapacity.updateMakersCapacity(capacity, minTime, maxTime, lastOrderTime);
            }
        }
    }

    private void updateAllMakersCapacities(Makers makers, SaveMakersRequestDto saveMakersRequestDto) {
        // 운영시간 업데이트, 생성 및 삭제
        updateMakersCapacities(makers, saveMakersRequestDto, DiningType.MORNING);
        updateMakersCapacities(makers, saveMakersRequestDto, DiningType.LUNCH);
        updateMakersCapacities(makers, saveMakersRequestDto, DiningType.DINNER);
    }
}
