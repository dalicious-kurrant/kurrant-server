package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.QMakersRepository;
import co.kurrant.app.makers_api.mapper.MakersMapper;
import co.kurrant.app.makers_api.service.MakersInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MakersInfoServiceImpl implements MakersInfoService {

    private final MakersMapper makersMapper;
    private final QMakersRepository qMakersRepository;

    @Override
    public Object getMakersInfo(String code) {

        Makers makers = qMakersRepository.findOneByCode(code);

        makersMapper.toDto(makers);



        return null;
    }
}
