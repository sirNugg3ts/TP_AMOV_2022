package pt.isec.amov.amovg11.utils

class TableSupporter {


    companion object{
        fun generateTable(level : Int) : MutableList<Any>{
            var generatedList : MutableList<Any>
            var operators : String = ""
            val minValues : Int = level*10
            val maxValues : Int = level*15 + 10
            generatedList = mutableListOf(20)
            if(level <= 2)
                operators = "+"
            else if(level <=4)
                operators = "+-"
            else if(level <=6)
                operators = "+-*"
            else
                operators = "+-*/"
            for(i in 0..4){
                for(j in 0..4) {
                    if ((i == 1 && j == 1) || (i == 1 && j == 3) || (i == 3 && j == 1) || (i == 3 && j == 3))
                        continue
                    if (i == 0 || i == 2 || i == 4) {
                        if (j % 2 == 0)
                            generatedList.add((minValues..maxValues).random())
                        else
                            generatedList.add(operators.random())

                    } else
                        generatedList.add(operators.random())
                }
            }
            return generatedList.asReversed()
        }


        fun checkOperation(num1 : Int,char1 : Char,num2 : Int,char2 : Char,num3 : Int) : Double{
            var hvPriority : Boolean = false
            var result : Double = 0.0
            if(char1 != '*' && char1 != '/')
                if(char2 =='*' || char2=='/')
                    hvPriority = true
            if(hvPriority){
                if(char2 == '*')
                    result = (num2 * num3).toDouble()
                else
                    result = (num2 / num3).toDouble()
            }else{
                when(char1){
                    '+'->result = (num1 + num2).toDouble()
                    '-'->result = (num1 - num2).toDouble()
                    '*'->result = (num1 * num2).toDouble()
                    '/'->result = (num1 / num2).toDouble()
                }
                when(char2){
                    '+'->result += num3
                    '-'->result -= num3
                    '*'->result *= num3
                    '/'->result /= num3
                }
            }

            return result
        }

    }
}