package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.client.dto.SpotResponseDto;
import co.dalicious.domain.client.entity.EatInSpot;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.client.repository.QSpotRepository;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.enums.ServiceForm;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DiningTypesUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.client.SaveSpotList;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.SpotMapper;
import co.kurrant.app.admin_api.service.SpotService;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SpotServiceImpl implements SpotService {

    private final SpotRepository spotRepository;
    private final QSpotRepository qSpotRepository;
    private final SpotMapper spotMapper;
    private final QGroupRepository qGroupRepository;
    private final GroupMapper groupMapper;
    private final GroupRepository groupRepository;
    private final MakersRepository makersRepository;

    @Override
    public List<SpotResponseDto> getAllSpotList(Integer status) {

        List<Spot> spotList = qSpotRepository.findAllByStatus(status);

        List<SpotResponseDto> resultList = new ArrayList<>();
        for (Spot spot : spotList) {
            SpotResponseDto spotResponseDto = spotMapper.toDto(spot);
            resultList.add(spotResponseDto);
        }

        return resultList;
    }

    @Override
    @Transactional
    public void saveSpotList(SaveSpotList saveSpotList) throws ParseException {
        List<SpotResponseDto> spotResponseDtos = saveSpotList.getSaveSpotList();
        List<BigInteger> spotIds = spotResponseDtos.stream()
                .map(SpotResponseDto::getSpotId)
                .toList();
        Set<BigInteger> groupIds = spotResponseDtos.stream()
                .map(SpotResponseDto::getGroupId)
                .collect(Collectors.toSet());
        Set<BigInteger> makersIds = spotResponseDtos.stream()
                .map(SpotResponseDto::getMakersId)
                .collect(Collectors.toSet());
        List<Group> groups = qGroupRepository.findAllByIds(groupIds);
        List<Makers> makersList = makersRepository.findAllById(makersIds);

        // FIXME 스팟 수정
        List<Spot> updateSpots = qSpotRepository.findAllByIds(spotIds);
        List<BigInteger> updateSpotIds = updateSpots.stream()
                .map(Spot::getId)
                .toList();
        Map<Spot, SpotResponseDto> spotMap = new HashMap<>();
        for (Spot updateSpot : updateSpots) {
            spotResponseDtos.stream()
                    .filter(v -> v.getSpotId().equals(updateSpot.getId()))
                    .findAny().ifPresent(spotResponseDto -> spotMap.put(updateSpot, spotResponseDto));
        }
        // TODO: 그룹이 가지고 있지 않은 스팟이면 생성금지
        for (Spot spot : spotMap.keySet()) {
            List<DiningType> spotDiningTypes = DiningTypesUtils.stringToDiningTypes(spotMap.get(spot).getDiningType());
            spotDiningTypes.retainAll(spot.getGroup().getDiningTypes());
            spot.updateDiningTypes(spotDiningTypes);
            spot.updateName(spotMap.get(spot).getSpotName());
            spot.updateAddress2(spotMap.get(spot).getAddress2());
        }

        // FIXME 스팟 생성
        List<SpotResponseDto> createSpots = spotResponseDtos.stream()
                .filter(v -> !updateSpotIds.contains(v.getSpotId()))
                .toList();
        for (SpotResponseDto createSpot : createSpots) {
            Spot spot = null;
            if (createSpot.getMakersId() == null) {
                spot = spotMapper.toEntity(createSpot, Group.getGroup(groups, createSpot.getGroupId()), DiningTypesUtils.stringToDiningTypes(createSpot.getDiningType()));
                spotRepository.save(spot);
            } else {
                Makers makers = makersList.stream()
                        .filter(maker -> maker.getId().equals(createSpot.getMakersId()))
                        .findAny()
                        .orElse(null);
                if(makers != null && ServiceForm.getContainEatIn().contains(makers.getServiceForm())) {
                    spot = spotMapper.toEatInSpot(createSpot, Group.getGroup(groups, createSpot.getGroupId()), DiningTypesUtils.stringToDiningTypes(createSpot.getDiningType()), createSpot.getMakersId());
                }
            }
            if (Group.getGroup(groups, createSpot.getGroupId()) == null) {
                throw new IllegalIdentifierException("(상세스팟 아이디:" + createSpot.getSpotId().toString() + ") 등록되어있지 않은 그룹입니다.");
            }
            if (spot != null) spotRepository.save(spot);
        }

    }

    @Override
    public void deleteSpot(List<BigInteger> spotIdList) {
        //요청받은 spot을 비활성한다.
        qSpotRepository.deleteSpots(spotIdList);
    }

    @Override
    public List<GroupDto.Group> getGroupList() {
        List<Group> groups = groupRepository.findAll();
        return groupMapper.groupsToDtos(groups);
    }

    private CreateAddressRequestDto makeCreateAddressRequestDto(String zipCode, String address1, String address2) {
        CreateAddressRequestDto createAddressRequestDto = new CreateAddressRequestDto();
        createAddressRequestDto.setAddress1(address1);
        createAddressRequestDto.setAddress2(address2);
        createAddressRequestDto.setZipCode(zipCode);
        return createAddressRequestDto;

    }
}