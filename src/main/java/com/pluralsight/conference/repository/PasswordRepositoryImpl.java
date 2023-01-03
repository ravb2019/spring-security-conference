package com.pluralsight.conference.repository;

import com.pluralsight.conference.model.Password;
import com.pluralsight.conference.model.ResetToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PasswordRepositoryImpl implements PasswordRepository{

    private  DataSource dataSource;

    public PasswordRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveToken(ResetToken resetToken) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("INSERT into reset_tokens (email, username, token, expiry_date) values " +
                "(?,?,?,?)", resetToken.getEmail(),
                resetToken.getUsername(),
                resetToken.getToken(),
                resetToken.calculateExpiryDate(ResetToken.EXPIRATION));
    }

    @Override
    public ResetToken findByToken(String token) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForObject("select email, username, token, expiry_date from reset_tokens " +
                        "where token = ?",
                (resultSet, i) -> {
                    ResetToken resetToken = new ResetToken();
                    resetToken.setEmail(resultSet.getString("email"));
                    resetToken.setUsername(resultSet.getString("username"));
                    resetToken.setToken(resultSet.getString("token"));
                    resetToken.setExpiryDate(resultSet.getTimestamp("expiry_date"));
                    return resetToken;
                }, token);
    }

    @Override
    public void update(Password password, String username) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.update("UPDATE users set password = ? where username =?",
                password.getPassword(), username);
    }
}
