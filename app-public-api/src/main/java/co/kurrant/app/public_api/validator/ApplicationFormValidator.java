package co.kurrant.app.public_api.validator;

import co.dalicious.domain.application_form.entity.ApartmentApplicationForm;
import co.dalicious.domain.application_form.repository.ApartmentApplicationFormRepository;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class ApplicationFormValidator {
    private final ApartmentApplicationFormRepository apartmentApplicationFormRepository;

    public ApartmentApplicationForm isVaildApartmentApplicationForm(BigInteger userId, Long applicationFormId) {
        // 존재하는지 확인
        ApartmentApplicationForm apartmentApplicationForm = apartmentApplicationFormRepository.findById(applicationFormId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.APPLICATION_FORM_NOT_FOUND));
        // 신청서 작성자와 로그인 유저가 일치하는 지 확인
        if(!apartmentApplicationForm.getUserId().equals(userId)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        return apartmentApplicationForm;
    }
}
