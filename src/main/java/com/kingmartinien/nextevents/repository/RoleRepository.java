package com.kingmartinien.nextevents.repository;

import com.kingmartinien.nextevents.entity.Role;
import com.kingmartinien.nextevents.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByLabel(RoleEnum label);

}
