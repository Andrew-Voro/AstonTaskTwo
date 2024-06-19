package aston.repository.impl;

import aston.db.ConnectionManager;
import aston.db.ConnectionManagerImpl;
import aston.exception.RepositoryException;
import aston.model.User;
import aston.repository.FriendRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendRepositoryImpl implements FriendRepository {

    private static final String SAVE_SQL = "INSERT INTO friends (U_ID, FRIEND_ID) VALUES (?, ?);";
    private static final String GET_ALL_COMMON_FRIENDS_SQL = "SELECT us.USER_ID, us.EMAIL, us.USER_NAME," +
            " us.LOGIN,us.BIRTHDAY FROM users AS u LEFT JOIN friends AS f ON u.USER_ID = f.U_ID " +
            "LEFT JOIN users as us ON f.FRIEND_ID = us.USER_ID WHERE u.USER_ID =? AND " +
            "f.FRIEND_ID IN (SELECT f.FRIEND_ID FROM users AS u LEFT JOIN friends AS f ON u.USER_ID = f.U_ID" +
            "  WHERE u.USER_ID =?)";
    private static final String DELETE_FRIEND_BY_U_ID_SQL = "DELETE FROM FRIENDS WHERE U_ID = ?;";
    private static final String EXIST_FRIENDS_BY_ID_SQL = "SELECT exists (SELECT 1 FROM FRIENDS WHERE U_ID = ? LIMIT 1);";


    private static FriendRepository instance;
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();


    public static synchronized FriendRepository getInstance() {
        if (instance == null) {
            instance = new FriendRepositoryImpl();
        }
        return instance;
    }


    @Override
    public void save(Long userId, Long friendId) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL)) {

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, friendId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

    }

    @Override
    public List<User> findCommonFriendsForTwoUsers(Long userId, Long otherUserId) {
        List<User> commonFriends = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_COMMON_FRIENDS_SQL)) {

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, otherUserId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = createUser(resultSet);
                commonFriends.add(user);
            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return commonFriends;
    }

    public User createUser(ResultSet resultSet) throws SQLException {
        Long userId = resultSet.getLong("user_id");
        User user = new User(
                resultSet.getString("LOGIN"),
                resultSet.getString("USER_NAME"),
                resultSet.getString("EMAIL"),
                resultSet.getDate("BIRTHDAY").toLocalDate(),
                userId,
                null);
        return user;
    }

    @Override
    public boolean exitsFriendsById(Long id) {
        boolean isExists = false;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXIST_FRIENDS_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isExists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return isExists;
    }

    @Override
    public boolean deleteFriendsByUId(Long id) {
        exitsFriendsById(id);
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FRIEND_BY_U_ID_SQL);) {
            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

}
