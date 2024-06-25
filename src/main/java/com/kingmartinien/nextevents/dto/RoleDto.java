package com.kingmartinien.nextevents.dto;

import com.kingmartinien.nextevents.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    private Long id;

    private RoleEnum label;

}
