package co.dalicious.data.redis.event;

import lombok.Getter;

import java.math.BigInteger;
import java.util.Collection;

@Getter
public class ReloadEvent {
    private final Collection<BigInteger> makersIds;

    public ReloadEvent(Collection<BigInteger> makersIds) {
        this.makersIds = makersIds;
    }
}
