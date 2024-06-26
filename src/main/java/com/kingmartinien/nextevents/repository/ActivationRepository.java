package com.kingmartinien.nextevents.repository;

import com.kingmartinien.nextevents.entity.Activation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationRepository extends JpaRepository<Activation, Long> {

    Optional<Activation> findByCode(String code);

}
