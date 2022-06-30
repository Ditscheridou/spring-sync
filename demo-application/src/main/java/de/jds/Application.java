package de.jds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.sync.diffsync.PersistenceCallbackRegistry;
import org.springframework.sync.diffsync.config.EnableDifferentialSynchronization;

@EnableDifferentialSynchronization
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    final ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
    final PersistenceCallbackRegistry persistenceCallbackRegistry = (PersistenceCallbackRegistry) run.getBean(
        "persistenceCallbackRegistry");
    System.out.println(persistenceCallbackRegistry.findPersistenceCallback("strings"));
  }
}
