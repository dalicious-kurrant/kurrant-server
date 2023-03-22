package co.dalicious.domain.user.util;

import co.dalicious.domain.user.entity.Founders;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.FoundersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FoundersUtil {
    private final FoundersRepository foundersRepository;
    private static final Integer FOUNDERS_LIMIT = 5000;

    public Boolean isFounders(User user) {
        Optional<Founders> founders = foundersRepository.findOneByUserAndIsActive(user, true);
        return founders.isPresent();
    }

    public Integer getFoundersNumber(User user) {
        Optional<Founders> founders = foundersRepository.findOneByUserAndIsActive(user, true);
        if(founders.isPresent()) {
            return founders.get().getFoundersNumber();
        }
        else {
            return 0;
        }
    }

    public Membership getFoundersMembership(User user) {
        Optional<Founders> founders = foundersRepository.findOneByUserAndIsActive(user, true);
        return founders.map(Founders::getMembership).orElse(null);
    }

    public Integer getMaxFoundersNumber() {
        return foundersRepository.getMaxFoundersNumber();
    }

    public Integer getLeftFoundersNumber() {
        return FOUNDERS_LIMIT - foundersRepository.getMaxFoundersNumber();
    }

    public Boolean isOverFoundersLimit() {
        return foundersRepository.getMaxFoundersNumber() > FOUNDERS_LIMIT;
    }

    public void saveFounders(Founders founders) {
        foundersRepository.save(founders);
    }

    public void cancelFounders(User user) {
        if(isFounders(user)) {
            Optional<Founders> founders = foundersRepository.findOneByUserAndIsActive(user, true);
            founders.ifPresent(value -> value.updateIsActive(false));
        }
    }
}
