package com.example.sudokufreeads

object SudokuGame {
    var sudokuColorSquare= mutableListOf<SudokuColorSquare>()
    var numbersproblem= mutableListOf<NumbersProblem>()
    var numbersplayer= mutableListOf<NumbersPlayer>()
    var numbers= mutableListOf<Numbers>()
    var numberswrongplayer= mutableListOf<NumberWrongPlayer>()
    var numberprob= mutableListOf<Numbersprob>()


    init {

    }



    fun clear(){
        sudokuColorSquare.clear()
        numberprob.clear()
        numbersproblem.clear()
        numbersplayer.clear()
        numberswrongplayer.clear()
        numbers.clear()
    }

    fun findnumber(col:Int, row:Int): Numbers? {
        for(number in numbers){
            if(col == number.col && row == number.row){
                return number
            }
        }
        return null
    }
    fun findwrongnumber(col:Int, row:Int): NumberWrongPlayer? {
        for(number in numberswrongplayer){
            if(col == number.col && row == number.row){
                return number
            }
        }
        return null
    }

    fun numberat(col:Int, row:Int): NumbersPlayer? {
        for(number in numbersplayer){
            if(col == number.col && row == number.row){
                return number
            }
        }
        return null
    }

    fun numberproblemat(col:Int, row:Int): NumbersProblem? {
        for(number in numbersproblem){
            if(col == number.col && row == number.row){
                return number
            }
        }
        return null
    }

    fun sudokuColorSquare(col:Int, row:Int){
        sudokuColorSquare.clear()
        sudokuColorSquare.add(SudokuColorSquare(col, row))
    }

    fun readstring(string:String){
        var size=string.length
        for(i in 0..(size-1)/3){
            numberproblem(string[i * 3+1].toInt() - 49, string[i * 3 ].toInt() - 49, string[i * 3+ 2].toInt() - 48)
        }
    }

    fun numberproblem(col:Int, row:Int, number:Int){
        if(findnumber(col, row)==null) {
            numbersproblem.add(NumbersProblem(col, row, number))
            numbers.add(Numbers(col, row, number))
        }
    }

    fun numberplayer(col:Int, row:Int, number:Int){
        numbersplayer.add(NumbersPlayer(col, row, number))
        numbers.add(Numbers(col, row, number))
    }
    fun numberwrongplayer(col:Int, row:Int, number:Int){
        numberswrongplayer.add(NumberWrongPlayer(col, row, number))

    }
    fun findnumberprob(col: Int, row: Int, number2: Int): Numbersprob?{
        for(number in numberprob){
            if(col == number.col && row == number.row && number2==number.number){
                return number
            }
        }
        return null
    }

    fun numberprob(col: Int, row: Int, number: Int){
        numberprob.add(Numbersprob(col, row, number))
    }





}