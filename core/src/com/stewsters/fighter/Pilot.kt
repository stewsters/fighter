package com.stewsters.fighter

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.mappings.Xbox
import com.badlogic.gdx.math.Vector3
import java.util.*
import kotlin.math.max

private fun deaden(value: Float): Float {
    return value * value * (if (value < 0) -1f else 1f)
}

interface Pilot {
    fun fly(fighterGame: FighterGame, actor: Actor)

    fun getPitch(): Float
    fun getYaw(): Float
    fun getRoll(): Float
    fun getAccel(): Float
}

abstract class PilotBase : Pilot {

    override fun getPitch(): Float = pitchp
    var pitchp = 0f

    override fun getYaw(): Float = yawp
    var yawp = 0f

    override fun getRoll(): Float = rollp
    var rollp = 0f

    override fun getAccel(): Float = accelp
    var accelp = 0f
//
//    fun flyTowards(us: Actor, target: Actor, turn: Float, acceleration: Float) {
//
//
//
//    }
//
//    fun getFromPerspective(){
//
//    }
}

class HumanPilot(val controller: Controller) : PilotBase() {
    override fun fly(fighterGame: FighterGame, us: Actor) {
        with(us) {
            if (aircraftType != null) {

                accelp = aircraftType.acceleration * accel(-controller.getAxis(Xbox.L_STICK_VERTICAL_AXIS))
                yawp = aircraftType.turnYaw * deaden(controller.getAxis(Xbox.L_STICK_HORIZONTAL_AXIS))
                pitchp = aircraftType.turn * deaden(controller.getAxis(Xbox.R_STICK_VERTICAL_AXIS))
                rollp = aircraftType.turn * deaden(controller.getAxis(Xbox.R_STICK_HORIZONTAL_AXIS))

                if (primaryWeapon != null && controller.getButton(Xbox.R_BUMPER)) {
                    primaryWeapon.fire(fighterGame, this)
                }

                if (secondaryWeapon != null && controller.getButton(Xbox.L_BUMPER)) {
                    secondaryWeapon.fire(fighterGame, this)
                }

            }
        }
    }
}

class DriftPilot : PilotBase() {
    init {
        pitchp = Random().nextFloat()
        yawp = Random().nextFloat()
        rollp = Random().nextFloat()
    }

    override fun fly(fighterGame: FighterGame, actor: Actor) {}

}

enum class AiState {
    ATTACK,
    RETREAT
}

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
            target = findInForwardArc(fighterGame, us = us, maxDist = 1000f, maxAngle = 30f, enemyOnly = true)
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
                    if (dist > 1200f) mode = AiState.ATTACK
                    -1f
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
    }

    private fun findInForwardArc(fighterGame: FighterGame, us: Actor, maxDist: Float, maxAngle: Float, enemyOnly: Boolean): Actor? {

        return fighterGame.actors.asSequence()
                .filter { it != us && it.aircraftType != null }
//                .filter {it.position.dst2(us.position) < maxDist }
                .minBy {
                    it.position.dst(us.position)
                }

    }

}


// Steers a missile at an opponent
class MissileGuidance(val target: Actor, val missileType: MissileType) : PilotBase() {

    override fun fly(fighterGame: FighterGame, us: Actor) {
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

        if (unRotatedTargetOffset.z > 0f) {
            pitchp = turn
        } else {
            pitchp = -turn
        }

        if (unRotatedTargetOffset.x > 0f) {
            yawp = turn
        } else {
            yawp = -turn
        }


        if (unRotatedTargetOffset.y > 0) {
            accelp = missileType.acceleration
        } else {
            accelp = 0.25f * missileType.acceleration
        }


        // if we are left, go left, right should go right

    }

}
