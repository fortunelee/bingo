package com.lp.bingo.checkin;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Configuration
@Component
public class ScheduledEx {



    @Scheduled(cron = "0 21 16 ? * *")
    public void init() throws InterruptedException {

        while(true){
            Thread.sleep(5000);{
                System.out.println("hello world!!!!!");
            }
        }

    }
}
