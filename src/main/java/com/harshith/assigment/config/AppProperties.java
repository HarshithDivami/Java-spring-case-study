package com.harshith.assigment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private final Mail mail = new Mail();
    private final Prediction prediction = new Prediction();
    private final Admin admin = new Admin();

    @Getter @Setter
    public static class Mail {
        private String from = "noreply@familyleague.local";
    }

    @Getter @Setter
    public static class Prediction {
        private int leagueLockHoursBeforeFirstMatch = 4;
        private int matchLockHoursBeforeStart = 1;
    }

    @Getter @Setter
    public static class Admin {
        private String alertEmail = "admin@familyleague.local";
    }
}
