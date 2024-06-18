package aston.repository;

import aston.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface UserRepository  extends Repository<User, Long>{
    User createUser(ResultSet resultSet) throws SQLException;
}
