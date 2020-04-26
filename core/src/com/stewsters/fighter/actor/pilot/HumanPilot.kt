package com.stewsters.fighter.actor.pilot

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.mappings.Xbox
import com.stewsters.fighter.FighterGame
import com.stewsters.fighter.actor.Actor
import com.stewsters.fighter.math.accel
import com.stewsters.fighter.math.deaden

class HumanPilot(val controller: Controller) : Pilot {
    override fun fly(fighterGame: FighterGame, us: Actor): PilotControl {
        with(us) {
            if (aircraftType == null) {
                return PilotControl()
            }

            return PilotControl(
                    accelp = aircraftType.acceleration * accel(-controller.getAxis(Xbox.L_STICK_VERTICAL_AXIS)),
                    yawp = aircraftType.turnYaw * deaden(controller.getAxis(Xbox.L_STICK_HORIZONTAL_AXIS)),
                    pitchp = aircraftType.turn * deaden(controller.getAxis(Xbox.R_STICK_VERTICAL_AXIS)),
                    rollp = aircraftType.turn * deaden(controller.getAxis(Xbox.R_STICK_HORIZONTAL_AXIS)),
                    primaryWeapon = controller.getButton(Xbox.R_BUMPER),
                    secondaryWeapon = controller.getButton(Xbox.L_BUMPER)
            )

//            if (primaryWeapon != null && controller.getButton(Xbox.R_BUMPER)) {
//                primaryWeapon.fire(fighterGame, this)
//            }
//
//            if (secondaryWeapon != null && controller.getButton(Xbox.L_BUMPER)) {
//                secondaryWeapon.fire(fighterGame, this)
//            }
        }
    }
}
