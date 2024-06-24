package com.kingmartinien.nextevents.service.impl;

import com.kingmartinien.nextevents.repository.TokenRepository;
import com.kingmartinien.nextevents.service.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenServiceImpl implements TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);
    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 */30 * * * *")
    @Override
    public void removeAllUselessTokens() {
        log.info("DELETING TOKENS AT {}", Instant.now());
        this.tokenRepository.deleteAllByExpiredAndRevoked(true, true);
    }

}
