package co.dalicious.domain.user.util;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.client.repository.EmployeeRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserCorporation;
import co.dalicious.domain.user.repository.UserCorporationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClientUtil {
    private final EmployeeRepository employeeRepository;
    private final UserCorporationRepository userCorporationRepository;

    // 그룹(기업)에 등록되어 있는 유저인지 확인 후 등록
    public void isRegisteredUser(User user) {
        // 해당 이메일로 등록된 사원이 있는지 확인
        String email = user.getEmail();
        Optional<List<Employee>> employees = employeeRepository.findByEmail(email);
        // 해당 이메일로 등록된 사원이 존재한다면 유저 정보에 기업 저장 (복수의 스팟이 가능)
        if (employees.isPresent()) {
            List<Employee> employeeList = employees.get();
            for(Employee employee : employeeList) {
                UserCorporation userCorporation = UserCorporation.builder()
                        .user(user)
                        .corporation(employee.getCorporation())
                        .build();
                userCorporationRepository.save(userCorporation);
            }
        }
    }
    // 디폴트 스팟을 가지고 있는지 확인
    public static Boolean hasSpot(User user) {
        BigInteger spotId = user.getDefaultSpotId();
        return spotId != null;
    }
}
