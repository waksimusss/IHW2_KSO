import types.TypeOfUser
import types.ProductFeatures

// Класс консольного приложения
class ConsoleSystem {
    private var client = Client()

    val typeOfUser: TypeOfUser
        get() = client.typeOfUser

    init {
        showEntryMenu()
    }

    private fun showEntryMenu(): String {
        return """
            Вход в систему:
            1. Войти как пользователь.
            2. Войти как администратор.
            3. Зарегистрироваться как пользователь.
            4. Зарегистрироваться как администратор.
            5. Посмотреть меню ресторана.
            0. Выйти из приложения.
        """.trimIndent()
    }

    fun makeEntryChoice() {
        try {
            println(showEntryMenu())
            val input = correctChoiceBorders(readln(), 1, 5)
            when (input) {
                //Пользователь
                1 -> {
                    val rawData = inputAccountData()
                    val status = client.loginVisitor(rawData.first, rawData.second)
                    if (status == "OK") {
                        client.typeOfUser = TypeOfUser.Visitor
                        client.isLogged = true
                    }
                    println("Сообщение: $status")
                }
                //Админ
                2 -> {
                    val rawData = inputAccountData()
                    val status = client.loginAdmin(rawData.first, rawData.second)
                    if (status == "OK") {
                        client.typeOfUser = TypeOfUser.Admin
                        client.isLogged = true
                    }
                    println("Сообщение: $status")
                }
                //Новый пользователь
                3 -> {
                    val rawData = inputAccountData()
                    val status = client.registerNewVisitor(rawData.first, rawData.second)
                    println("Сообщение: $status")
                }
                //Новый админ
                4 -> {
                    val rawData = inputAccountData()
                    val status = client.registerNewAdmin(rawData.first, rawData.second)
                    println("Сообщение: $status")
                }
                //Вывод меню
                5 -> {
                    println(client.getMenu())
                }

                0 -> {
                    client.typeOfUser = TypeOfUser.Exit
                }
            }
        } catch (ex: Exception) {
            println("Ошибка: ${ex.message.toString()}")
        }
    }

    private fun showAdminMenu(): String {
        return """
            Меню администратора:
            1. Добавить блюдо в меню.
            2. Убрать блюдо из меню по его id. 
            3. Изменить характеристику блюда по его id.
            4. Увеличить количество единиц блюда по его id.
            5. Получить статистику по ресторану.
            6. Посмотреть меню ресторана.
            7. Выйти из аккаунта.
            0. Выйти из приложения.
        """.trimIndent()
    }

    fun makeAdminChoice() {
        try {
            println(showAdminMenu())
            val choice = correctChoiceBorders(readln(), 1, 8)
            when (choice) {
                //Добавление блюда в меню
                1 -> {
                    print("Название блюда: ")
                    val name = readln()
                    print("Стоимость блюда: ")
                    val price = correctChoiceBorders(readln(), 0, Int.MAX_VALUE)
                    print("Длительность приготовления. (Формат: Xh, Ym, Zs. Например, 1h 10m 10s: ")
                    val duration = readln()
                    print("Оставшееся кол-во порций. Значение по умолчанию 1: ")
                    val count = fromStringToInt(readln())
                    val result = client.addDishToMenu(name, price, duration, count)
                    println("ID блюда = $result")
                }
                //Удаление блюда из меню
                2 -> {
                    print("Введите ID блюда: ")
                    val id = correctChoiceBorders(readln(), 0, Int.MAX_VALUE)
                    val result = client.removeDishFromMenu(id)
                    println("Сообщение: $result")
                }
                //Изменение характеристик блюда
                3 -> {
                    print("Введите ID блюда: ")
                    val id = correctChoiceBorders(readln(), 0, Int.MAX_VALUE)
                    print("Введите характеристику для изменения: Name, Price, Time: ")
                    val feature = readln().lowercase()
                    if (feature !in mutableListOf("name", "price", "time")) {
                        throw IndexOutOfBoundsException("Неверный ввод.")
                    }
                    var featureToChange: ProductFeatures = ProductFeatures.Name
                    when (feature) {
                        "name" -> {
                            featureToChange = ProductFeatures.Name
                        }

                        "price" -> {
                            featureToChange = ProductFeatures.Price
                        }

                        "time" -> {
                            featureToChange = ProductFeatures.Time
                        }
                    }

                    print("Введите изменения: ")
                    val changes = readln()

                    val result = client.setFeaturesToDish(id, featureToChange, changes)
                    println("Сообщение: $result")
                }
                // Увеличить кол-во блюда на
                4 -> {
                    print("Введите ID блюда: ")
                    val id = correctChoiceBorders(readln(), 0, Int.MAX_VALUE)
                    print("Введите на сколько пополнить / уменьшить его запас: ")
                    val delta = correctChoiceBorders(readln(), Int.MIN_VALUE, Int.MAX_VALUE)

                    val result = client.increaseTheNumberOfDish(id, delta)
                    println("Сообщение: $result")
                }
                //Статистика ресторана
                5 -> {
                    println("Сообщение:\n${client.getStatistic()}")
                }
                //Меню
                6 -> {
                    println(client.getMenu())
                }

                7 -> {
                    client.isLogged = false
                    client.typeOfUser = TypeOfUser.None
                    client.exitAdmin()
                }

                0 -> {
                    client.typeOfUser = TypeOfUser.Exit
                }
            }
        } catch (ex: Exception) {
            println(ex.message.toString())
        }
    }


