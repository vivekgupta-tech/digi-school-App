package com.rangoli.digitalschool.features.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rangoli.digitalschool.MainActivity
import com.rangoli.digitalschool.R
import com.rangoli.digitalschool.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())

    private val SPLASH_DURATION_MS = 15000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBrandText()
        startSplashAnimations()
        scheduleNavigation()
    }

    private fun setupBrandText() {
        val digiText = "Digi"
        val schoolText = "School"
        val full = digiText + schoolText

        val spannable = SpannableString(full)

        val digiColor = ContextCompat.getColor(this, R.color.splash_brand_digi)
        spannable.setSpan(ForegroundColorSpan(digiColor), 0, digiText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, digiText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val schoolColor = ContextCompat.getColor(this, R.color.splash_brand_school)
        spannable.setSpan(ForegroundColorSpan(schoolColor), digiText.length, full.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(Typeface.NORMAL), digiText.length, full.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvBrand.text = spannable
    }

    private fun startSplashAnimations() {
        animateFadeIn(binding.decoCircle1, 0L, 800L)
        animateFadeIn(binding.decoCircle2, 200L, 800L)

        animateScaleFadeIn(binding.ringRotate, 0.6f, 100L, 700L)
        animateFadeIn(binding.ringInner, 150L, 600L)

        handler.postDelayed({
            val anim = AnimationUtils.loadAnimation(this, R.animator.anim_logo_in)
            binding.logoBook.visibility = View.VISIBLE
            binding.logoBook.startAnimation(anim)
            binding.logoBook.alpha = 1f
        }, 200)

        handler.postDelayed({
            val anim = AnimationUtils.loadAnimation(this, R.animator.anim_logo_in)
            binding.aiStar.startAnimation(anim)
            binding.aiStar.alpha = 1f
            startSparkleBlink()
        }, 550)

        handler.postDelayed({
            startRingRotation()
        }, 600)

        handler.postDelayed({
            val anim = AnimationUtils.loadAnimation(this, R.animator.anim_fade_up)
            binding.tvBrand.startAnimation(anim)
            binding.tvBrand.alpha = 1f
        }, 800)

        handler.postDelayed({
            val anim = AnimationUtils.loadAnimation(this, R.animator.anim_fade_up)
            binding.tvSlogan.startAnimation(anim)
            binding.tvSlogan.alpha = 1f
        }, 1000)

        handler.postDelayed({
            val anim = AnimationUtils.loadAnimation(this, R.animator.anim_fade_up)
            binding.dotsContainer.startAnimation(anim)
            binding.dotsContainer.alpha = 1f

            handler.postDelayed({ startDotBounce(binding.dot1, 0L) }, 400)
            handler.postDelayed({ startDotBounce(binding.dot2, 170L) }, 400)
            handler.postDelayed({ startDotBounce(binding.dot3, 340L) }, 400)
        }, 1150)
    }

    private fun startSparkleBlink() {
        val scaleX = ObjectAnimator.ofFloat(binding.aiStar, View.SCALE_X, 1f, 1.25f, 1f).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = 1500L // Fixed: use duration property directly
        }
        val scaleY = ObjectAnimator.ofFloat(binding.aiStar, View.SCALE_Y, 1f, 1.25f, 1f).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = 1500L // Fixed
        }
        val alpha = ObjectAnimator.ofFloat(binding.aiStar, View.ALPHA, 1f, 0.6f, 1f).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = 1500L // Fixed
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun startRingRotation() {
        val rotator = ObjectAnimator.ofFloat(binding.ringRotate, View.ROTATION, 0f, 360f).apply {
            duration = 12000L // Fixed
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }
        rotator.start()
    }

    private fun animateFadeIn(view: View, startDelay: Long, duration: Long) {
        view.alpha = 0f
        val anim = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
            this.startDelay = startDelay
            this.duration = duration
        }
        anim.start()
    }

    private fun animateScaleFadeIn(view: View, fromScale: Float, startDelay: Long, duration: Long) {
        view.alpha = 0f
        view.scaleX = fromScale
        view.scaleY = fromScale

        val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, fromScale, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, fromScale, 1f)

        AnimatorSet().apply {
            playTogether(alpha, scaleX, scaleY)
            this.startDelay = startDelay
            this.duration = duration
            interpolator = OvershootInterpolator(1.2f)
            start()
        }
    }

    private fun startDotBounce(dot: View, startDelay: Long) {
        handler.postDelayed({
            val bounceUp = ObjectAnimator.ofFloat(dot, View.TRANSLATION_Y, 0f, -10f).apply {
                duration = 400L // Fixed
                interpolator = AccelerateDecelerateInterpolator()
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
            val fadeUp = ObjectAnimator.ofFloat(dot, View.ALPHA, 0.35f, 1f).apply {
                duration = 400L // Fixed
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
            AnimatorSet().apply {
                playTogether(bounceUp, fadeUp)
                start()
            }
        }, startDelay)
    }

    private fun scheduleNavigation() {
        handler.postDelayed({
            navigateToMain()
        }, SPLASH_DURATION_MS)
    }

    private fun navigateToMain() {
        ObjectAnimator.ofFloat(binding.splashRoot, View.ALPHA, 1f, 0f).apply {
            duration = 350L // Fixed
            start()
        }

        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java).apply {
                // Fixed: use property 'flags' instead of class setter
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)

            overridePendingTransition(
                R.animator.anim_slide_in_right,
                R.animator.anim_slide_out_left
            )
            finish()
        }, 350)
    }
//wertghyjukilop;[poiu
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}