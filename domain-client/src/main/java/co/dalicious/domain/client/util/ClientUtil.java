package co.dalicious.domain.client.util;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.client.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClientUtil {
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void hasCorporation(Corporation corporation, String email) {
        Optional<Employee> employee = employeeRepository.findByCorporationAndEmail(corporation, email);
        if(employee.isPresent()) {
            Corporation userCorporation = employee.get().getCorporation();

        }
    }
}
