package sd.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import restaurant.Encryptor
import restaurant.StatusOfToken
import restaurant.usersystem.Admin
import restaurant.usersystem.User
import restaurant.usersystem.TypeOfUser
import restaurant.usersystem.Visitor
import java.util.*

object TokenSystem {
    private const val SECRET = "FYG3YYEG7QDWJXgjdsg3tfy243Y4Dd"
    private const val ISSUER = "http://0.0.0.0:8080/"
    private val DEPENDENCIES = mutableMapOf<String, User>()

    fun createToken(userId: String, user: User): String {
        when (user.role) {
            TypeOfUser.Visitor -> {
                val key = JWT.create()
                    .withAudience(TypeOfUser.Visitor.toString())
                    .withIssuer(ISSUER)
                    .withClaim("username", userId.toInt())
                    .withExpiresAt(Date(System.currentTimeMillis() + 6000000))
                    .sign(Algorithm.HMAC512(SECRET))

                DEPENDENCIES[key] = user
                return key
            }

            TypeOfUser.Admin -> {
                val key = JWT.create()
                    .withAudience(TypeOfUser.Admin.toString())
                    .withIssuer(ISSUER)
                    .withClaim("username", userId.toInt())
                    .withExpiresAt(Date(System.currentTimeMillis() + 6000000))
                    .sign(Algorithm.HMAC512(SECRET))

                DEPENDENCIES[key] = user
                return key
            }
        }

    }

    private fun decodeToken(token: String): StatusOfToken {
        val decoder = JWT.decode(token)
        return StatusOfToken(
            Encryptor.encryptThis(decoder.claims["username"].toString()),
            decoder.expiresAt <= Date(),
            decoder.audience.first()
        )
    }

    internal fun getAdminByToken(token: String): Admin {
        val status = decodeToken(token)
        if (status.isExpired) {
            throw IllegalAccessException("Token is expired.")
        }
        if (status.role != "Admin") {
            throw IllegalAccessException("You are not admin.")
        }
        if (!DEPENDENCIES.containsKey(token)) {
            throw IllegalAccessException("Your token is not valid.")
        }
        return DEPENDENCIES[token] as Admin
    }

    internal fun getVisitorByToken(token: String): Visitor {
        val status = decodeToken(token)
        if (status.isExpired) {
            throw IllegalAccessException("Token is expired.")
        }
        if (status.role != "Visitor") {
            throw IllegalAccessException("You are not visitor.")
        }
        if (!DEPENDENCIES.containsKey(token)) {
            throw IllegalAccessException("Your token is not valid.")
        }
        return DEPENDENCIES[token] as Visitor
    }
}