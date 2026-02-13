package uk.gov.hmcts.reform.dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class Application {

    public static void main(final String[] args) {
        try {
            SpringApplication.run(Application.class, args);
        } catch (Throwable t) {
            System.exit(1);
        }
    }
}
