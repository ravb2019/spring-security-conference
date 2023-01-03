package com.pluralsight.conference.service;

import com.pluralsight.conference.model.Password;
import com.pluralsight.conference.model.ResetToken;
import com.pluralsight.conference.repository.PasswordRepository;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final PasswordRepository passwordRepository;

    public PasswordServiceImpl(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }

    public void createResetToken(Password password, String token) {
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(password.getEmail());
        resetToken.setUsername(password.getUsername());

        passwordRepository.saveToken(resetToken);
    }

    @Override
    public boolean confirmResetToken(ResetToken token) {
        return false;
    }

    @Override
    public void update(Password password, String username) {
        passwordRepository.update(password, username);
    }
}
