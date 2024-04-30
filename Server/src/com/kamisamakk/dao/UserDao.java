package Server.src.com.kamisamakk.dao;

import CommonClass.*;

import java.sql.SQLException;
import java.util.ArrayList;

public interface UserDao {
    public User login(String userId, String userPassword) throws SQLException;
    public User register(User user);
    public ArrayList<User> friends(String user_id) throws SQLException;
    public ArrayList<Group> groups(String user_id) throws SQLException;
}
