package co.dalicious.domain.user.util;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.repository.EmployeeRepository;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.SpotStatus;
import co.dalicious.domain.user.repository.UserApartmentRepository;
import co.dalicious.domain.user.repository.UserCorporationRepository;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClientUtil {
    private final EmployeeRepository employeeRepository;
    private final UserCorporationRepository userCorporationRepository;
    private final UserApartmentRepository userApartmentRepository;

    // 그룹(기업)에 등록되어 있는 유저인지 확인 후 등록
    public void isRegisteredUser(User user) {
        // 해당 이메일로 등록된 사원이 있는지 확인
        String email = user.getEmail();
        List<Employee> employees = employeeRepository.findAllByEmail(email);
        // 해당 이메일로 등록된 사원이 존재한다면 유저 정보에 기업 저장 (복수의 스팟이 가능)
        if (!employees.isEmpty()) {
            for(Employee employee : employees) {
                UserCorporation userCorporation = UserCorporation.builder()
                        .user(user)
                        .corporation(employee.getCorporation())
                        .build();
                userCorporationRepository.save(userCorporation);
            }
        }
    }

    public SpotStatus getSpotStatus(User user) {
        // 유저가 Default로 설정한 스팟을 가져온다.
        UserSpot userSpot = user.getUserSpot();
        if (userSpot == null) {
            // 유저가 속해있는 그룹이 있는지 조회한다.
            List<UserCorporation> userCorporations = userCorporationRepository.findByUserAndClientStatus(user, ClientStatus.BELONG);
            List<UserApartment> userApartments = userApartmentRepository.findAllByUserAndClientStatus(user, ClientStatus.BELONG);
            if (userCorporations.isEmpty() && userApartments.isEmpty()) {
                // 그룹과 스팟 모두 존재하지 않을 경우
                return SpotStatus.NO_SPOT_AND_CLIENT;
            }
            // 그룹은 존재하지만 스팟은 존재하지 않을 경우
            return SpotStatus.NO_SPOT_BUT_HAS_CLIENT;
        }
        // 가져온 스팟을 통해 그룹을 조회한다.
        switch (userSpot.getClientType()) {
            case APARTMENT -> {
                ApartmentSpot apartmentSpot = userSpot.getApartmentSpot();
                Apartment apartment = apartmentSpot.getApartment();
                // 유저가 그 그룹에 속해있는지 조회한다.
                Optional<UserApartment> userApartment = userApartmentRepository.findOneByUserAndApartmentAndClientStatus(user, apartment, ClientStatus.BELONG);
                if (userApartment.isEmpty()) {
                    throw new ApiException(ExceptionEnum.SPOT_DATA_INTEGRITY_ERROR);
                }
            }
            case CORPORATION -> {
                CorporationSpot corporationSpot = userSpot.getCorporationSpot();
                Corporation corporation = corporationSpot.getCorporation();
                // 유저가 그 그룹에 속해있는지 조회한다.
                Optional<UserCorporation> userCorporation = userCorporationRepository.findOneByUserAndCorporationAndClientStatus(user, corporation, ClientStatus.BELONG);
                if (userCorporation.isEmpty()) {
                    throw new ApiException(ExceptionEnum.SPOT_DATA_INTEGRITY_ERROR);
                }
            }
        }
        // 그룹과 스팟이 모두 존재할 경우
        return SpotStatus.HAS_SPOT_AND_CLIENT;
    }
}
