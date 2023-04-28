package co.kurrant.batch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service
@Slf4j
@RequiredArgsConstructor
public class BatchPaycheckService {
    private final EntityManager entityManager;

}
