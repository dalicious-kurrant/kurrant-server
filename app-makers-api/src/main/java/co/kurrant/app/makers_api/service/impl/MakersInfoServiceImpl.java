package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.dto.OriginDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.entity.enums.Origin;
import co.dalicious.domain.food.repository.OriginRepository;
import co.dalicious.domain.food.repository.QMakersCapacityRepository;
import co.dalicious.domain.food.repository.QMakersRepository;
import co.kurrant.app.makers_api.mapper.MakersMapper;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.MakersInfoService;
import co.kurrant.app.makers_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MakersInfoServiceImpl implements MakersInfoService {

    private final UserUtil userUtil;
    private final MakersMapper makersMapper;
    private final QMakersRepository qMakersRepository;
    private final QMakersCapacityRepository qMakersCapacityRepository;
    private final OriginRepository originRepository;

    @Override
    public Object getMakersInfo(SecurityUser securityUser) {

        Makers makers = userUtil.getMakers(securityUser);

        List<MakersInfoResponseDto> makersInfoResponseDtoList = new ArrayList<>();

        //dailyCapacity 구하기
        List<MakersCapacity> makersCapacity = qMakersCapacityRepository.findByMakersId(makers.getId());
        Integer dailyCapacity = 0;
        List<String> diningTypes = new ArrayList<>();
        for (MakersCapacity capacity : makersCapacity){
            dailyCapacity += capacity.getCapacity();
            diningTypes.add(capacity.getDiningType().getDiningType());
        }

        MakersInfoResponseDto makersInfo = makersMapper.toDto(makers, dailyCapacity, diningTypes);

        makersInfoResponseDtoList.add(makersInfo);

        return makersInfoResponseDtoList;
    }

    @Override
    @Transactional
    public List<OriginDto.WithId> getMakersOrigins(SecurityUser securityUser) {
        Makers makers = userUtil.getMakers(securityUser);
        return makersMapper.originToDtos(makers.getOrigins());
    }

    @Override
    @Transactional
    public void postMakersOrigins(SecurityUser securityUser, List<OriginDto> originDtos) {
        Makers makers = userUtil.getMakers(securityUser);
        for (OriginDto originDto : originDtos) {
            originRepository.save(makersMapper.dtoToOrigin(originDto, makers));
        }
    }

    @Override
    @Transactional
    public void updateMakersOrigin(SecurityUser securityUser, OriginDto.WithId originDto) {
        Origin origin = originRepository.findById(originDto.getId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        origin.updateOrigin(originDto);
    }

}
