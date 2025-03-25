import kotlin.random.Random


fun main() {
    val navalBattle = NavalBattle()
    navalBattle.start()
}


class NavalBattle {
    private var fieldUser = Array(10) { Array(10) { 0 } }
    private val ships = mutableListOf<Int>(1111, 111, 11, 11, 1, 1, 1).shuffled()
    private val asciiSym = listOf(1040, 1041, 1042, 1043, 1044, 1045, 1046, 1047, 1048, 1050)
    private val shipCoordinates: MutableList<MutableList<String>> = mutableListOf()
    private val letters = mapOf(
        'А' to 0,
        'Б' to 1,
        'В' to 2,
        'Г' to 3,
        'Д' to 4,
        'Е' to 5,
        'Ж' to 6,
        'З' to 7,
        'И' to 8,
        'К' to 9
    )

    fun start() {
        val userField = createField(fieldUser, '1')
        val fieldPc = fieldUser.map { it.clone() }.toTypedArray()
        val allAssumptions: MutableList<Pair<Int, Int>> = mutableListOf()
        var userScore = 0
        var pcScore = 0
        var isGame = true

        createField(fieldPc, '2')

        viewField(userField)
        println("field pc")
        viewField(fieldPc)

        while (isGame) {
            println("Ваш ход! Введите координаты")
            val userInput = readln().trim().uppercase()

            if (userInput.isEmpty() || userInput.isBlank() || userInput.length !in 2..3 || !userInput.substring(1).all { it.isDigit() }) {
                println("Неверный формат ввода")
                continue
            }

            val firstLetter: Char = userInput[0]
            val secondNumber = userInput.substring(1).toInt()

            if (firstLetter in letters.keys.toList()) {
                if (secondNumber in 1..10) {
                    val (letter, number) = decodingInput(userInput)
//                    println(shipCoordinates)

                    when {
                        fieldPc[letter][number] == 2 && fieldPc[letter][number] != 1 && fieldPc[letter][number] != 8 -> {
                            fieldPc[letter][number] = 8
                            fieldUser[letter][number] = 8
                            userScore++
                            viewField(userField)

                            println("Вы попали по координатам ${getKeyByValue(letters, letter)}${number + 1}\n" +
                                    "${checkShipStatus(shipCoordinates, "${firstLetter}${secondNumber}")}\n")

                            if (isWin(userScore)) {
                                println("Вы победили!")
                                isGame = false
                                break
                            }

                        }
                        else -> {
                            viewField(userField)
                        }
                    }

                    println("Ход соперника")

                    while (true) {
                        val indx0 = letters[asciiSym.random().toChar()] ?: 0
                        val indx1 = Random.nextInt(0, 10)

                        if (Pair(indx0, indx1) !in allAssumptions) {
                            if (fieldUser[indx0][indx1] == 1 && fieldUser[indx0][indx1] != 2 && fieldUser[indx0][indx1] != 8) {
                                allAssumptions.add(Pair(indx0, indx1))
                                fieldUser[indx0][indx1] = 7
                                fieldPc[letter][number] = 7

                                viewField(fieldPc)
                                println("Противник попал по координатам ${getKeyByValue(letters, indx0)}${indx1 + 1}\n")

                                pcScore++

                                if (isWin(pcScore)) {
                                    println("Вы проиграли!")
                                    isGame = false
                                    break
                                }
                                break
                            } else {
                                //Если здесь убрать break и поставить continue, то ПК не будет ошибаться
                                println("Противник промахнулся\n")
                                break
                            }
                        }
                    }

                }else {
                    println("Некорректный ввод: второй символ должен быть от 1 до 10")
                    continue
                }
            }else {
                println("Некорректный ввод: первый символ должен быть в диапазоне A Б В Г Д Е Ж З И К")
                continue
            }
        }
    }


    private fun decodingInput(userInput: String) : Pair<Int, Int> {
        val letter = letters[userInput[0]]
        val number = userInput.substring(1).toInt() - 1

        return Pair(letter ?: 0, number)
    }

