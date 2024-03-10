package restaurant.usersystem

import kotlinx.serialization.Serializable
import restaurant.dish.ProductFeatures
import kotlin.time.Duration

@Serializable
class Admin(
    val id: Int,
    private val login: String,
    private val password: String
) : User() {
    init {
        setRole(TypeOfUser.Admin)
    }

    override fun compareData(log: String, password: String): Boolean {
        return login == log && this.password == password
    }

    fun addDishToMenu(name: String, price: Int, timeProduction: Duration, count: Int = 1): Int {
        if (isLoggedNow) {
            return orderSystem.menuObj.addDishToMenu(name, price, timeProduction, count)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun removeDishFromMenu(dishId: Int) {
        if (isLoggedNow) {
            orderSystem.menuObj.removeDishFromMenu(dishId)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun setParamToDish(dishId: Int, params: ProductFeatures, type: Any) {
        if (isLoggedNow) {
            orderSystem.menuObj.setParamToDish(dishId, params, type)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun increaseTheNumberOfDish(dishId: Int, delta: Int) {
        if (isLoggedNow) {
            orderSystem.increaseTheNumber(dishId, delta)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun getStatistic(): String {
        return if (isLoggedNow) {
            orderSystem.getStatistics()
        } else {
            orderSystem.getStatistics()
        }
    }
}