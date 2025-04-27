package com.trigg.fault_injection.Database;

import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Model.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @Override
    public void createAccount(UserAccount user, List<String> roles) {
        String insertAcct = "INSERT INTO user_account (username, password) VALUES (?, ?)";
        jdbcTemplate.update(insertAcct, user.getUsername(), user.getPassword());

        String selectUser = "SELECT u_id from user_account WHERE username = ?";
        Integer uid = jdbcTemplate.queryForObject(selectUser, new Object[]{user.getUsername()}, Integer.class);

        if(uid != null){
            for(String role : roles){
                jdbcTemplate.update("INSERT INTO authority (u_id, role) VALUES (?, ?)", uid, role);
            }
        }
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        String selectUsername = "SELECT * FROM user_account WHERE username = ?";
        List<UserAccount> users = jdbcTemplate.query(selectUsername, new Object[]{username},
                (rs, rowNum) -> {
                    UserAccount user = new UserAccount();
                    user.setId(rs.getInt("u_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    return user;
                }
        );
        return users.stream().findFirst();
    }

    @Override
    public Integer checkExistingUser(String username) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_account WHERE username = ?",
                Integer.class, username);
    }

    @Override
    public void registerUserAccount(String username, String password) {
        jdbcTemplate.update(
                "INSERT INTO user_account (username, password, approved) VALUES (?, ?, false)",
                username, password);

        Integer uid = jdbcTemplate.queryForObject(
                "SELECT u_id FROM user_account WHERE username = ?",
                Integer.class, username);

        jdbcTemplate.update(
                "INSERT INTO authority (u_id, role) VALUES (?, ?)",
                uid, "ROLE_USER");
    }

    @Override
    public UserAccount selectAccount(String username) {
        String selectAccount = "SELECT * FROM user_account WHERE username = ?";
        return jdbcTemplate.queryForObject(selectAccount, new Object[]{username}, new UserAccountMapper());
    }

    @Override
    public String getUserRole(int id) {
        String selectRole = "SELECT role FROM authority WHERE u_id = ?";
        return jdbcTemplate.queryForObject(selectRole, String.class, id);
    }

    @Override
    public void updateUserRole(int id, String newRole) {
        String deleteSql = "DELETE FROM authority WHERE u_id = ?";
        String insertSql = "INSERT INTO authority (u_id, role) VALUES (?, ?)";

        jdbcTemplate.update(deleteSql, id);
        jdbcTemplate.update(insertSql, id, newRole);
    }

    @Override
    public List<String> getPendingUsers() {
        String selectPendingUsers = "SELECT username from user_account WHERE approved = FALSE";
        return jdbcTemplate.query(selectPendingUsers, (rs,rowNum) -> rs.getString("username"));
    }

    @Override
    public void approveUser(int id) {
        String updateApproval = "UPDATE user_account SET approved = true WHERE u_id = ?";
        jdbcTemplate.update(updateApproval, id);
    }

    class UserAccountMapper implements RowMapper<UserAccount> {
        public UserAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserAccount account = new UserAccount(
                    rs.getInt("u_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getBoolean("approved")
            );
            return account;
        }
    }
}
