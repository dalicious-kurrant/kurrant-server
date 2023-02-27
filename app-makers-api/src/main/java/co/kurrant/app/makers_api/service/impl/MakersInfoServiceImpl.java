package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.repository.MakersCapacityRepository;
import co.dalicious.domain.food.repository.QMakersCapacityRepository;
import co.dalicious.domain.food.repository.QMakersRepository;
import co.dalicious.system.enums.DiningType;
import co.kurrant.app.makers_api.mapper.MakersMapper;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.MakersInfoService;
import co.kurrant.app.makers_api.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MakersInfoServiceImpl implements MakersInfoService {

    private final UserUtil userUtil;
    private final MakersMapper makersMapper;
    private final QMakersRepository qMakersRepository;
    private final QMakersCapacityRepository qMakersCapacityRepository;
    @Override
    public Object getMakersInfo(SecurityUser securityUser) {

        Makers makers = userUtil.getMakers(securityUser);

        List<MakersInfoResponseDto> makersInfoResponseDtoList = new ArrayList<>();

        //dailyCapacity 구하기
        List<MakersCapacity> makersCapacity = qMakersCapacityRepository.findByMakersId(makers.getId());
        Integer dailyCapacity = 0;
        List<DiningType> diningTypes = new ArrayList<>();
        for (MakersCapacity capacity : makersCapacity){
            dailyCapacity += capacity.getCapacity();
            diningTypes.add(capacity.getDiningType());
        }

        MakersInfoResponseDto makersInfo = makersMapper.toDto(makers, dailyCapacity, diningTypes);

        makersInfoResponseDtoList.add(makersInfo);

        return makersInfoResponseDtoList;
    }
}
