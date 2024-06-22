package com.kingmartinien.nextevents.mapper;

import com.kingmartinien.nextevents.dto.CreateUserDto;
import com.kingmartinien.nextevents.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toEntity(CreateUserDto createUserDto);

}
