package com.inu.bus.activity

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import com.inu.bus.MyService
import com.inu.bus.R
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * Created by Minjae Son on 2018-08-07.
 * Updated by ByoungMean on 2019-09-03.
 */

class SplashActivity : AppCompatActivity(){

    // SplashActivity 상태를 나타내는 변수 설정
    private var mPaused = false
    private var isAnimationEnd = false
    private var mShouldFinish = false

    // activity_splash.xml 레이아웃 설정
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(applicationContext, MyService::class.java))
        setContentView(R.layout.activity_splash)
        activity_splash_layout.alpha = 0f           // 처음엔 투명하게
        activity_splash_layout
                .animate()
                .alpha(1f)                    // 서서히 선명하게
                .setDuration(2700)                  // 1000ms 동안
                .setListener(animationListener)
                .start()

        val gifimage = GlideDrawableImageViewTarget(iv_splash_bus)
        Glide.with(this).load(R.drawable.gif_splash_bus).into(gifimage)
    }

    // 애니메이션 이벤트 처리 등록
    private var animationListener = object : Animator.AnimatorListener{
        override fun onAnimationRepeat(animation: Animator?) { }

        //애니메이션이 끝나면 호출
        override fun onAnimationEnd(animation: Animator?) {
            if(!mPaused){
                nextActivity()
            }
            isAnimationEnd = true
        }

        override fun onAnimationCancel(animation: Animator?) { }

        override fun onAnimationStart(animation: Animator?) { }
    }

    // MainAcitivity를 작동
    private fun nextActivity(){
        startActivity(Intent(applicationContext, MainActivity::class.java))
        mShouldFinish = true
    }

    // 애니메이션이 끝났으면 MainActivity로
    override fun onResume() {
        super.onResume()
        mPaused = false
        if(isAnimationEnd){
            nextActivity()
        }
    }

    // MainActivity로 넘어갔다면 종료
    override fun onPause() {
        super.onPause()
        mPaused = true
        if(mShouldFinish){
            finish()
        }
    }
}