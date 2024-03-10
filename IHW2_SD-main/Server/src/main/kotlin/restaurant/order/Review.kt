package restaurant.order

import kotlinx.serialization.Serializable

@Serializable
internal data class Review(var stars: Int, var comment: String, var isInitialized : Boolean = true) {
    constructor() : this(-1, "empty", false)
}