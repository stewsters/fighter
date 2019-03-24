package com.stewsters.fighter.actor

import com.badlogic.gdx.Gdx
import com.stewsters.fighter.FighterGame

class Life(val max: Float, var cur: Float = max) {

    fun takeDamage(fighterGame: FighterGame, actor: Actor, float: Float) {
        Gdx.app.log("Aircraft", "Took $float damage")
        cur -= float

        if (cur <= 0) {
            die(fighterGame, actor)
        }
    }

    fun die(fighterGame: FighterGame, actor: Actor) {
        fighterGame.audio.explode()
        fighterGame.removeActors.add(actor)
        if (actor.respawnable) {
            fighterGame.respawnActors.add(actor)
        }
        Gdx.app.log("Destruction", "Target Exploded")
    }

}