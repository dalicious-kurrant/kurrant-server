package co.kurrant.app.public_api.controller.client;

import co.kurrant.app.public_api.dto.client.ApartmentSpotApplicationFormDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "6. Client")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/application-form")
@RestController
public class ApplicationFormController {
    @Operation(summary = "아파트 스팟 개설 신청 API", description = "아파트 스팟 개설을 신청을 요청한다.")
    @PostMapping("/apartments")
    public void registerApartmentSpot(@RequestBody ApartmentSpotApplicationFormDto apartmentSpotApplicationFormDto) {

    }
}
