package com.rangoli.digitalschool.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║   SplashActivity.kt                                          ║
 * ║   Splash screen with:                                        ║
 * ║   • Book logo + AI sparkle inside a dashed circle            ║
 * ║   • Dashed circle continuously rotating (Nimate-style)       ║
 * ║   • Brand name with Digi/School dual color                   ║
 * ║   • Slogan fade-up                                           ║
 * ║   • Bouncing loading dots                                    ║
 * ║   • Auto-navigate to MainActivity after 15s                  ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())

    // Total splash duration before navigating to next screen
    private val SPLASH_DURATION_MS = 15000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBrandText()
        startSplashAnimations()
        scheduleNavigation()
    }

    /**
     * Sets "Digi" in bold brand-blue and "School" in light weight
     * using a SpannableString on the brand TextView.
     */
    private fun setupBrandText() {
        val digiText = "Digi"
        val schoolText = "School"
        val full = digiText + schoolText

        val spannable = SpannableString(full)

        // "Digi" — bold + brand color
        val digiColor = ContextCompat.getColor(this, R.color.splash_brand_digi)
        spannable.setSpan(
            ForegroundColorSpan(digiColor),
            0, digiText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0, digiText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // "School" — light weight + secondary color
        val schoolColor = ContextCompat.getColor(this, R.color.splash_brand_school)
        spannable.setSpan(
            ForegroundColorSpan(schoolColor),
            digiText.length, full.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.NORMAL),
            digiText.length, full.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvBrand.text = spannable
    }

    /**
     * Runs all entrance animations in sequence:
     *  1. Deco circles fade in (0ms)
     *  2. Ring + inner ring fade/scale in (100ms)
     *  3. Logo book pop in with overshoot (200ms)
     *  4. AI sparkle pop in (550ms)
     *  5. Ring starts rotating (600ms — continuous)
     *  6. Brand text fade-up (800ms)
     *  7. Slogan fade-up (1000ms)
     *  8. Dots fade-up + start bouncing (1150ms)
     */
    private fun startSplashAnimations() {

        // ── 1. Deco circles ──────────────────────────────────────
        animateFadeIn(binding.decoCircle1, 0L, 800L)
        animateFadeIn(binding.decoCircle2, 200L, 800L)

        // ── 2. Rings appear ──────────────────────────────────────
        animateScaleFadeIn(binding.ringRotate, 0.6f, 100L, 700L)
        animateFadeIn(binding.ringInner, 150L, 600L)

        // ── 3. Logo book pop-in ───────────────────────────────────
        handler.postDelayed({
            val anim = AnimationUtils.loadAnimation(this, R.animator.anim_logo_in)
            binding.logoBook.visibility = View.VISIBLE
            binding.logoBook.startAnimation(anim)
            binding.logoBook.alpha = 1f
        }, 200)

        // ── 4. AI sparkle pop-in ──────────────────────────────────
        handler.postDelayed({
            val anim = AnimationUtils.loadAnimation(this, R.animator.anim_logo_in)
            binding.aiStar.startAnimation(anim)
            binding.aiStar.alpha = 1f
        }, 550)

        // ── 5. Start ring continuous rotation ─────────────────────
        handler.postDelayed({
            startRingRotation()
        }, 600)

        // ── 6. Brand name fade-up ─────────────────────────────────
        handler.postDelayed({
            val anim = AnimationUtils.loadAnimation(this, R.animator.anim_fade_up)
            binding.tvBrand.startAnimation(anim)
            binding.tvBrand.alpha = 1f
        }, 800)

        // ── 7. Slogan fade-up ─────────────────────────────────────
        handler.postDelayed({
            val anim = AnimationUtils.loadAnimation(this, R.animator.anim_fade_up)
            binding.tvSlogan.startAnimation(anim)
            binding.tvSlogan.alpha = 1f
        }, 1000)

        // ── 8. Dots fade-up then bounce ───────────────────────────
        handler.postDelayed({
            val anim = AnimationUtils.loadAnimation(this, R.animator.anim_fade_up)
            binding.dotsContainer.startAnimation(anim)
            binding.dotsContainer.alpha = 1f

            // Staggered bounce for each dot
            handler.postDelayed({ startDotBounce(binding.dot1, 0L) }, 400)
            handler.postDelayed({ startDotBounce(binding.dot2, 170L) }, 400)
            handler.postDelayed({ startDotBounce(binding.dot3, 340L) }, 400)
        }, 1150)
    }

    /**
     * Continuous 360° rotation of the dashed ring using ObjectAnimator.
     * Duration = 12000ms per revolution (smooth, slower).
     */
    private fun startRingRotation() {
        val rotator = ObjectAnimator.ofFloat(binding.ringRotate, View.ROTATION, 0f, 360f).apply {
            duration = 12000L
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }
        rotator.start()
    }

    /**
     * Simple fade-in alpha animator.
     */
    private fun animateFadeIn(view: View, startDelay: Long, duration: Long) {
        view.alpha = 0f
        val anim = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
            this.startDelay = startDelay
            this.duration = duration
        }
        anim.start()
    }

    /**
     * Scale + fade in, starting from [fromScale].
     */
    private fun animateScaleFadeIn(
        view: View,
        fromScale: Float,
        startDelay: Long,
        duration: Long
    ) {
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

    /**
     * Bouncing dot: translates up and back in infinite loop with delay.
     */
    private fun startDotBounce(dot: View, startDelay: Long) {
        handler.postDelayed({
            val bounceUp = ObjectAnimator.ofFloat(dot, View.TRANSLATION_Y, 0f, -10f).apply {
                duration = 400L
                interpolator = AccelerateDecelerateInterpolator()
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
            val fadeUp = ObjectAnimator.ofFloat(dot, View.ALPHA, 0.35f, 1f).apply {
                duration = 400L
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
            AnimatorSet().apply {
                playTogether(bounceUp, fadeUp)
                start()
            }
        }, startDelay)
    }

    /**
     * Navigate to MainActivity after SPLASH_DURATION_MS.
     * Uses a smooth fade-out transition.
     */
    private fun scheduleNavigation() {
        handler.postDelayed({
            navigateToMain()
        }, SPLASH_DURATION_MS)
    }

    private fun navigateToMain() {
        // Fade-out the whole splash before navigating
        ObjectAnimator.ofFloat(binding.splashRoot, View.ALPHA, 1f, 0f).apply {
            duration = 350L
            start()
        }

        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java).apply {
                // Pass any needed extras here, e.g. deep link data
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)

            // Slide transition: new screen slides in from right
            overridePendingTransition(
                R.animator.anim_slide_in_right,   // entering activity
                R.animator.anim_slide_out_left    // exiting splash
            )
            finish()
        }, 350)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}