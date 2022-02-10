package com.blackjack.lib.game

/**
 * Created by shen on 17/5/28.
 */
interface GameViewCallback {
    fun alertText(text: String)//TODO

    fun onScoreChanged(newScore:Int)
}