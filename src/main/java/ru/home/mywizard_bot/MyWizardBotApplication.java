package ru.home.mywizard_bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;

@SpringBootApplication
public class MyWizardBotApplication {

    public static void main(String[] args) {

        SpringApplication.run(MyWizardBotApplication.class, args);
    }

}
