package com.metuncc.mlm.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface MlmScheduledServices {
    @Scheduled(cron = "0 15 * * * *")
    void cancelUnconfirmedReservations();

    @Scheduled(cron = "0 5 0 * * *")
    void updateRoomSlots();

    //Every day, at 23.45
    @Scheduled(cron = "0 45 23 * * ?")
    void dequeueForBooks();

    //Every day, at 23.45
    @Scheduled(cron = "0 45 23 * * ?")
    void increaseDebt();
}
