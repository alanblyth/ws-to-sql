package com.intellimax.fileingressws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static String FILE_STORE = "tempFileStore";
    public static void main(String[] args) {
        org.apache.log4j.BasicConfigurator.configure();

        SpringApplication.run(Application.class, args);
    }
}
