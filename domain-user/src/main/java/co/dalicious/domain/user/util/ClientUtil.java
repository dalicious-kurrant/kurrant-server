package co.dalicious.domain.user.util;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.repository.EmployeeRepository;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.SpotStatus;
import co.dalicious.domain.user.repository.UserGroupRepository;
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
    private final UserGroupRepository userGroupRepository;

    // 그룹(기업)에 등록되어 있는 유저인지 확인 후 등록
    public void isRegisteredUser(User user) {
        // 해당 이메일로 등록된 사원이 있는지 확인
        String email = user.getEmail();
        List<Employee> employees = employeeRepository.findAllByEmail(email);
        // 해당 이메일로 등록된 사원이 존재한다면 유저 정보에 기업 저장 (복수의 스팟이 가능)
        if (!employees.isEmpty()) {
            for (Employee employee : employees) {
                UserGroup userCorporation = UserGroup.builder()
                        .user(user)
                        .group(employee.getCorporation())
                        .build();
                userGroupRepository.save(userCorporation);
            }
        }
    }

    public SpotStatus getSpotStatus(User user) {
        // 유저가 Default로 설정한 스팟을 가져온다.
        List<UserSpot> userSpots = user.getUserSpots();
        if (userSpots == null || userSpots.isEmpty()) {
            // 유저가 속해있는 그룹이 있는지 조회한다.
            List<UserGroup> userGroups = userGroupRepository.findAllByUserAndClientStatus(user, ClientStatus.BELONG);
            if (userGroups.isEmpty()) {
                // 그룹과 스팟 모두 존재하지 않을 경우
                return SpotStatus.NO_SPOT_AND_CLIENT;
            }
            // 그룹은 존재하지만 스팟은 존재하지 않을 경우
            return SpotStatus.NO_SPOT_BUT_HAS_CLIENT;
        }

        // 가져온 스팟을 통해 그룹을 조회한다.
        Optional<UserSpot> userSpot = userSpots.stream()
                .filter(UserSpot::getIsDefault)
                .findAny();
        if(userSpot.isPresent()) {
            Spot spot = userSpot.get().getSpot();
            Group group = spot.getGroup();
            // 유저가 그 그룹에 속해있는지 조회한다.
            Optional<UserGroup> userGroup = userGroupRepository.findOneByUserAndGroupAndClientStatus(user, group, ClientStatus.BELONG);
            if (userGroup.isEmpty()) {
                throw new ApiException(ExceptionEnum.SPOT_DATA_INTEGRITY_ERROR);
            }
            // 그룹과 스팟이 모두 존재할 경우
            return SpotStatus.HAS_SPOT_AND_CLIENT;
        } else {
            return SpotStatus.NO_SPOT_BUT_HAS_CLIENT;
        }

    }
}
