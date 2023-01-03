package com.pluralsight.conference.repository;

import com.pluralsight.conference.model.Account;
import com.pluralsight.conference.model.ConferenceUserDetails;
import com.pluralsight.conference.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    @Autowired
    private DataSource dataSource;

    @Override
    public Account create(Account account) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.update(
                "INSERT into accounts " +
                        "(username, password, email, firstname, lastname) " +
                        "values (?,?,?,?,?)",
                account.getUsername(),
                account.getPassword(),
                account.getEmail(),
                account.getFirstName(),
                account.getLastName());
        return account;
    }

    @Override
    public void saveToken(VerificationToken verificationToken) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.update(
                "INSERT into verification_tokens " +
                        "(username, token, expiry_date) " +
                        "values (?,?,?)",
                verificationToken.getUsername(),
                verificationToken.getToken(),
                verificationToken.calculateExpiryDate(VerificationToken.EXPIRATION));
    }

    @Override
    public VerificationToken findByToken(String token) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.queryForObject("select username, token, expiry_date from " +
                        "verification_tokens where token = ?",
                (resultSet, i) -> {
                    VerificationToken rsToken = new VerificationToken();
                    rsToken.setUsername(resultSet.getString("username"));
                    rsToken.setToken(resultSet.getString("token"));
                    rsToken.setExpiryDate(resultSet.getTimestamp("expiry_date"));
                    return rsToken;
                }, token);
    }

    @Override
    public Account finByUsername(String username) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.queryForObject("select username, firstname, lastname, password from " +
                        "accounts where username = ?",
                (resultSet, i) -> {
                    Account account = new Account();
                    account.setUsername(resultSet.getString("username"));
                    account.setFirstName(resultSet.getString("firstname"));
                    account.setLastName(resultSet.getString("lastname"));
                    account.setPassword(resultSet.getString("password"));
                    return account;
                }, username);
    }

    @Override
    public void createUserDetails(ConferenceUserDetails userDetails) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.update("insert  into users (username, password, enabled)" +
                "values (?,?,?)", userDetails.getUsername(),
                userDetails.getPassword(), 1);
    }

    @Override
    public void createAuthorities(ConferenceUserDetails userDetails) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
            template.update("insert into authorities (username, authority) values (?,?)",
                    userDetails.getUsername(),
                    grantedAuthority.getAuthority());
        }
    }

    @Override
    public void delete(Account account) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.update("DELETE from accounts where username = ?", account.getUsername());
    }

    @Override
    public void deleteToken(String token) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.update("delete from  verification_tokens where token = ?", token);
    }
}
