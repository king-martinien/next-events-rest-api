package com.kingmartinien.nextevents.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface TokenService {

    @Scheduled(cron = "0 */30 * * * *")
    void removeAllUselessTokens();

}
