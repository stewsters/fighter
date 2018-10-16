package com.stewsters.fighter

interface Collider {
    fun collision(fighterGame: FighterGame, us: Actor, actor: Actor)
}

class DamageCollider(val damage: Float) : Collider {
    override fun collision(fighterGame: FighterGame, us: Actor, actor: Actor) {
        actor.life?.takeDamage(fighterGame, actor, damage)
    }
}

class DamageAndDisappearCollider(val damage: Float) : Collider {
    override fun collision(fighterGame: FighterGame, us: Actor, actor: Actor) {
        actor.life?.takeDamage(fighterGame, actor, damage)
        fighterGame.removeActors.add(us)
    }
}