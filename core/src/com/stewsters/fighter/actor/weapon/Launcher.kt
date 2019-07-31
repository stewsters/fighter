package com.stewsters.fighter.actor.weapon

import com.badlogic.gdx.math.Vector3
import com.stewsters.fighter.FighterGame
import com.stewsters.fighter.actor.Actor
import com.stewsters.fighter.types.*

abstract class Launcher(
        val refireMS: Long,
        val launchVelocity: Float,
        val tubeOffsets: List<Vector3> = listOf(
                right.cpy().scl(1f).add(forward),
                left.cpy().scl(1f).add(forward),
                up.cpy().scl(1f).add(forward),
                down.cpy().scl(1f).add(forward)
        )
) : Weapon {

    var lastFired: Long = 0
    var nextTube = 0

    override fun fire(fighterGame: FighterGame, shooter: Actor) {
        val millis = System.currentTimeMillis()
        if (lastFired + refireMS < millis) {

            val offset = tubeOffsets[nextTube++].cpy().mul(shooter.rotation)

            if (nextTube >= tubeOffsets.size)
                nextTube = 0

            project(
                    fighterGame,
                    shooter,
                    shooter.position.cpy().add(offset),
                    shooter.rotation.cpy(),
                    shooter.velocity + launchVelocity
            )

            lastFired = millis
        }
    }

}