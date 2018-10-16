package com.stewsters.fighter

import com.badlogic.gdx.graphics.g3d.Model


enum class AircraftType(
        val acceleration: Float = 1f,

        val turn: Float = 10f,
        val turnYaw: Float = turn,
        var model: Model? = null,
        val life: Float = 1f,
        val radius: Float = 1f
) {
    SWORDFISH(acceleration = 30f, turn = 80f, turnYaw = 60f, life = 30f, radius = 0.8f),
    TILAPIA(acceleration = 28f, turn = 60f, life = 30f, radius = 0.8f); // NPC ships, we want to be better than them
}


enum class BulletType(
        val launchVelocity: Float = 6f,
        val refireMS: Long = 1000,
        var model: Model? = null,
        val expiration: Long = 1500,
        val radius: Float = 0.3f,
        val damage: Float = 2f
        // sound?
        // damage?
) {
    RAILGUN(launchVelocity = 40f, refireMS = 120),
    UGS_8(launchVelocity = 40f, refireMS = 150) // bot weapon, objectively worse
}

enum class MissileType(
        val refireMS: Long,
        val damage: Float,
        val explosionRadius: Float,
        val radius: Float = 0.3f,
        var model: Model? = null,
        val launchVelocity: Float = 5f,
        val acceleration: Float = 20f,
        val turn: Float = 30f,
        val expiration: Long
) {
    VIPER_MK2(
            refireMS = 3000,
            damage = 20f,
            explosionRadius = 5f,
            acceleration = 35f,
            turn = 65f,
            expiration = 10000
    ),
    COBRA(
            refireMS = 10000,
            damage = 8f,
            explosionRadius = 5f,
            acceleration = 15f,
            expiration = 8000
    ), // bot weapon
    ANACONDA(
            refireMS = 20000,
            damage = 100f,
            explosionRadius = 20f,
            radius = 1f,
            acceleration = 15f,
            turn = 15f,
            expiration = 30000
    )

}
