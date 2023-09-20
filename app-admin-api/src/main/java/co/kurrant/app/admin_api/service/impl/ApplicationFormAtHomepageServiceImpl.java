package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.application_form.dto.corporation.CorporationRequestAtHomepageDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestAtHomepageDto;
import co.dalicious.domain.application_form.mapper.RequestedPartnershipMapper;
import co.dalicious.domain.application_form.repository.RequestedPartnershipRepository;
import co.kurrant.app.admin_api.service.ApplicationFormAtHomepageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationFormAtHomepageServiceImpl implements ApplicationFormAtHomepageService {

    private final RequestedPartnershipRepository requestedPartnershipRepository;
    private final RequestedPartnershipMapper requestedPartnershipMapper;

    @Override
    @Transactional
    public void createCorporationRequestAtHomepage(CorporationRequestAtHomepageDto request) {

        requestedPartnershipRepository.save(requestedPartnershipMapper.toRequestedCorporationEntity(request));
    }

    @Override
    @Transactional
    public void createMakersRequestAtHomepage(MakersRequestAtHomepageDto request) {
        requestedPartnershipRepository.save(requestedPartnershipMapper.toRequestedMakersEntity(request));
    }
}
