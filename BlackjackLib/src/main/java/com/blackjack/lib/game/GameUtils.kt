package com.blackjack.lib.game

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by shen on 17/5/29.
 */
class GameUtils {
    var cardCount = 4
    var includeKing = false
    /**
     * color值 4=黑桃,3=红桃,2=梅花,1=方片
     * 牌面数值为1-54/52，牌值大小为card％13
     */
    fun getWashedCards(): ArrayList<Int> {
        val size = if (includeKing) 54 else 52
        val tempCard: Array<Int> = Array(size * cardCount) { i -> (i % size) + 1 }

        val length = tempCard.size
        val random = Random()
        // 换牌2次
        for (j in 0..1) {
            for (i in 0 until length) {
                val value = tempCard[i]
                val randomIndex = random.nextInt(length)
                tempCard[i] = tempCard[randomIndex]
                tempCard[randomIndex] = value
            }
        }
        return tempCard.toCollection(ArrayList())
    }

    fun totalCardCount(): Int {
        val size = if (includeKing) 54 else 52
        return size * cardCount
    }


    /**
     * 单张牌所表示的点数
     */
    fun getPoint(card: Int): Int {
        var cardValue = card % 13
        cardValue = if (cardValue == 0) 13 else cardValue
        return (if (cardValue > 10) 10 else cardValue)
    }


    fun blackJack(cardList: ArrayList<Int>): ArrayList<Int>? {
        var A: Int = -1
        var J: Int = -1
        for (i in cardList.size - 1 downTo 0) {
            val card = cardList[i]

            if (A == -1 && card % 13 == 1) {
                A = i
            }

            if (J == -1 && card % 13 >= 10) {
                J = i
            }
            if (A != -1 && J != -1) {
                break
            }
        }
        if (A != -1 && J != -1) {
            return arrayListOf(cardList.removeAt(A.coerceAtLeast(J)), cardList.removeAt(
                A.coerceAtMost(
                    J
                )
            ))
        }
        return null
    }

}