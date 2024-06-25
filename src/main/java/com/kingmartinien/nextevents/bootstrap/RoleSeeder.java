package com.kingmartinien.nextevents.bootstrap;

import com.kingmartinien.nextevents.entity.Role;
import com.kingmartinien.nextevents.enums.RoleEnum;
import com.kingmartinien.nextevents.repository.RoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        this.loadRoles();
    }

    private void loadRoles() {
        RoleEnum[] roleEnums = RoleEnum.values();
        Arrays.stream(roleEnums).forEach(roleEnum -> {
            Optional<Role> optionalRole = this.roleRepository.findByLabel(roleEnum);
            if (optionalRole.isEmpty()) {
                Role role = Role.builder().label(roleEnum).build();
                this.roleRepository.save(role);
            }
        });
    }

}
