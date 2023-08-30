package co.kurrant.app.admin_api.service;

import co.dalicious.domain.application_form.dto.corporation.CorporationRequestAtHomepageDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestAtHomepageDto;

public interface ApplicationFormAtHomepageService {
    void createCorporationRequestAtHomepage(CorporationRequestAtHomepageDto request);
    void createMakersRequestAtHomepage(MakersRequestAtHomepageDto request);
}
