package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.food.repository.QMakersCapacityRepository;

import co.kurrant.app.admin_api.mapper.MakersMapper;
import co.kurrant.app.admin_api.service.MakersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MakersServiceImpl implements MakersService {

    private final MakersRepository makersRepository;
    private final QMakersCapacityRepository qMakersCapacityRepository;
    private final MakersMapper makersMapper;

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
}
