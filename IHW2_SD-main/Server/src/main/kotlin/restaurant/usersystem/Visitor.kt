package restaurant.usersystem

import kotlinx.serialization.Serializable
import restaurant.order.ImportanceLevel
import restaurant.order.OrderStatus

@Serializable
class Visitor(
    var id: Int, private var login: String, private var password: String
) : User() {
    init {
        setRole(TypeOfUser.Visitor)
    }

    @Serializable private var counterOrders = 0
    @Serializable private var visitorStatus = UserStatus.Beginner

    override fun compareData(log: String, password: String): Boolean {
        return login == log && this.password == password
    }

    private fun matchStatusWithLevel(): ImportanceLevel {
        return when (visitorStatus) {
            UserStatus.Beginner -> {
                ImportanceLevel.Low
            }

            UserStatus.Medium -> {
                ImportanceLevel.Medium
            }

            UserStatus.Lover -> {
                ImportanceLevel.High
            }
        }
    }

    @JvmName("MakeOrderByInt")
    fun makeOrder(mapOfOrder: MutableMap<Int, Int>): Int {
        if (isLoggedNow) {
            return orderSystem.addOrder(mapOfOrder, matchStatusWithLevel(), id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun addToOrder(orderId: Int, dishId: Int) {
        if (isLoggedNow) {
            orderSystem.addToExistedOrder(orderId, dishId, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun addToOrder(orderId: Int, dishes: MutableMap<Int, Int>) {
        if (isLoggedNow) {
            orderSystem.addToExistedOrder(orderId, dishes, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun cancelOrder(orderId: Int)  {
        if (isLoggedNow) {
            orderSystem.cancelOrder(orderId, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun getOrderStatus(orderId: Int): OrderStatus {
        if (isLoggedNow) {
            return orderSystem.getOrderStatus(orderId, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun payOrder(orderId: Int) : Int {
        if (isLoggedNow) {
            increaseLevel()
            return orderSystem.payOrder(orderId, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    private fun increaseLevel() {
        ++counterOrders
        if (counterOrders in 11..29) {
            visitorStatus = UserStatus.Medium
        } else if (counterOrders > 30) {
            visitorStatus = UserStatus.Lover
        }
    }

    fun leaveFeedbackAboutOrder(orderId: Int, stars: Int, comment: String) {
        if (isLoggedNow) {
            orderSystem.setReviewToOrder(orderId, stars, comment, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }
}