package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.repository.MakersCapacityRepository;
import co.dalicious.domain.food.repository.QMakersCapacityRepository;
import co.dalicious.domain.food.repository.QMakersRepository;
import co.kurrant.app.makers_api.mapper.MakersMapper;
import co.kurrant.app.makers_api.service.MakersInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MakersInfoServiceImpl implements MakersInfoService {

    private final MakersMapper makersMapper;
    private final QMakersRepository qMakersRepository;
    private final QMakersCapacityRepository qMakersCapacityRepository;
    @Override
    public Object getMakersInfo(String code) {

        List<MakersInfoResponseDto> makersInfoResponseDtoList = new ArrayList<>();

        Makers makers = qMakersRepository.findOneByCode(code);

        //dailyCapacity 구하기
        List<MakersCapacity> makersCapacity = qMakersCapacityRepository.findByMakersId(makers.getId());
        Integer dailyCapacity = 0;
        for (MakersCapacity capacity : makersCapacity){
            dailyCapacity += capacity.getCapacity();
        }

        MakersInfoResponseDto makersInfo = makersMapper.toDto(makers, dailyCapacity);

        makersInfoResponseDtoList.add(makersInfo);

        return makersInfoResponseDtoList;
    }
}
