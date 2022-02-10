package com.blackjack.lib.game.card

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blackjack.lib.CommonUtils
import com.blackjack.lib.L
import com.blackjack.lib.R
import com.blackjack.lib.SoundPoolManager
import com.blackjack.lib.game.GameView
import kotlinx.android.synthetic.main.jk_back_card_layout.view.*
import kotlinx.android.synthetic.main.jk_card_layout.view.*


/**
 * Created by shen on 17/5/29.
 */
@SuppressLint("UseCompatLoadingForDrawables")
class CardView(context: Context?, attrs: AttributeSet?) : FrameLayout(context!!, attrs) {
    var isHitSecond = false

    private var resArray: Array<String> =
        arrayOf("jk_card_diamonds_", "jk_card_clubs_", "jk_card_hearts_", "jk_card_spades_")
    private var marginPx = context?.let { CommonUtils.dpToPx(it, 25) }
    var blackCard: Drawable
    private fun getCardRes(card: Int): Int {
        var cardValue = card % 13
        cardValue = (if (cardValue == 0) 13 else cardValue)
        val color = (card - 1) / 13
        return resources.getIdentifier(
            resArray[color] + cardValue.toString(),
            "drawable",
            context.packageName
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setUserCard(viewGroup: ViewGroup, cards: ArrayList<Int>, callback: OnClickListener?) {
        viewGroup.removeAllViews()
        viewGroup.layoutParams.width = 0

        val pointWidth = addPointHintView(viewGroup, "")
        viewGroup.layoutParams.width = pointWidth

        for (i in 0 until cards.size) {
            val imageView = ImageView(context)
            val drawable = context.getDrawable(getCardRes(cards[i]))
            imageView.setImageDrawable(drawable)
            if (cards.size > 1) {
                imageView.visibility = View.GONE
            }

            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = Gravity.CENTER
            if (drawable != null)
                layoutParams.leftMargin = if (i == 0) 0 else -(drawable.intrinsicWidth - marginPx!!)

            if (drawable != null)
                (if (i == 0) drawable.intrinsicWidth else marginPx)?.let {
                    updateViewWidth(
                        viewGroup,
                        it
                    )
                }
            viewGroup.addView(imageView, layoutParams)

            if (cards.size > 1) {
                CommonUtils.playHideViewAndShowViewFromTop(
                    imageView,
                    CommonUtils.getViewTop(viewGroup).toFloat(),
                    if (i == cards.size - 1) (if (cards.size == 1) 500L else 60) else 0,
                    if (i == cards.size - 1) callback else null
                )
            }
        }
    }

    fun addUserCard(card: Int, callback: OnClickListener?) {
        L.d("CardView addUserCard card:${card}")
        addCard(card, if (isHitSecond) userCardsLayout2 else userCardsLayout, callback)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun addCard(card: Int, viewGroup: ViewGroup, callback: OnClickListener?) {
        val imageView = ImageView(context)
        val drawable = context.getDrawable(getCardRes(card))
        imageView.setImageDrawable(drawable)
        imageView.visibility = View.GONE

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (drawable != null) {
            layoutParams.leftMargin = -(drawable.intrinsicWidth - marginPx!!)
        }

        marginPx?.let { updateViewWidth(viewGroup, it) }
        viewGroup.addView(imageView, layoutParams)
        SoundPoolManager.play(GameView.MusicType.ShuffleSingle.name)

        CommonUtils.playHideViewAndShowViewFromTop(
            imageView,
            CommonUtils.getViewTop(viewGroup).toFloat(),
            0L,
            callback
        )
    }

    private fun updateViewWidth(view: View, addWidth: Int) {
        val layoutParams = view.layoutParams
        L.d("updateViewWidth width:${layoutParams.width}, addWidth:${addWidth}")
        layoutParams.width = (if (layoutParams.width > 0) layoutParams.width else 0) + addWidth
    }

    fun setBankerCard(cards: ArrayList<Int>) {
        bankerCardsLayout.removeAllViews()
        bankerCardsLayout.layoutParams.width = 0

        val pointWidth = addPointHintView(bankerCardsLayout, "")
        bankerCardsLayout.layoutParams.width = pointWidth

        for (i in 0 until cards.size) {
            var itemView: View
            val drawable: Drawable? = context.getDrawable(getCardRes(cards[i]))
            itemView = if (i != 0) {
                val view = View.inflate(context, R.layout.jk_back_card_layout, null)
                view.backView.setImageDrawable(blackCard)
                view.frontView.setImageDrawable(drawable)
                view.setTag(R.id.cardView, cards[i])

                view
            } else {
                val imageView = ImageView(context)
                imageView.setImageDrawable(drawable)
                imageView
            }

            itemView.visibility = View.GONE

            val layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            if (drawable != null) {
                layoutParams.leftMargin = if (i == 0) 0 else -(drawable.intrinsicWidth - marginPx!!)
            }

            if (drawable != null) {
                (if (i == 0) drawable.intrinsicWidth else marginPx)?.let {
                    updateViewWidth(bankerCardsLayout,
                        it
                    )
                }
            }
            bankerCardsLayout.addView(itemView, layoutParams)

            if (i == 0) {
                CommonUtils.playHideViewAndShowViewFromTop(
                    itemView,
                    CommonUtils.getViewTop(bankerCardsLayout).toFloat(),
                    0L,
                    null
                )
            } else {
                CommonUtils.playHideViewAndShowViewFromTop(
                    itemView,
                    CommonUtils.getViewTop(bankerCardsLayout).toFloat(),
                    60L,
                    null
                )
            }
        }
    }

    fun addBankerCard(card: Int, callback: OnClickListener?) {
        L.d("CardView addBankerCard card:${card}")
        addCard(card, bankerCardsLayout, callback)
    }

    //翻开庄家第二张牌
    fun checkOverBankerCard(callback: OnClickListener) {
        if (bankerCardsLayout.childCount == 3) {
            val view: View = bankerCardsLayout.getChildAt(2)
            val tag = view.getTag(R.id.cardView)
            if (tag is Int) {
                turnCard(view, R.id.backView, R.id.frontView, callback)
                view.setTag(R.id.cardView, null)
                return
            }
        }
        callback.onClick(this)
    }

    fun reset() {
        userCardsLayout.removeAllViews()
        userCardsLayout.layoutParams.width = 0
        userCardsLayout2.removeAllViews()
        userCardsLayout2.layoutParams.width = 0
        bankerCardsLayout.removeAllViews()
        bankerCardsLayout.layoutParams.width = 0

        userCardsLayout2.visibility = View.GONE
        isHitSecond = false
    }

    private fun turnCard(cardRootView: View, backId: Int, frontId: Int, callbacks: OnClickListener) {
        val hideAnimation = CommonUtils.getRotateHideAnimation()
        hideAnimation.duration = 500
        hideAnimation.fillAfter = true

        val showAnimation = CommonUtils.getRotateShowAnimation()
        showAnimation.duration = 500
        showAnimation.fillAfter = true

        val frontView: View = cardRootView.findViewById(frontId)
        val animationListener = object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                callbacks.onClick(cardRootView)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {
            }
        }
        showAnimation.setAnimationListener(animationListener)

        hideAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                frontView.startAnimation(showAnimation)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {
                val hideAnimation = CommonUtils.getRotateHideAnimation()
                hideAnimation.duration = 0
                hideAnimation.fillAfter = true
                frontView.startAnimation(hideAnimation)
            }
        })
        val backView: View = cardRootView.findViewById(backId)
        backView.startAnimation(hideAnimation)
    }

