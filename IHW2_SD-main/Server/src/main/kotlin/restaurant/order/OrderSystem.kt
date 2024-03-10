package restaurant.order

import kotlinx.serialization.encodeToString
import restaurant.Logger
import restaurant.Serializer
import restaurant.dish.Menu
import java.util.PriorityQueue
import java.util.concurrent.atomic.AtomicInteger

class OrderSystem(private var maxNumberOfOrders: Int = 5) {
    private var orderIdGetter = 0
    private var statsModule: StatsModule
    val menuObj = Menu()

    init {
        tryToDeserialize()
        if (!isOrdersInitialized) {
            allOrders = mutableListOf()
        }
        statsModule = StatsModule(allOrders)
    }

    companion object {
        internal lateinit var allOrders: MutableList<Order>
        internal val numberOfThreads = AtomicInteger(0)
        private val comparator: Comparator<Order> = compareByDescending { it.level }
        private val waitingOrders = PriorityQueue(comparator)

        private val locker = Any()
        private val isOrdersInitialized
            get() = ::allOrders.isInitialized

        /**
         * Inner method for multithreading system that get last order from priority queue and start cooking his.
         * Besides, it is multithreading-safety, because it is possible that many threads will start call this method in one time.
         * @return object of Order, or null if queue is empty
         */
        internal fun getOrderFromQueue(): Order? {
            synchronized(locker) {
                if (waitingOrders.isEmpty()) {
                    return null
                }
                val result = waitingOrders.poll()
                Logger.writeToLog("Order ${result.orderId} from queue start working!")
                result.startOrder()
                return result
            }
        }
    }

    private val getOrderId: Int
        get() {
            ++orderIdGetter
            return orderIdGetter
        }

    /**
     * Inner method: match orderId with Order object
     * @param id: orderId
     * @return object of Order, or maybe null if it doesn't exist
     */
    private fun getOrderById(id: Int): Order? {
        return allOrders.find { it.orderId == id }
    }


    @JvmName("AddOrderIntToInt")
    fun addOrder(list: MutableMap<Int, Int>, level: ImportanceLevel, userId: Int): Int {
        val idForThisOrder = getOrderId
        val mapOrder = menuObj.getDishMapFromIds(list)
        val order = Order(mapOrder, level, userId, idForThisOrder)
        allOrders.add(order)
        order.acceptOrder(menuObj.acceptOrder(mapOrder))

        if (numberOfThreads.get() < maxNumberOfOrders) {
            Logger.writeToLog("Order #${order.orderId} for user #${userId} are starting working! Time: ${order.getMaxTime()}")
            order.startOrder()
        } else {
            Logger.writeToLog("Order #${order.orderId} for user #${userId} waiting in queue. Cooking time: ${order.getMaxTime()}")
            waitingOrders.add(order)
        }
        serialize()
        return idForThisOrder
    }

    /**
     * Add another dish (one dish) to existed order with id = orderID. This method also
     * checks the possibility to add the dish to this order and,
     * if it's not possible, return this result to user
     * @param orderId: id of order, where system need to add the dish
     * @param dishId: id of dish, which should be added to the order
     * @param userId: id of user, who calls this method
     * @param amount: how many dish should be added to order
     * @throws SecurityException: if user try to check the order that created not by himself
     * @throws SecurityException: if order with this id doesn't exist
     */
    fun addToExistedOrder(orderId: Int, dishId: Int, userId: Int, amount: Int = 1) {
        val order = getOrderById(orderId)
        menuObj.acceptOrder(menuObj.getDishMapFromIds(mutableMapOf(dishId to amount)))
        if (order == null) {
            Logger.writeToLogResult("This order #$orderId doesn't exists.", Logger.Status.ERROR)
            throw SecurityException("This order #$orderId doesn't exist.")
        } else if (order.userId != userId) {
            Logger.writeToLogResult("You doesn't have necessary right to see this order.", Logger.Status.ERROR)
            throw SecurityException("You doesn't have necessary right to see this order.")
        } else {
            val dish = menuObj.getDishById(dishId)
            if (dish == null) {
                Logger.writeToLogResult(
                    "Try to add to order $orderId for user #${order.userId} unlisted dish $dishId",
                    Logger.Status.OK
                )
            } else {
                order.addDish(dish, amount)
            }
        }
        serialize()
    }

    /**
     *  Add other dishes (not one dish) to existed order with id = orderID. This method also
     *  checks the possibility to add the dish to this order and,
     *  if it's not possible, return this result to user
     *
     * @param orderId: id of order, where system need to add dishes
     * @param mapOfDishes: map of dishes, where key is dishId and value is the amount of dish.
     * @param userId: id of user, who calls this method
     */
    fun addToExistedOrder(orderId: Int, mapOfDishes: MutableMap<Int, Int>, userId: Int) {
        val order = getOrderById(orderId)
        menuObj.acceptOrder(menuObj.getDishMapFromIds(mapOfDishes))
        if (order == null) {
            Logger.writeToLogResult("This order #$orderId doesn't exists.", Logger.Status.ERROR)
        } else if (order.userId != userId) {
            Logger.writeToLogResult("You doesn't have necessary right to see this order.", Logger.Status.ERROR)
            throw SecurityException("You doesn't have necessary right to see this order.")
        } else {
            for ((key, value) in mapOfDishes) {
                val dish = menuObj.getDishById(key)
                if (dish == null) {
                    Logger.writeToLogResult(
                        "Try to add to order $orderId unlisted dish #$key",
                        Logger.Status.ERROR
                    )
                } else {
                    order.addDish(dish, value)
                }
            }
        }
        serialize()
    }