    private fun checkShipStatus(shipCoordinates: MutableList<MutableList<String>>, userInput: String) : String {
        var text = ""
        for (arr_coordinate in shipCoordinates) {
            if (arr_coordinate.contains(userInput)) {
                if (arr_coordinate.size > 1) {
                    arr_coordinate.remove(userInput)
                    text = "Вражеский корабль подбит!"
                    break
                } else {
                    arr_coordinate.remove(userInput)
                    text = "Вражеский корабль уничтожен!"
                    break
                }
            }
        }
        return text
    }

    private fun convertCoordinatesToLabel(coordinate: Pair<Int, Int>, letters: Map<Char, Int>): String {
        var convertedCoordinate = ' '
        for (num in letters.keys) {
            if (letters[num] == coordinate.first) {
                convertedCoordinate = num
                break
            }
        }
        return "${convertedCoordinate}${coordinate.second + 1}"
    }

    private fun getKeyByValue(map: Map<Char, Int>, value: Int): Char {
        var char = ' '
        for (i in map.keys) {
            if (map[i] == value) {
                char = i
                break
            }
        }
        return char
    }

    private fun isWin(num: Int) = num == ships.joinToString("").length

    private fun viewField(field: Array<Array<Int>>) {
        println("   A Б В Г Д Е Ж З И К")
        println("   1 2 3 4 5 6 7 8 9 10")
        var check = true
        while (check) {
            var counter = 1
            for (i in 0 .. field.size - 1) {
                println(if (counter.toString().length == 2) "$counter ${field[i].joinToString(" ")}" else "$counter  ${field[i].joinToString(" ")}")
                counter++
                if (counter == 11) check = false
            }
        }
    }

    private fun createField(field: Array<Array<Int>>, sym: Char) : Array<Array<Int>> {
        for (ship in ships) {
            val shipLength = ship.toString().length
            var placed = false

            while (!placed) {
                val orientation = Random.nextInt(0, 2)
                val numHorz = Random.nextInt(0, 10)
                val numVert = Random.nextInt(0, 10)

                if (orientation == 0) {
                    if (numVert + shipLength <= 10) {
                        var canPlace = true
                        for (j in 0 until shipLength) {
                            if (field[numHorz][numVert + j] != 0 ||
                                (numVert + j > 0 && field[numHorz][numVert + j - 1] != 0) ||
                                (numVert + j < 9 && field[numHorz][numVert + j + 1] != 0) ||
                                (numHorz > 0 && field[numHorz - 1][numVert + j] != 0) ||
                                (numHorz < 9 && field[numHorz + 1][numVert + j] != 0)) {
                                canPlace = false
                                break
                            }
                        }
                        if (canPlace) {
                            val shipHealth: MutableList<String> = mutableListOf()
                            for (j in 0 until shipLength) {
                                field[numHorz][numVert + j] = sym.digitToInt()

                                if (sym == '2') {
                                    shipHealth.add(convertCoordinatesToLabel(Pair(numHorz, numVert + j), letters))
                                }
                            }

                            if (shipHealth.isNotEmpty()) shipCoordinates.add(shipHealth)
                            placed = true
                        }
                    }
                } else {
                    if (numHorz + shipLength <= 10) {
                        var canPlace = true
                        for (j in 0 until shipLength) {
                            if (field[numHorz + j][numVert] != 0 ||
                                (numHorz + j > 0 && field[numHorz + j - 1][numVert] != 0) ||
                                (numHorz + j < 9 && field[numHorz + j + 1][numVert] != 0) ||
                                (numVert > 0 && field[numHorz + j][numVert - 1] != 0) ||
                                (numVert < 9 && field[numHorz + j][numVert + 1] != 0)) {
                                canPlace = false
                                break
                            }
                        }
                        if (canPlace) {
                            val shipHealth: MutableList<String> = mutableListOf()
                            for (j in 0 until shipLength) {
                                field[numHorz + j][numVert] = sym.digitToInt()

                                if (sym == '2') {
                                    shipHealth.add(convertCoordinatesToLabel(Pair(numHorz + j, numVert), letters))
                                }
                            }

                            if (shipHealth.isNotEmpty()) shipCoordinates.add(shipHealth)
                            placed = true
                        }
                    }
                }
            }
        }
        return field
    }
}