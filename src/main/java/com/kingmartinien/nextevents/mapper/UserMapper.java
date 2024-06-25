package com.kingmartinien.nextevents.mapper;

import com.kingmartinien.nextevents.dto.CreateUserDto;
import com.kingmartinien.nextevents.dto.UserDto;
import com.kingmartinien.nextevents.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toEntity(CreateUserDto createUserDto);

    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

}
