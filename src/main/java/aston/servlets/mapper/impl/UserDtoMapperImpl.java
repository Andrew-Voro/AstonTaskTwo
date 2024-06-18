package aston.servlets.mapper.impl;

import aston.model.User;
import aston.servlets.dto.UserIncomingDto;
import aston.servlets.dto.UserOutGoingDto;
import aston.servlets.dto.UserUpdateDto;
import aston.servlets.mapper.UserDtoMapper;

import java.time.LocalDate;
import java.util.List;

public class UserDtoMapperImpl implements UserDtoMapper {


    private static UserDtoMapper instance;

    private UserDtoMapperImpl() {
    }

    public static synchronized UserDtoMapper getInstance() {
        if (instance == null) {
            instance = new UserDtoMapperImpl();
        }
        return instance;
    }

    @Override
    public User map(UserIncomingDto userDto) {
        return new User(
                userDto.getLogin(),
                userDto.getName(),
                userDto.getEmail(),
                LocalDate.parse(userDto.getBirthday()),
                null,
                null
        );
    }

    @Override
    public User map(UserUpdateDto userDto) {
        return new User(
                userDto.getLogin(),
                userDto.getName(),
                userDto.getEmail(),
                LocalDate.parse(userDto.getBirthday()),
                userDto.getId(),
                null
        );
    }

    @Override
    public UserOutGoingDto map(User user) {
        return new UserOutGoingDto(
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday().toString(),
                user.getId(),
                user.getFriends()
        );
    }

    @Override
    public List<UserOutGoingDto> map(List<User> user) {
        return user.stream().map(this::map).toList();
    }
}
