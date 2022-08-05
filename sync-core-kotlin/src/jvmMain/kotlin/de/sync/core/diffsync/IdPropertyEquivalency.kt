package de.sync.core.diffsync

/**
 * Determines if two objects are equivalent by comparing their "id" properties.
 *
 * @author Craig Walls
 */
actual class IdPropertyEquivalency : Equivalency {
    override fun isEquivalent(o1: Any?, o2: Any?): Boolean {
        return try {
            val idField1 = o1?.javaClass?.getDeclaredField("id")
            idField1?.isAccessible = true
            val id1 = idField1?.get(o1)
            val idField2 = o2?.javaClass?.getDeclaredField("id")
            idField2?.isAccessible = true
            val id2 = idField2?.get(o2)
            id1 == id2
        } catch (e: NoSuchFieldException) {
            false
        } catch (e: IllegalAccessException) {
            false
        }
    }
}