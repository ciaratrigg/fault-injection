package com.trigg.fault_injection.Database;

import com.trigg.fault_injection.Model.UserAccount;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO {
    void setDataSource(DataSource dataSource);
    void createAccount(UserAccount user, List<String> roles);
    Optional<UserAccount> findByUsername(String username);
}
