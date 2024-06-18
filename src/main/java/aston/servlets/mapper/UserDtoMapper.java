package aston.servlets.mapper;

import aston.model.User;
import aston.servlets.dto.UserIncomingDto;
import aston.servlets.dto.UserOutGoingDto;
import aston.servlets.dto.UserUpdateDto;

import java.util.List;

public interface UserDtoMapper {
    User map(UserIncomingDto userIncomingDto);

    User map(UserUpdateDto userIncomingDto);

    UserOutGoingDto map(User user);

    List<UserOutGoingDto> map(List<User> user);

}
