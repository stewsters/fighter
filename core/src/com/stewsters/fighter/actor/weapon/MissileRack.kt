package com.stewsters.fighter.actor.weapon

import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.stewsters.fighter.FighterGame
import com.stewsters.fighter.actor.Actor
import com.stewsters.fighter.actor.DamageCollider
import com.stewsters.fighter.actor.Expiration
import com.stewsters.fighter.actor.Life
import com.stewsters.fighter.actor.pilot.MissileGuidance
import com.stewsters.fighter.actor.pilot.beingAimedAtBy
import com.stewsters.fighter.types.MissileType

// Fires homing ordinance
class MissileRack(val missileType: MissileType) : Launcher(
        missileType.refireMS,
        missileType.launchVelocity
) {
    override fun project(fighterGame: FighterGame, shooter: Actor, position: Vector3, rotation: Quaternion, velocity: Float) {

        // find a target
        val target: Actor? = fighterGame.actors.asSequence()
                .filter { it.aircraftType != null && it != shooter }
//                .filter { it.faction.isEnemy(shooter.faction) }
                .filter { it beingAimedAtBy shooter}
                .minBy { shooter.position.dst2(it.position) }

        if (target == null) {
            // fail to fire
            fighterGame.audio.beepFail()
            return
        }
        // fire
        fighterGame.audio.launch()

        fighterGame.newActors.add(Actor(
                position = position,
                rotation = rotation,
                velocity = velocity,
                model = missileType.model,
                pilot = MissileGuidance(target, missileType),
                life = Life(1f),
                expiration = Expiration(missileType.expiration),
                radius = missileType.radius,
                collider = DamageCollider(1f)
        ))

    }
}