package aston.repository;

import aston.exception.NotFoundException;
import aston.model.User;

import java.util.List;

public interface FriendRepository   {
    void save(Long userId, Long friendId);
    List<User> findCommonFriendsForTwoUsers(Long userId, Long otherUserId) throws NotFoundException;
    boolean deleteFriendsByUId(Long id);
    boolean exitsFriendsById(Long id);
}
