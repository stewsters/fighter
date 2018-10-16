package com.stewsters.fighter

import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3


interface Weapon {
    fun fire(fighterGame: FighterGame, shooter: Actor)
    fun project(fighterGame: FighterGame, shooter: Actor, position: Vector3, facing: Quaternion, velocity: Float)
}

abstract class Launcher(val refireMS: Long, val launchVelocity: Float) : Weapon {
    var lastFired: Long = 0
    var rightNext = true

    override fun fire(fighterGame: FighterGame, shooter: Actor) {
        val millis = System.currentTimeMillis()
        if (lastFired + refireMS < millis) {

            val offset: Vector3
            offset = if (rightNext) {
                Actor.right.cpy().scl(0.5f).add(Actor.forward).mul(shooter.facing)
            } else {
                Actor.left.cpy().scl(0.5f).add(Actor.forward).mul(shooter.facing)
            }

            rightNext = !rightNext

            project(
                    fighterGame,
                    shooter,
                    shooter.pos.cpy().add(offset),
                    shooter.facing.cpy(),
                    shooter.velocity + launchVelocity
            )

            lastFired = millis
        }
    }

}

// Fires projectiles
class Cannon(val bulletType: BulletType) : Launcher(bulletType.refireMS, bulletType.launchVelocity) {

    override fun project(fighterGame: FighterGame, shooter: Actor, position: Vector3, facing: Quaternion, velocity: Float) {

        // I think we will need a sound player class, that detects the distance to each and scales the audio
        fighterGame.audio.laser()
        fighterGame.newActors.add(Actor(
                position,
                facing,
                velocity,
                bulletType.model!!,
                expiration = Expiration(bulletType.expiration),
                radius = bulletType.radius,
                collider = DamageAndDisappearCollider(bulletType.damage)
        ))
    }
}

// Fires homing ordinance
class MissileRack(val missileType: MissileType) : Launcher(
        missileType.refireMS,
        missileType.launchVelocity
) {
    override fun project(fighterGame: FighterGame, shooter: Actor, position: Vector3, facing: Quaternion, velocity: Float) {

        // find a target
        val target: Actor? = fighterGame.actors.asSequence()
//                .filter { it.faction.isEnemy(shooter.faction) }
                .filter { it.aircraftType != null && it != shooter }
                .minBy { shooter.pos.dst2(it.pos) }

        if (target == null) {
            // fail to fire
            fighterGame.audio.beepFail()
            return
        }
        // fire
        fighterGame.audio.launch()

        fighterGame.newActors.add(Actor(
                pos = position,
                facing = facing,
                velocity = velocity,
                model = missileType.model!!,
                pilot = MissileGuidance(target, missileType),
                life = Life(1f),
                expiration = Expiration(missileType.expiration),
                radius = missileType.radius,
                collider = DamageCollider(1f)
        ))

    }
}