    private fun showVisitorMenu(): String {
        return """
            Меню посетителя:
            1. Добавить новый заказ.
            2. Добавить блюдо / блюда в существующий заказ.
            3. Получить статус заказа по его ID.
            4. Отменить заказ по его ID.
            5. Заплатить за заказ.
            6. Оставить отзыв о заказе.
            7. Посмотреть меню ресторана.
            8. Выйти из аккаунта.
            0. Выйти из приложения.
        """.trimIndent()
    }

    fun makeVisitorChoice() {
        try {
            println(showVisitorMenu())
            val choice = correctChoiceBorders(readln(), 1, 9)
            when (choice) {
                //Новый заказ
                1 -> {
                    println("\t Меню \t \n${client.getMenu()}")
                    val list = inputOrderList()
                    val result = client.makeOrder(list)
                    println("ID заказа = $result")
                }
                //Добавить блюдо в заказ
                2 -> {
                    println("\t Меню \t \n${client.getMenu()}")
                    print("Введите ID вашего заказа: ")
                    val orderId = correctChoiceBorders(readln(), 1, Int.MAX_VALUE)

                    if (client.getOrderStatus(orderId) != "Cooking") {
                        println("Ваш заказ уже готов.")
                    } else {
                        val list = inputOrderList()
                        val result = client.addToOrder(orderId, list)
                        println("Сообщение: $result")
                    }
                }
                //Статус заказа по его ID.
                3 -> {
                    print("Введите ID вашего заказа: ")
                    val orderId = correctChoiceBorders(readln(), 1, Int.MAX_VALUE)
                    val result = client.getOrderStatus(orderId)
                    println("Сообщение: $result")
                }
                //Отмена заказа
                4 -> {
                    print("Введите ID вашего заказа: ")
                    val orderId = correctChoiceBorders(readln(), 1, Int.MAX_VALUE)
                    val result = client.cancelOrder(orderId)
                    println("Сообщение: $result")
                }
                //Оплата заказа
                5 -> {
                    print("Введите ID вашего заказа: ")
                    val orderId = correctChoiceBorders(readln(), 1, Int.MAX_VALUE)
                    val result = client.payOrder(orderId)
                    println("Сообщение: $result")
                }
                //Оценка обслуживания
                6 -> {
                    print("Введите ID вашего заказа: ")
                    val orderId = correctChoiceBorders(readln(), 1, Int.MAX_VALUE)
                    print("Пожалуйста, поставьте оценку от 1 до 5 для нашего заведения: ")
                    val stars = correctChoiceBorders(readln(), 1, 5)
                    print("Ваш комментарий к заказу: ")
                    val comment = readln()

                    val result = client.leaveFeedbackAboutOrder(orderId, stars, comment)
                    println("Сообщение: $result")
                }

                7 -> {
                    println(client.getMenu())
                }

                8 -> {
                    client.isLogged = false
                    client.typeOfUser = TypeOfUser.None
                    client.exitVisitor()
                }

                0 -> {
                    client.typeOfUser = TypeOfUser.Exit
                }
            }
        } catch (ex: Exception) {
            println(ex.message.toString())
        }
    }

    private fun correctChoiceBorders(str: String, leftBorder: Int, rightBorder: Int): Int {
        val value = str.toInt()
        if (value !in leftBorder..rightBorder && value != 0) {
            throw IndexOutOfBoundsException("Неверный ввод.")
        }
        return value
    }

    private fun fromStringToInt(str: String): Int {
        return try {
            str.toInt()
        } catch (ex: Exception) {
            1
        }
    }

    //Создание списка заказов
    private fun inputOrderList(): MutableMap<Int, Int> {
        val order = mutableMapOf<Int, Int>()
        println("Введите ваш заказ в следующем формате: [ID_блюда] [Кол-во]. (В конце ввода поставьте -1):")
        var input = readln()
        while (input != "-1") {
            val splitData = input.split(" ")
            if (splitData.size != 2) {
                throw IndexOutOfBoundsException("Необходимо ввести 2 числа")
            }
            val dishId = splitData[0].toInt()
            val count = splitData[1].toInt()

            order[dishId] = count
            input = readln()
        }
        return order
    }

    //Получение данных аккаунта
    private fun inputAccountData(): Pair<String, String> {
        print("Введите ваш логин: ")
        val login = readln()
        print("Введите ваш пароль: ")
        val password = readln()
        return Pair(login, password)
    }

    internal fun final() {
        client.exitServer()
    }
}