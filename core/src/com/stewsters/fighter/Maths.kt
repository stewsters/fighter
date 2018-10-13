package com.stewsters.fighter

import com.badlogic.gdx.math.Vector3
import kotlin.math.pow
import kotlin.math.sqrt

// https://gamedev.stackexchange.com/questions/35859/algorithm-to-shoot-at-a-target-in-a-3d-game
fun leadTarget(src: Vector3, targetPos: Vector3, targetVel: Vector3, projectileVel: Float): FiringSolution {

    // vector to the target
    val toTarget = targetPos.cpy().sub(src)

    val a = targetVel.dot(targetVel) - projectileVel * projectileVel
    val b = 2 * targetVel.dot(toTarget)
    val c = toTarget.dot(toTarget)

    val p = -b / (2 * a)
    val q = sqrt(b * b - 4f * a * c) / (2 * a)

    val t1 = p - q
    val t2 = p + q
    val t: Float

    if (t1 > t2 && t2 > 0) {
        t = t2
    } else {
        t = t1
    }

    val aimSpot = targetPos.cpy().mulAdd(targetVel, t)
    val bulletPath = aimSpot.cpy().sub(src)
    val timeToImpact = bulletPath.len() / projectileVel //speed must be in units per second

    return FiringSolution(aimSpot, bulletPath, timeToImpact)
}

data class FiringSolution(
        val aimSpot: Vector3, // This is where we need to aim to hit them
        val path: Vector3,    // this is the offset to the aimspot
        val timeToImpact: Float // estimated time to impact
)

fun accel(x: Float) = (0.3f * (x + 1f).pow(2f)) - 0.2f

//
fun main(args: Array<String>) {

    println(accel(-1f))
    println(accel(1f))

//    leadTarget(
//            src = Vector3(0f, 0f, 0f),
//            targetPos = Vector3(10f, 10f, 10f),
//            targetVel = Vector3(1f, 0f, 0f),
//            projectileVel = 10f
//    )
}