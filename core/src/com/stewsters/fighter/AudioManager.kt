package com.stewsters.fighter

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import java.util.*

class AudioManager(val fighterGame: FighterGame) {

    val random = Random()

    lateinit var laser: Sound
    lateinit var explosion: Sound

    lateinit var launch: Sound
    lateinit var beep: Sound

    // Loading assets

    fun variation() = (random.nextFloat() * 0.2f) + 0.9f


    fun init() {
        laser = Gdx.audio.newSound(Gdx.files.internal("audio/laser2.mp3"))
        explosion = Gdx.audio.newSound(Gdx.files.internal("audio/explosions/explosion08.wav"))
        launch = Gdx.audio.newSound(Gdx.files.internal("audio/lowDown.mp3"))
        beep = Gdx.audio.newSound(Gdx.files.internal("audio/lowRandom.mp3"))

    }

    fun explode() {
        explosion.play(variation(), variation(), variation())
    }

    fun laser() {
        laser.play(variation(), variation(), variation())
    }

    fun launch() {
        launch.play(variation(), variation(), variation())
    }

    fun beepFail() {
        beep.play(variation(), variation(), variation())
    }

    fun dispose() {
        laser.dispose()
        explosion.dispose()
        launch.dispose()
        beep.dispose()
    }

}
