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

    private fun volume() = (random.nextFloat() * 0.1f) + 0.9f
    private fun pitch() = (random.nextFloat() * 0.2f) + 0.9f
    private fun pan() = (random.nextFloat() * 0.4f) - 0.2f

    fun init() {
        laser = Gdx.audio.newSound(Gdx.files.internal("audio/laser2.mp3"))
        explosion = Gdx.audio.newSound(Gdx.files.internal("audio/explosions/explosion08.wav"))
        launch = Gdx.audio.newSound(Gdx.files.internal("audio/lowDown.mp3"))
        beep = Gdx.audio.newSound(Gdx.files.internal("audio/lowRandom.mp3"))

    }

    fun explode() {
        explosion.play(volume(), pitch(), pan())
    }

    fun laser() {
        laser.play(volume(), pitch(), pan())
    }

    fun launch() {
        launch.play(volume(), pitch(), pan())
    }

    fun beepFail() {
        beep.play(volume(), pitch(), pan())
    }

    fun dispose() {
        laser.dispose()
        explosion.dispose()
        launch.dispose()
        beep.dispose()
    }

}
