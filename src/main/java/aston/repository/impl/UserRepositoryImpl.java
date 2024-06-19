package aston.repository.impl;

import aston.db.ConnectionManager;
import aston.db.ConnectionManagerImpl;
import aston.exception.RepositoryException;
import aston.model.User;
import aston.repository.UserRepository;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class UserRepositoryImpl implements UserRepository {
    private static final String SAVE_SQL = "INSERT INTO users (EMAIL, USER_NAME, LOGIN,BIRTHDAY) VALUES (?, ? ,?, ?);";

    private static final String UPDATE_SQL = "UPDATE users SET EMAIL = ?, USER_NAME = ?, LOGIN =?, BIRTHDAY =? WHERE user_id = ?;";

    private static final String DELETE_SQL = "DELETE FROM  users  WHERE USER_ID = ?;";

    private static final String FIND_BY_ID_SQL = "SELECT u.USER_ID, u.EMAIL, u.USER_NAME, u.LOGIN,u.BIRTHDAY,f.FRIEND_ID FROM users AS u LEFT JOIN friends AS f ON u.USER_ID = f.U_ID  WHERE USER_ID = ?;";

    private static final String FIND_ALL_SQL = "SELECT USER_ID, EMAIL,  USER_NAME, LOGIN, BIRTHDAY,f.FRIEND_ID FROM users AS u LEFT JOIN friends AS f ON u.USER_ID = f.U_ID;";

    private static final String EXIST_BY_ID_SQL = "SELECT exists (SELECT 1 FROM users WHERE user_id = ? LIMIT 1);";

    private static final String DELETE_FRIEND_BY_U_ID_SQL = "DELETE FROM FRIENDS WHERE U_ID = ?;";

    private static final String DELETE_FRIEND_BY_FRIEND_ID_SQL = "DELETE FROM FRIENDS WHERE FRIEND_ID = ?;";
    private static UserRepository instance;
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();


    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepositoryImpl();
        }
        return instance;
    }


    @Override
    public User save(User user) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getLogin());
            preparedStatement.setDate(4, user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : Date.valueOf(LocalDate.now()));
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                user = new User(
                        user.getLogin(),
                        user.getEmail(),
                        user.getName(),
                        user.getBirthday(),
                        resultSet.getLong("user_id"),
                        null
                );
            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = createUser(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public boolean exitsById(Long id) {
        boolean isExists = false;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXIST_BY_ID_SQL)) {

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
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userList.add(createUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return userList;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FRIEND_BY_U_ID_SQL);) {
            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FRIEND_BY_FRIEND_ID_SQL);) {
            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);) {
            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }


    @Override
    public void update(User user) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getLogin());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            preparedStatement.setLong(5, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    public User createUser(ResultSet resultSet) throws SQLException {
        Long userId = resultSet.getLong("user_id");
        Set<Long> friends = new HashSet<>();
        User user = new User(
                resultSet.getString("LOGIN"),
                resultSet.getString("USER_NAME"),
                resultSet.getString("EMAIL"),
                resultSet.getDate("BIRTHDAY").toLocalDate(),
                userId,
                null);
        if (resultSet.getArray("FRIEND_ID") != null) {
            do {
                friends.add(Long.valueOf(resultSet.getArray("FRIEND_ID").toString()));
            } while (!resultSet.next());
            user.setFriends(friends);
        }

        return user;
    }

}
