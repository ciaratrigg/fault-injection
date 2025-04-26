package com.trigg.fault_injection.Database;

import com.trigg.fault_injection.Model.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
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

        String selectUser = "SELEC u_id from user_account WHERE username = ?";
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
}
