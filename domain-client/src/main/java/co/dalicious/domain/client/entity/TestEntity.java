package co.dalicious.domain.client.entity;

import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    private BigInteger id;

    private String name;
}
