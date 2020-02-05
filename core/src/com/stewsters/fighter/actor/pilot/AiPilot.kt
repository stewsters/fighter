package com.stewsters.fighter.actor.pilot

import com.badlogic.gdx.math.Vector3
import com.stewsters.fighter.FighterGame
import com.stewsters.fighter.actor.Actor
import com.stewsters.fighter.math.leadTarget
import kotlin.math.max

class AiPilot : PilotBase() {

    var lastTarget: Actor? = null
    var lastTargetTime: Long = 0
    var mode: AiState = AiState.ATTACK

    override fun fly(fighterGame: FighterGame, us: Actor) {

        val aircraftType = us.aircraftType!!

        val time = System.currentTimeMillis()
        val target: Actor?

        // find a target.  close and in front of us are good ideas
        if (lastTargetTime + 1000 < time) {
            target = findInForwardArc(fighterGame, us = us, enemyOnly = true)
            lastTarget = target
            lastTargetTime = time
        } else {
            target = lastTarget
        }


        if (target != null) {

            val dist = us.position.dst2(target.position)

            // If in attack mode and we are too close, change to retreat mode.
            // if we are in retreat mode and too far away, switch to attack mode
            val towards = when (mode) {
                AiState.ATTACK -> {
                    if (dist < 200f) mode = AiState.RETREAT
                    1f
                }
                AiState.RETREAT -> {
                    if (dist > 1200f)
                        mode = AiState.ATTACK

//                    else if (us beingAimedAtBy target)
//                        mode = AiState.EVADE

                    -1f
                }
                AiState.EVADE -> {
//                    if (!us.beingAimedAtBy(target))
//                        mode = AiState.ATTACK
                    0f
                }
            }

            val turn = aircraftType.turn
            val turnYaw = aircraftType.turnYaw

            // else calculate where they will be, and fly towards that location
            val solution = leadTarget(
                    us.position,
                    target.position,
                    Vector3(0f, target.velocity, 0f).mul(target.rotation),
                    max(us.velocity, 3f)
            )

            val unRotatedTargetOffset = solution.path.mul(us.rotation.cpy().conjugate())

            if (unRotatedTargetOffset.z > 0f) {
                pitchp = turn
            } else {
                pitchp = -turn
            }

            if (unRotatedTargetOffset.x > 0f) {
                yawp = turnYaw
                rollp = turn
            } else {
                yawp = -turnYaw
                rollp = -turn
            }

            pitchp *= towards
            yawp *= towards
            rollp *= towards


            // if we are close enough, shoot
            if (unRotatedTargetOffset.y > 0) {

                if (us.secondaryWeapon != null && dist < 3000) {
                    us.secondaryWeapon.fire(fighterGame, us)
                }

                if (dist < 1500 && mode == AiState.ATTACK) {

                    accelp = 0.25f * aircraftType.acceleration
                    if (us.primaryWeapon != null) {
                        us.primaryWeapon.fire(fighterGame, us)
                    }

                } else {
                    accelp = aircraftType.acceleration
                }

            } else {
                if (mode == AiState.ATTACK) {
                    accelp = aircraftType.acceleration
                } else { // retreat
                    accelp = 0.25f * aircraftType.acceleration
                }

            }


        }
        // if we dont have a target, get in formation?
    }

    private fun findInForwardArc(
            fighterGame: FighterGame,
            us: Actor,
            maxDist: Float? = null,
            maxAngle: Float? = null,
            enemyOnly: Boolean? = null
    ): Actor? {

        val seq = fighterGame.actors.asSequence()
                .filter { it != us && it.aircraftType != null }


        if (maxDist != null) {
            seq.filter { it.position.dst2(us.position) < maxDist }
        }

        if (maxAngle != null) {
            seq.filter { us angleTo it < maxAngle }
        }

        if (enemyOnly != null) {
            seq.filter { us.faction.isEnemy(it.faction) }
        }

        return seq.minBy {
            it.position.dst(us.position)
        }


    }

}


enum class AiState {
    ATTACK,
    RETREAT,
    EVADE,
}