package de.jds.example

import org.springframework.context.annotation.Configuration
import org.springframework.sync.persistence.PersistenceCallbackRegistry

@Configuration
class DemoSynConfigurer : DiffSyncConfigurer {
    override fun addPersistenceCallbacks(persistenceCallbackRegistry: PersistenceCallbackRegistry) {
        persistenceCallbackRegistry.addPersistenceCallback(DemoPersistenceCallback())
    }
}