    fun showUserPoint(point: Int) {
        if (isHitSecond) {
            if (userCardsLayout2.childCount > 0 && userCardsLayout2.getChildAt(0) is TextView) {
                (userCardsLayout2.getChildAt(0) as TextView).text = point.toString()
            }
        } else {
            if (userCardsLayout.childCount > 0 && userCardsLayout.getChildAt(0) is TextView) {
                (userCardsLayout.getChildAt(0) as TextView).text = point.toString()
            }
        }
    }

    fun showBankerPoint(point: Int) {
        if (bankerCardsLayout.childCount > 0 && bankerCardsLayout.getChildAt(0) is TextView) {
            (bankerCardsLayout.getChildAt(0) as TextView).text = point.toString()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun addPointHintView(viewGroup: ViewGroup, initStr: String?): Int {
        val pointHintView = TextView(context)
        pointHintView.gravity = Gravity.CENTER
        val drawable = context.getDrawable(R.drawable.jk_alert_small_bg)
        pointHintView.background = drawable
        initStr?.let { pointHintView.text = it }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = CommonUtils.dpToPx(context, 5)
        viewGroup.addView(pointHintView, layoutParams)
        return drawable?.intrinsicWidth ?: 0
    }

    fun onSplit(card: Int) {
        val view = userCardsLayout.getChildAt(2)
        userCardsLayout.removeView(view)

        userCardsLayout2.visibility = View.VISIBLE
        setUserCard(userCardsLayout2, arrayListOf(card), null)
    }

    init {
        View.inflate(context, R.layout.jk_card_layout, this)
        val layoutParams = userCardsLayout.layoutParams as LinearLayout.LayoutParams
        layoutParams.bottomMargin = CommonUtils.getScreenWidth(context!!) / 2
        val layoutParams2 = userCardsLayout2.layoutParams as LinearLayout.LayoutParams
        layoutParams2.bottomMargin = layoutParams.bottomMargin
        blackCard = context.resources.getDrawable(R.drawable.jk_card_back, context.theme)
        bankerCardsLayout.minimumHeight = blackCard.intrinsicHeight
    }
}