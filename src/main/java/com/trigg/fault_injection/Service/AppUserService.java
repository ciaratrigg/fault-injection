package com.trigg.fault_injection.Service;

import com.trigg.fault_injection.Database.UserDAO;
import com.trigg.fault_injection.Model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserService implements UserDetailsService {

    private UserDAO userDAO;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public AppUserService(UserDAO userDAO, JdbcTemplate jdbcTemplate){
        this.userDAO = userDAO;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = jdbcTemplate.query(
                "SELECT role FROM authority WHERE u_id = ?",
                new Object[]{user.getId()},
                (rs, rowNum) -> new SimpleGrantedAuthority(rs.getString("role"))
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    public Integer checkExistingUser(String username){
        return userDAO.checkExistingUser(username);
    }

    public void registerUserAccount(String username, String password){
        userDAO.registerUserAccount(username, password);
    }

    public UserAccount retrieveAccount(String username){
        return userDAO.selectAccount(username);
    }

    //get roles
    public List<String> getUserRoles(int id){
        return userDAO.getUserRoles(id);
    }

}
