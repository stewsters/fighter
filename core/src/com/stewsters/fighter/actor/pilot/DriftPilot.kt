package com.stewsters.fighter.actor.pilot

import com.stewsters.fighter.FighterGame
import com.stewsters.fighter.actor.Actor
import java.util.*

class DriftPilot : PilotBase() {
    init {
        pitchp = Random().nextFloat()
        yawp = Random().nextFloat()
        rollp = Random().nextFloat()
    }

    override fun fly(fighterGame: FighterGame, actor: Actor) {}

}