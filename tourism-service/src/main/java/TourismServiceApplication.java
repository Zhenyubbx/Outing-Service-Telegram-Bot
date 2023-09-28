import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
@ComponentScan("com.skipper.*")
@EnableFeignClients
@ConfigurationPropertiesScan("com.skipper.config")
@EnableCaching
public class TourismServiceApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TourismServiceApplication.class, args);
        logAppStartup(context.getEnvironment());
    }

    private static void logAppStartup(Environment env) {

        String protocol = "https";
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        String hostAddress = env.getProperty("server.host-address");

        log.info("----------------------------------------------------------");
        log.info("Application '{}' is running!",  env.getProperty("spring.application.name"));
        log.info("Version: {}",  env.getProperty("spring.application.version"));
        log.info("Access URLs:");
        log.info("Local: {}://localhost:{}{}", protocol, serverPort, contextPath);
        log.info("External: {}://{}:{}{}", protocol, hostAddress, serverPort, contextPath);
        log.info("----------------------------------------------------------");

    }
}