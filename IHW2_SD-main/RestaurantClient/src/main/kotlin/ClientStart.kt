import types.TypeOfUser

fun main() {
    val console = ConsoleSystem()
    while (true) {
        when (console.typeOfUser) {
            TypeOfUser.Admin -> {
                console.makeAdminChoice()
            }

            TypeOfUser.Visitor -> {
                console.makeVisitorChoice()
            }

            TypeOfUser.None -> {
                console.makeEntryChoice()
            }

            TypeOfUser.Exit -> {
                break
            }
        }
    }
    console.final()
}