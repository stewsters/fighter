package com.stewsters.fighter.actor.pilot

abstract class PilotBase : Pilot {

    override fun getPitch(): Float = pitchp
    var pitchp = 0f

    override fun getYaw(): Float = yawp
    var yawp = 0f

    override fun getRoll(): Float = rollp
    var rollp = 0f

    override fun getAccel(): Float = accelp
    var accelp = 0f

}