package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.application_form.dto.corporation.CorporationRequestAtHomepageDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestAtHomepageDto;
import co.dalicious.domain.application_form.mapper.RequestedCorporationMapper;
import co.dalicious.domain.application_form.mapper.RequestedMakersMapper;
import co.dalicious.domain.application_form.repository.RequestedCorporationRepository;
import co.dalicious.domain.application_form.repository.RequestedMakersRepository;
import co.kurrant.app.admin_api.service.ApplicationFormAtHomepageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationFormAtHomepageServiceImpl implements ApplicationFormAtHomepageService {

    private final RequestedCorporationRepository requestedCorporationRepository;
    private final RequestedCorporationMapper requestedCorporationMapper;
    private final RequestedMakersRepository requestedMakersRepository;
    private final RequestedMakersMapper requestedMakersMapper;

    @Override
    @Transactional
    public void createCorporationRequestAtHomepage(CorporationRequestAtHomepageDto request) {

        requestedCorporationRepository.save(requestedCorporationMapper.toRequestedCorporationEntity(request));
    }

    @Override
    @Transactional
    public void createMakersRequestAtHomepage(MakersRequestAtHomepageDto request) {
        requestedMakersRepository.save(requestedMakersMapper.toRequestedCorporationEntity(request));
    }
}
