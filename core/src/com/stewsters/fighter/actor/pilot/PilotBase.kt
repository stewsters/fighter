package com.stewsters.fighter.actor.pilot

abstract class PilotBase : Pilot {

    /**
     * Pilots can set their flight controls.
     * We may be able to put the code to fire weapons in here
     */
    override fun getPitch(): Float = pitchp
    var pitchp = 0f

    override fun getYaw(): Float = yawp
    var yawp = 0f

    override fun getRoll(): Float = rollp
    var rollp = 0f

    override fun getAccel(): Float = accelp
    var accelp = 0f

}
