package com.blackjack.lib.game.coin

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.blackjack.lib.CommonUtils
import com.blackjack.lib.R
import com.blackjack.lib.SoundPoolManager
import com.blackjack.lib.game.GameView

/**
 * Created by shen on 17/5/28.
 */
class BetCoin(context: Context?, attrs: AttributeSet?) : FrameLayout(context!!, attrs) {


    private val marginGap = context?.let { CommonUtils.dpToPx(it, 5) }

    var betCallback: BetCallback? = null

    fun addCoin(score: Int) {
        val view = View(context)
        view.setBackgroundResource(CoinView.coinMap[score] ?: 0)
        val width = CommonUtils.getScreenWidth(context) / 4 - (context.resources.getDimensionPixelSize(
            R.dimen.coin_padding) * 2)
        val layoutParams = LayoutParams(width, width)
        layoutParams.gravity = Gravity.CENTER
        layoutParams.leftMargin = marginGap!! * childCount.coerceAtMost(5)
        view.isSoundEffectsEnabled = false
        view.setOnClickListener {
            SoundPoolManager.play(GameView.MusicType.Bet.name)
            removeView(view)
            betCallback?.onResetBet(score)
        }
        addView(view, layoutParams)
    }

    fun reset() {
        translationY = 0F
        scaleX = 1F
        scaleY = 1F
        removeAllViews()
    }

    fun removeClickCoinListener() {
        for (i in 0 until childCount) {
            getChildAt(i).setOnClickListener(null)
        }
    }

    interface BetCallback {
        /**
         * 减少跟住
         */
        fun onResetBet(score: Int)
    }
}