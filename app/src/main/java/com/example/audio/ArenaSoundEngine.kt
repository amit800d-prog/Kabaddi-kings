package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Lightweight offline synthesizer for Kabaddi stadium SFX (whistles, tackles, buzzers, gavel).
 * Respects user preference `settings_sfx_enabled` in SharedPreferences and safely no-ops
 * on devices/environments without audio hardware.
 */
object ArenaSoundEngine {
    private fun isSfxEnabled(context: Context): Boolean {
        return try {
            val prefs = context.getSharedPreferences("kabaddi_kings_prefs", Context.MODE_PRIVATE)
            prefs.getBoolean("settings_sfx_enabled", true)
        } catch (_: Throwable) {
            false
        }
    }

    private fun playTone(toneType: Int, durationMs: Int, volume: Int = 70) {
        try {
            val tg = ToneGenerator(AudioManager.STREAM_MUSIC, volume)
            tg.startTone(toneType, durationMs)
            CoroutineScope(Dispatchers.Default).launch {
                delay(durationMs.toLong() + 80L)
                runCatching { tg.release() }
            }
        } catch (_: Throwable) {
            // Safely ignore in headless or test environments
        }
    }

    fun playWhistle(context: Context) {
        if (!isSfxEnabled(context)) return
        playTone(ToneGenerator.TONE_SUP_RINGTONE, 220, 65)
    }

    fun playRaidSuccess(context: Context) {
        if (!isSfxEnabled(context)) return
        playTone(ToneGenerator.TONE_PROP_ACK, 180, 75)
    }

    fun playSuperRaid(context: Context) {
        if (!isSfxEnabled(context)) return
        CoroutineScope(Dispatchers.Default).launch {
            playTone(ToneGenerator.TONE_DTMF_5, 110, 80)
            delay(130L)
            playTone(ToneGenerator.TONE_DTMF_9, 180, 85)
        }
    }

    fun playTackleSlap(context: Context) {
        if (!isSfxEnabled(context)) return
        playTone(ToneGenerator.TONE_PROP_BEEP2, 160, 75)
    }

    fun playBuzzer(context: Context) {
        if (!isSfxEnabled(context)) return
        playTone(ToneGenerator.TONE_SUP_ERROR, 380, 80)
    }

    private fun isBgmEnabled(context: Context): Boolean {
        return try {
            val prefs = context.getSharedPreferences("kabaddi_kings_prefs", Context.MODE_PRIVATE)
            prefs.getBoolean("settings_bgm_enabled", true)
        } catch (_: Throwable) {
            false
        }
    }

    fun playAuctionGavel(context: Context) {
        if (!isSfxEnabled(context)) return
        CoroutineScope(Dispatchers.Default).launch {
            playTone(ToneGenerator.TONE_PROP_BEEP, 90, 80)
            delay(130L)
            playTone(ToneGenerator.TONE_PROP_ACK, 180, 85)
        }
    }

    fun playStadiumAmbienceCue(context: Context) {
        if (!isBgmEnabled(context)) return
        CoroutineScope(Dispatchers.Default).launch {
            playTone(ToneGenerator.TONE_DTMF_1, 80, 28)
            delay(140L)
            playTone(ToneGenerator.TONE_DTMF_5, 110, 32)
        }
    }
}
