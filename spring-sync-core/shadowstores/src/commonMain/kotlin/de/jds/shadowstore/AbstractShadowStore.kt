package de.jds.shadowstore

/**
 * Shadow store constructor
 *
 * @param nodeId the unique id of the node that this shadow store is being created for.
 */
abstract class AbstractShadowStore(private val nodeId: String) : ShadowStore {
    /**
     * Produces a node-specific key by prefixing the key with the remote node ID.
     *
     * @param key the resource key
     * @return a node-specific key
     */
    protected fun getNodeSpecificKey(key: String): String {
        return "$nodeId:$key"
    }
}
