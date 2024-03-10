package restaurant

import restaurant.order.OrderSystem
import restaurant.usersystem.*

data class StatusOfToken(val id: String, val isExpired: Boolean, val role: String)

class System {
    init {
        Logger.writeToLog("-".repeat(30))
        Logger.writeToLog("System was created.")
    }

    private val orderSystem = OrderSystem()
    private val authSystem = AuthorizationSystem(orderSystem)

    private fun tryAuth(login: String, password: String): User? {
        return authSystem.tryAuth(login, Encryptor.encryptThis(password))
    }

    fun tryAuthVisitor(login: String, password: String): Visitor {
        val possibleUser = tryAuth(login, password)
        if (possibleUser != null && possibleUser.role == TypeOfUser.Visitor) {
            return possibleUser as Visitor
        }
        if (possibleUser == null) {
            throw SecurityException("No account has been registered with this data")
        }
        throw SecurityException("Error in data.")
    }

    fun tryAuthAdmin(login: String, password: String): Admin {
        val possibleUser = tryAuth(login, password)
        if (possibleUser != null && possibleUser.role == TypeOfUser.Admin) {
            return possibleUser as Admin
        }
        if (possibleUser == null) {
            throw SecurityException("No account has been registered with this data")
        }
        throw SecurityException("Error in data.")
    }

    fun registerNewUser(login: String, password: String, role: TypeOfUser): Boolean {
        if (login.isEmpty() || password.isEmpty()) {
            throw SecurityException("Data cannot be null")
        }
        return authSystem.addUserToSystem(login, Encryptor.encryptThis(password), role)
    }

    fun getMenu(): String {
        return orderSystem.menuObj.getString()
    }

    fun exitUser(user: User?) {
        return authSystem.exitFromSystem(user)
    }
}