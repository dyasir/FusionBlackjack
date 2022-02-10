package com.blackjack.lib.game.coin

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.blackjack.lib.CommonUtils
import com.blackjack.lib.L
import com.blackjack.lib.R
import com.blackjack.lib.SoundPoolManager
import com.blackjack.lib.game.GameView
import kotlinx.android.synthetic.main.coin_layout.view.*

/**
 * Created by shen on 17/5/28.
 */
class CoinView(context: Context?, attrs: AttributeSet?) : FrameLayout(context!!, attrs) {
    companion object Constants {
        var coinMap: HashMap<Int, Int> = HashMap()

        init {
            coinMap[5] = R.drawable.score5
            coinMap[10] = R.drawable.score10
            coinMap[25] = R.drawable.score25
            coinMap[100] = R.drawable.score100
            coinMap[500] = R.drawable.score500
        }
    }

    private val scoreType1 = intArrayOf(5, 10, 25, 100)
    private val scoreType2 = intArrayOf(10, 25, 100, 500)
    private var currentScoreType: IntArray = scoreType1
    var downScore: Int = 0
    var _maxCoin: Int = 1000
    private lateinit var childArray: Array<View>
    var coinCallback: CoinCallback? = null

    private var animatorSet: AnimatorSet = AnimatorSet()

    init {
        View.inflate(context, R.layout.coin_layout, this)
        bottomCoin.layoutParams.height = CommonUtils.getScreenWidth(context!!) / 4
        betCoin.betCallback = object : BetCoin.BetCallback {
            override fun onResetBet(score: Int) {
                downScore -= score
                val leftScore = _maxCoin + score
                checkScoreType(leftScore)
                refresh()
                coinCallback?.onPlayedCoin(-score, downScore)
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        childArray = arrayOf(score1, score2, score3, score4)

        score1.setOnClickListener {
            downScore(0)
        }
        score2.setOnClickListener {
            downScore(1)
        }
        score3.setOnClickListener {
            downScore(2)
        }
        score4.setOnClickListener {
            downScore(3)
        }
    }

    private fun getCoinViewHeight(): Int {
        return CommonUtils.getScreenWidth(context) / 4
    }

    fun setLocation(userCardCenterY: Int, userBetHintCenterY: Int) {
        L.d("setLocation userCarCenterY:$userCardCenterY, userBetHintCenterY:$userBetHintCenterY")
        val targetScale = 0.5F
        val coinViewHeight = getCoinViewHeight()
        val betCoinTop = userCardCenterY - coinViewHeight / 2
        animatorSet.duration = 500
        val targetTranslation = (userBetHintCenterY - userCardCenterY - coinViewHeight * targetScale / 2).toFloat()
        animatorSet.playTogether(ObjectAnimator.ofFloat(betCoin, "translationY", 0F, targetTranslation))
        animatorSet.playTogether(ObjectAnimator.ofFloat(betCoin, "scaleX", 1F, targetScale))
        animatorSet.playTogether(ObjectAnimator.ofFloat(betCoin, "scaleY", 1F, targetScale))
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                betCoin.removeClickCoinListener()
            }
        })

        val betCoinLayoutParams = (betCoin.layoutParams as RelativeLayout.LayoutParams)
        betCoinLayoutParams.topMargin = betCoinTop

    }

    fun setMaxCoin(maxCoin: Int) {
        this._maxCoin = maxCoin
        downScore = 0
        checkScoreType(maxCoin)
        refresh()
    }

    fun reset() {
        betCoin.reset()
    }

    fun checkScoreType(maxScore: Int) {
        currentScoreType = if (maxScore < 2000) {
            scoreType1
        } else {
            scoreType2
        }
    }

    fun refresh() {
        val leftScore = _maxCoin - downScore
        for (i in 3 downTo 0) {
            if (leftScore < currentScoreType[i]) {
                childArray[i].visibility = View.INVISIBLE
            } else {
                childArray[i].setBackgroundResource(coinMap[currentScoreType[i]] ?: 0)
                childArray[i].visibility = View.VISIBLE
            }
        }
    }

    interface CoinCallback {
        fun onPlayedCoin(betCoin: Int, totalBetCoin: Int)
    }

    private fun downScore(index: Int) {
        val addScore = currentScoreType[index]
        downScore += addScore
        val leftScore = _maxCoin - downScore
        checkScoreType(leftScore)
        refresh()
        betCoin.addCoin(addScore)
        coinCallback?.onPlayedCoin(addScore, downScore)
        SoundPoolManager.play(GameView.MusicType.Bet.name)
    }

    fun onCompleteBet() {
        hideBottomCoin()
        animatorSet.start()
    }

    private fun hideBottomCoin() {
        score1.visibility = View.INVISIBLE
        score2.visibility = View.INVISIBLE
        score3.visibility = View.INVISIBLE
        score4.visibility = View.INVISIBLE
    }
}