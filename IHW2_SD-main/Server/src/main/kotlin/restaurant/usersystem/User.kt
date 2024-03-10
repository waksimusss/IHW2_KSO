package restaurant.usersystem

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import restaurant.order.OrderSystem

@Serializable
sealed class User {

    abstract fun compareData(log: String, password: String): Boolean

    @Serializable
    var isLoggedNow: Boolean = false

    @Serializable
    var role = TypeOfUser.Visitor

    @Transient
    protected lateinit var orderSystem: OrderSystem

    @JvmName("set role")
    protected fun setRole(newRole: TypeOfUser) {
        role = newRole
    }

    internal fun setOS(order: OrderSystem): User {
        orderSystem = order
        return this
    }
}