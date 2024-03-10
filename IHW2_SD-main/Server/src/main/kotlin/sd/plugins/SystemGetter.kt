package sd.plugins

import restaurant.System

class SystemGetter {
    companion object {
        private val systemObj = System()

        val system: System
            get() = systemObj
    }
}