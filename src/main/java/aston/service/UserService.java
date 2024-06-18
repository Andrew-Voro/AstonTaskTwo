package aston.service;

import aston.exception.NotFoundException;
import aston.servlets.dto.UserIncomingDto;
import aston.servlets.dto.UserOutGoingDto;
import aston.servlets.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserOutGoingDto save(UserIncomingDto userDto);

    UserOutGoingDto update(UserUpdateDto userDto) throws NotFoundException;

    UserOutGoingDto findById(Long userId) throws NotFoundException;

    List<UserOutGoingDto> findAll();

    void delete(Long userId) throws NotFoundException;

    UserOutGoingDto addFriend(Long userId, Long friendId);

    List<UserOutGoingDto> findCommonFriendsForTwoUsers(Long userId,Long otherUserId) throws NotFoundException;
}
