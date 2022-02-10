package com.blackjack.lib

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.help_layout.view.*

/**
 * Created by shen on 17/5/28.
 */
class HelpView(context: Context?, attrs: AttributeSet?) : FrameLayout(context!!, attrs) {
    init {
        View.inflate(context,R.layout.help_layout, this)
    }

    fun setItemClickListener(onClickListener: OnClickListener) {
        back.setOnClickListener(onClickListener)
        help.setOnClickListener(onClickListener)
    }
}