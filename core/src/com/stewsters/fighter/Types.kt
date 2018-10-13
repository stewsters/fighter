package com.stewsters.fighter

import com.badlogic.gdx.graphics.g3d.Model


enum class AircraftType(
        val acceleration: Float = 1f,

        val turn: Float = 10f,
        val turnYaw:Float = turn,
        var model: Model? = null,
        val life: Float = 1f,
        val radius: Float = 1f
) {
    SWORDFISH(acceleration = 30f, turn = 60f,turnYaw = 40f, life = 20f, radius = 0.8f),
    TILAPIA(acceleration = 28f, turn = 45f, life = 5f, radius = 0.8f); // NPC ships, we want to be better than them
}


enum class BulletType(
        val launchVelocity: Float = 6f,
        val refireMS: Long = 1000,
        var model: Model? = null,
        val expiration: Long = 1000,
        val radius: Float = 0.3f
        // sound?
        // damage?
) {
    RAILGUN(launchVelocity = 40f, refireMS = 100),
    UGS_8(launchVelocity = 30f, refireMS = 200) // bot weapon, objectively worse
}

enum class MissileType(
        val refireMS: Long,
        val damage: Float,
        val explosionRadius: Float,
        val radius: Float = 0.3f,
        var model: Model? = null,
        val launchVelocity: Float = 3f,
        val acceleration: Float = 20f,
        val turn: Float = 30f
) {
    VIPER_MK2(5000, 10f, 10f, acceleration = 20f),
    COBRA(10000, 8f, 5f, acceleration = 15f) // bot weapon
}
