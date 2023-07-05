package co.dalicious.domain.client.entity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
public class TestEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    private BigInteger id;

    private String name;
}
