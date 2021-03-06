package com.stewsters.fighter.actor.pilot

import com.badlogic.gdx.math.Vector3
import com.stewsters.fighter.FighterGame
import com.stewsters.fighter.actor.Actor
import com.stewsters.fighter.math.leadTarget
import com.stewsters.fighter.types.MissileType
import kotlin.math.max

// Steers a missile at an opponent
class MissileGuidance(val target: Actor, val missileType: MissileType) : Pilot {

    override fun fly(fighterGame: FighterGame, us: Actor): PilotControl {
        // if we are close enough, detonate
        if (target.position.dst(us.position) < missileType.explosionRadius) {
            fighterGame.actors
                    .filter { it.position.dst(us.position) < missileType.explosionRadius }
                    .forEach { it.life?.takeDamage(fighterGame, it, missileType.damage) }

            // destroy ourselves
            fighterGame.removeActors.add(us)
        }


        val turn = missileType.turn

        // else calculate where they will be, and fly towards that location
        val solution = leadTarget(
                us.position,
                target.position,
                Vector3(0f, target.velocity, 0f).mul(target.rotation),
                max(us.velocity, 5f)
        )

        val unRotatedTargetOffset = solution.path.mul(us.rotation.cpy().conjugate())

        return PilotControl(
                pitchp = if (unRotatedTargetOffset.z > 0f) {
                    turn
                } else {
                    -turn
                },
                yawp = if (unRotatedTargetOffset.x > 0f) {
                    turn
                } else {
                    -turn
                },
                accelp = if (unRotatedTargetOffset.y > 0) {
                    missileType.acceleration
                } else {
                    0.25f * missileType.acceleration
                }
        )


    }

}