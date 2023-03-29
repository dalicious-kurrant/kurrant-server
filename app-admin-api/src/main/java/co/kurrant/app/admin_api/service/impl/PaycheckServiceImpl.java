package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.kurrant.app.admin_api.service.PaycheckService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PaycheckServiceImpl implements PaycheckService {
    private final MakersRepository makersRepository;
    @Override
    public void postMakersPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.MakersRequest paycheckDto) {
        Makers makers = makersRepository.findById(paycheckDto.getMakersId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));

    }
}
