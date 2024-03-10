package restaurant.dish

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class Dish(var dishId : Int, var name : String, var price : Int, var timeProduction : Duration)
