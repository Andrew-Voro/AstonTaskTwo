package aston.service.impl;

import aston.exception.NotFoundException;
import aston.model.User;
import aston.repository.FriendRepository;
import aston.repository.UserRepository;
import aston.repository.impl.FriendRepositoryImpl;
import aston.repository.impl.UserRepositoryImpl;
import aston.service.UserService;
import aston.servlets.dto.UserIncomingDto;
import aston.servlets.dto.UserOutGoingDto;
import aston.servlets.dto.UserUpdateDto;
import aston.servlets.mapper.UserDtoMapper;
import aston.servlets.mapper.impl.UserDtoMapperImpl;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository = UserRepositoryImpl.getInstance();
    private final FriendRepository friendRepository = FriendRepositoryImpl.getInstance();
    private static final UserDtoMapper userDtoMapper = UserDtoMapperImpl.getInstance();

    private static UserService instance;


    private UserServiceImpl() {
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserServiceImpl();
        }
        return instance;
    }

    private void checkExistUser(Long userId) throws NotFoundException {
        if (!userRepository.exitsById(userId)) {
            throw new NotFoundException("User not found.");
        }
    }

    @Override
    public UserOutGoingDto save(UserIncomingDto userDto) {
        User user = userRepository.save(userDtoMapper.map(userDto));
        return userDtoMapper.map(userRepository.findById(user.getId()).orElse(user));
    }

    @Override
    public UserOutGoingDto findById(Long userId) throws NotFoundException {
        checkExistUser(userId);
        User user = userRepository.findById(userId).orElseThrow();
        return userDtoMapper.map(user);
    }

    @Override
    public List<UserOutGoingDto> findAll() {
        List<User> all = userRepository.findAll();
        return userDtoMapper.map(all);
    }

    @Override
    public List<UserOutGoingDto> findCommonFriendsForTwoUsers(Long userId, Long otherUserId) throws NotFoundException{
        checkExistUser(userId);
        checkExistUser(otherUserId);
        List<User> commonFriends = friendRepository.findCommonFriendsForTwoUsers(userId, otherUserId);
        return userDtoMapper.map(commonFriends);
    }

    @Override
    public void delete(Long userId) throws NotFoundException {
        checkExistUser(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserOutGoingDto update(UserUpdateDto userDto) throws NotFoundException {
        if (userDto == null || userDto.getId() == null) {
            throw new IllegalArgumentException();
        }
        checkExistUser(userDto.getId());
        userRepository.update(userDtoMapper.map(userDto));
        return userDtoMapper.map(userRepository.findById(userDto.getId()).get());
    }

    @Override
    public UserOutGoingDto addFriend(Long userId, Long friendId) {
        userRepository.exitsById(userId);
        userRepository.exitsById(friendId);
        friendRepository.save(userId, friendId);
        return userDtoMapper.map(userRepository.findById(userId).get());
    }


}
