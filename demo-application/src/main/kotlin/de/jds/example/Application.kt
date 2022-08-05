package de.jds.example

import de.sync.core.persistence.PersistenceCallbackRegistry
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.sync.diffsync.config.EnableDifferentialSynchronization

@EnableDifferentialSynchronization
@SpringBootApplication
class Application {
    fun main(args: Array<String>) {
        val run = SpringApplication.run(Application::class.java, *args)
        val persistenceCallbackRegistry = run.getBean(
            "persistenceCallbackRegistry"
        ) as PersistenceCallbackRegistry
        println(persistenceCallbackRegistry.findPersistenceCallback("strings"))
    }
}