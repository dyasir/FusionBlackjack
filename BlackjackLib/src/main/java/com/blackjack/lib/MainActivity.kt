package com.blackjack.lib

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blackjack.lib.game.GameViewCallback
import kotlinx.android.synthetic.main.jk_activity_main.*
import kotlinx.android.synthetic.main.jk_dialog_help.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by shen on 17/5/26.
 */

class MainActivity : AppCompatActivity() {
    /**
     * 每天最多免费赠送次数
     */
    val MAX_FREE_COUNT_PER_DAY = 3
    /**
     * 每次赠送筹码数
     */
    val FREE_SCORE = 1000


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            if (context !is AppCompatActivity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jk_activity_main)

        helpView.setItemClickListener(helpViewOnClickListener)
        gameView.gameViewCallback = gameViewCallback

        var score = getScore()
        gameView.updateScore(score)
        gameView.init()

    }

    private val helpViewOnClickListener: View.OnClickListener = View.OnClickListener { v: View? ->
        when (v!!.id) {
            R.id.help -> openHelpPage()
            R.id.back -> finish()
        }
    }

    private fun openHelpPage() {
        val dialog = Dialog(this, R.style.TransparentDialog)
        val view = LayoutInflater.from(this).inflate(R.layout.jk_dialog_help, null, false)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(true)
        view.dialogRootView.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    val gameViewCallback: GameViewCallback = object : GameViewCallback {
        override fun alertText(text: String) {
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show();
        }

        override fun onScoreChanged(newScore: Int) {
            saveScore(newScore)
            if (newScore == 0) {
                var freeScore = getFreeScore()
                if (freeScore == 0) {
                    alertText("You have run out of chips today, please come back tomorrow!")
                } else {
                    alertText("You got" + freeScore.toString() + "give away chips, please enjoy")
                    gameView.post {
                        gameView.updateScore(freeScore)
                        gameView.init()
                    }
                }
            }
        }
    }

    private fun getScore(): Int {
        return PreferenceManager.getDefaultSharedPreferences(this).getInt("score", FREE_SCORE)
    }

    fun saveScore(score: Int) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("score", score).apply()
    }

    fun getFreeScore(): Int {
        var simpleDateFormat = SimpleDateFormat("yyyyMMdd")
        var dateStr = simpleDateFormat.format(Date())
        var freeDateTimes = PreferenceManager.getDefaultSharedPreferences(this).getString("free_score", "")
        var freeDate: String? = null
        var lastFreeCount: Int = 0
        if (freeDateTimes != null && freeDateTimes.length > 9) {
            freeDate = freeDateTimes.substring(0, 8)
            lastFreeCount = freeDateTimes.substring(9).toInt()
        }
        if (dateStr.equals(freeDate)) {
            if (lastFreeCount < MAX_FREE_COUNT_PER_DAY) {
                lastFreeCount++
            } else {
                return 0
            }
        } else {
            lastFreeCount = 1
        }

        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("free_score", dateStr + "_" + lastFreeCount).apply()
        return FREE_SCORE
    }
}