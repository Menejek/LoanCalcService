package org.panama.loancalculatorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoanCalculatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanCalculatorServiceApplication.class, args);
    }

}
