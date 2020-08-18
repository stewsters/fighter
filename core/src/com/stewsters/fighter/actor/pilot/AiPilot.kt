package com.stewsters.fighter.actor.pilot

import com.badlogic.gdx.math.Vector3
import com.stewsters.fighter.FighterGame
import com.stewsters.fighter.actor.Actor
import com.stewsters.fighter.math.leadTarget
import kotlin.math.max

class AiPilot : Pilot {

    var lastTarget: Actor? = null
    var lastTargetTime: Long = 0
    var mode: AiState = AiState.ATTACK

    override fun fly(fighterGame: FighterGame, us: Actor): PilotControl {

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


        if (target == null) {
            // if we dont have a target, get in formation?
            return PilotControl()
        }

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
                max(us.velocity, us.primaryWeapon?.getVelocity()?:3f)
        )

        val unRotatedTargetOffset = solution.path.mul(us.rotation.cpy().conjugate())

        var pitchp = 0f
        var yawp = 0f
        var rollp = 0f


        if (unRotatedTargetOffset.z > 0f) {
            pitchp = turn * towards
        } else {
            pitchp = -turn * towards
        }

        if (unRotatedTargetOffset.x > 0f) {
            yawp = turnYaw * towards
            rollp = turn * towards
        } else {
            yawp = -turnYaw * towards
            rollp = -turn * towards
        }

//        pitchp *= towards
//        yawp *= towards
//        rollp *= towards

        var primary = false
        var secondary = false
        var accelp = 0f
        if (unRotatedTargetOffset.y > 0) { // THey are in front of  us

            // if we are close enough, shoot
            if (dist < 3000) {
                secondary = true
            }

            if (dist < 1500 && mode == AiState.ATTACK) {

                accelp = 0.25f * aircraftType.acceleration
                primary = true

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

        return PilotControl(
                pitchp = pitchp,
                yawp = yawp,
                rollp = rollp,
                accelp = accelp,
                primaryWeapon = primary,
                secondaryWeapon = secondary
        )


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

        return seq.minByOrNull {
            it.position.dst(us.position)
        }


    }

}


enum class AiState {
    ATTACK,
    RETREAT,
    EVADE,
}