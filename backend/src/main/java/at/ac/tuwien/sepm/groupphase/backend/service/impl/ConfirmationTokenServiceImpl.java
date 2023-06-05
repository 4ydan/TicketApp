package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ConfirmationToken;
import at.ac.tuwien.sepm.groupphase.backend.repository.ConfirmationTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token) {
        log.trace("save confirmation token {}", token);
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        log.trace("Get token {}", token);
        return confirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        log.trace("set confirmation date for token {}", token);
        return confirmationTokenRepository.updateConfirmedAt(
            token, LocalDateTime.now());
    }

    public void deleteConfirmationTokensByUserId(Long id) {
        log.trace("delete confirmation token with user id {}", id);
        confirmationTokenRepository.deleteAllByUser_Id(id);
    }
}