    /**
     * Cancel the existed order.
     * @param orderId: id of order that is need to be cancelled
     * @param userId: id of user, who calls this method
     */
    fun cancelOrder(orderId: Int, userId: Int) {
        val order = getOrderById(orderId)
        if (order == null) {
            Logger.writeToLogResult("Try to cancel order. Order #$orderId doesn't exists.", Logger.Status.ERROR)
            throw Exception("You cannot cancel non-existed order.")
        } else if (order.userId != userId) {
            Logger.writeToLogResult("You doesn't have necessary right to see this order.", Logger.Status.ERROR)
            throw SecurityException("You doesn't have necessary right to see this order.")
        } else {
            order.cancelOrder()
        }
        serialize()
    }

    /**
     * Get order status by orderId
     *
     * @param orderId: id of order
     * @param userId: id of user, who calls this method
     * @return order status
     */
    fun getOrderStatus(orderId: Int, userId: Int): OrderStatus {
        val order = getOrderById(orderId)
        return if (order == null) {
            Logger.writeToLogResult("Try to get order status. Order #$orderId doesn't exists.", Logger.Status.ERROR)
            throw SecurityException("Uncorrected number of order")
        } else if (order.userId != userId) {
            Logger.writeToLogResult("You doesn't have necessary right to see this order.", Logger.Status.ERROR)
            throw SecurityException("You doesn't have necessary right to see this order.")
        } else {
            Logger.writeToLogResult("OrderStatus for user #${order.userId}  is ${order.getStatus()}", Logger.Status.OK)
            order.getStatus()
        }
    }

    fun payOrder(orderId: Int, userId: Int): Int {
        val order = getOrderById(orderId)
        serialize()
        if (order != null && order.userId != userId) {
            Logger.writeToLogResult("You doesn't have necessary right to see this order.", Logger.Status.ERROR)
            throw SecurityException("You doesn't have necessary right to see this order.")
        } else if (order != null) {
            return order.payOrder()
        } else {
            Logger.writeToLogResult(
                "Try to pay for order. Order #$orderId with this id doesn't exists",
                Logger.Status.ERROR
            )
            throw SecurityException("You cannot cancel non-existed order.")
        }
    }

    /**
     * Set review to order.
     *
     * @param orderId: id of order
     * @param stars: how many stars visitor put to the order
     * @param comment: user's comment that he leave to this order
     * @param userId: id of user, who calls this method
     */
    fun setReviewToOrder(orderId: Int, stars: Int, comment: String, userId: Int) {
        val order = getOrderById(orderId)
        if (order != null && order.userId != userId) {
            Logger.writeToLogResult("You doesn't have necessary right to see this order.", Logger.Status.ERROR)
            throw SecurityException("You doesn't have necessary right to see this order.")
        } else if (order != null) {
            order.setReview(stars, comment)
        } else {
            Logger.writeToLogResult(
                "Try to set review to order. Order with this id #$orderId doesn't exists",
                Logger.Status.ERROR
            )
            throw SecurityException("You cannot cancel non-existed order.")
        }
        serialize()
    }

    /**
     * Admin method: increase / decrease the number of dish in restaurant by delta
     *
     * @param dishId: id of dish, number of that system should increase / decrease
     * @param delta: the number of portions of dish you need
     */
    fun increaseTheNumber(dishId: Int, delta: Int) {
        menuObj.increaseAmountOfDish(dishId, delta)
        serialize()
    }

    /**
     * @return returns statistics of the restaurant for the admin
     */
    fun getStatistics(): String {
        return statsModule.getStatistic()
    }

    private fun serialize() {
        Serializer.write(Serializer.json.encodeToString(allOrders), Serializer.allOrdersFile)
        Serializer.write(Serializer.json.encodeToString(orderIdGetter), Serializer.orderIdGetterFile)
    }

    private fun tryToDeserialize() {
        try {
            allOrders = Serializer.json.decodeFromString(Serializer.read(Serializer.allOrdersFile)!!)
            Logger.writeToLog("AllOrders in OrderSystem has deserialized successfully!")
        } catch (ex: Exception) {
            Logger.writeToLog("AllOrders deserialization: ${ex.message.toString()}")
        }

        try {
            orderIdGetter = Serializer.json.decodeFromString(Serializer.read(Serializer.orderIdGetterFile)!!)
            Logger.writeToLog("OrderIdGetter in OrderSystem has deserialized successfully!")
        } catch (ex: Exception) {
            Logger.writeToLog("OrderIdGetter deserialization: ${ex.message.toString()}")
        }
    }

}