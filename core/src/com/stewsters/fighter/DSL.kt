package com.stewsters.fighter

// This is a prototype for generating scripted missions
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import java.util.*

enum class Place(
        environmentSetup: () -> Environment,
        val props: (game: FighterGame) -> Unit,
        val ships:(figherGame:FighterGame,controllers: Array<Controller>)->Unit,
        val environment: Environment = environmentSetup()) {

    DEEP_SPACE(
            {
                val environment = Environment()
                environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f))
                environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
                environment
            },
            {

                val size = 3f
                val containerModel =
                    modelBuilder.createBox(size, size, size,
                            Material(ColorAttribute.createDiffuse(Color.GRAY)),
                            attr)

                for (x in 0..5) {
                    for (y in 0..5) {
                        for (z in 0..5) {
                            val x = x.toFloat() * 40f  - (3 * 40)
                            val y = y.toFloat() * 40f  - (3 * 40)
                            val z = z.toFloat() * 40f  - (3 * 40)

                            it.actors.add(Actor(
                                    Vector3(x, y, z),
                                    Quaternion(),
                                    0f,
                                    containerModel,
                                    pilot = DriftPilot(),
                                    radius = size,
                                    collider = DamageCollider(100f)
                            ))
                        }
                    }
                }

            },
            {figherGame:FighterGame, controllers:Array<Controller>->
                // players
                controllers.forEachIndexed { i, controller ->
                    com.badlogic.gdx.Gdx.app.log("Controller Found, Assigning ship", controller.name)
                    val aircraftType = com.stewsters.fighter.AircraftType.SWORDFISH // if(i%2==0)  AircraftType.SWORDFISH else AircraftType.TILAPIA
                    val playerStart = com.stewsters.fighter.PlayerStart.values()[i]
                    val actor = com.stewsters.fighter.Actor(
                            position = playerStart.pos.cpy(),
                            rotation = playerStart.rotation.cpy(),
                            velocity = 300f,
                            model = aircraftType.model,
                            pilot = com.stewsters.fighter.HumanPilot(controller),
                            life = com.stewsters.fighter.Life(aircraftType.life),
                            aircraftType = aircraftType,
                            primaryWeapon = com.stewsters.fighter.Cannon(com.stewsters.fighter.BulletType.RAILGUN),
                            secondaryWeapon = com.stewsters.fighter.MissileRack(com.stewsters.fighter.MissileType.VIPER_MK2),
                            radius = aircraftType.radius,
                            collider = com.stewsters.fighter.DamageCollider(4f),
                            respawnable = true
                    )
                    figherGame.actors.add(actor)
                    figherGame.players.add(actor)
                }

                // add some computers
                for (i in controllers.size until com.stewsters.fighter.PlayerStart.values().size) {
                    val aircraftType = com.stewsters.fighter.AircraftType.TILAPIA
                    val playerStart = com.stewsters.fighter.PlayerStart.values()[i]
                    figherGame.actors.add(
                            com.stewsters.fighter.Actor(
                                    position = playerStart.pos.cpy(),
                                    rotation = playerStart.rotation.cpy(),
                                    velocity = 300f,
                                    model = aircraftType.model,
                                    pilot = com.stewsters.fighter.AiPilot(),
                                    life = com.stewsters.fighter.Life(aircraftType.life),
                                    aircraftType = aircraftType,
                                    primaryWeapon = com.stewsters.fighter.Cannon(com.stewsters.fighter.BulletType.UGS_8),
                                    secondaryWeapon = com.stewsters.fighter.MissileRack(com.stewsters.fighter.MissileType.COBRA),
                                    collider = com.stewsters.fighter.DamageCollider(4f),
                                    respawnable = true
                            )
                    )
                }
            }
    ),
    ASTEROIDS(
            {
                val environment = Environment()
                environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f))
                environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
                environment
            },
            {

                val asteroidModels = kotlin.Array(3) {
                    val size = (it + 1) * 5f
                    modelBuilder.createSphere(size, size, size,
                            5 + it, 5 + it,
                            Material(ColorAttribute.createDiffuse(Color.GRAY)),
                            attr)
                }

                val r = Random()
                for (x in 0..5) {
                    for (y in 0..5) {
                        for (z in 0..5) {
                            val x = x.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40)
                            val y = y.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40)
                            val z = z.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40)

                            val size = r.nextInt(asteroidModels.size)
                            it.actors.add(Actor(
                                    Vector3(x, y, z),
                                    Quaternion(),
                                    0f,
                                    asteroidModels[size],
                                    pilot = DriftPilot(),
                                    radius = (size + 1) * 2.5f,
                                    collider = DamageCollider(100f)
                            ))
                        }
                    }
                }

            },
            {figherGame:FighterGame, controllers:Array<Controller>->}
    ),
    OCEAN(
            {
                val environment = Environment()
                environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f))
                environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
                environment
            },
            {},
            {figherGame:FighterGame, controllers:Array<Controller>->}
    )

}


class Campaign(val missions: List<Mission>)

class Mission(val name: String, val description: String, val place: Place, val flightGroups: List<FlightGroup>)

class FlightGroup(val faction: Faction)


val campaign = Campaign(
        arrayListOf(
                Mission(
                        "Prelude to War",
                        "Diplomacy has broken down and war declared between the Aldroni Federation and the Dominion. Attack and destroy all Dominion colonist supplies",
                        Place.DEEP_SPACE,
                        listOf(
                                FlightGroup(
                                        Faction.ALDRONI
                                ),
                                FlightGroup(
                                        Faction.DOMINION
                                )
                        )

                ),
                Mission(
                        "Retribution",
                        "After the initial strike on their supplies, the Dominion is suspected to attack ",
                        Place.ASTEROIDS,
                        listOf(
                                FlightGroup(
                                        Faction.ALDRONI
                                ),
                                FlightGroup(
                                        Faction.DOMINION
                                )
                        )
                ),
                Mission(
                        "Strike at Kathos",
                        "",
                        Place.OCEAN,
                        listOf(
                                FlightGroup(
                                        Faction.ALDRONI
                                ),
                                FlightGroup(
                                        Faction.DOMINION
                                )
                        )
                )
        )
)


// fighter / interceptor
//
// patrol torpedo boats
// bomber

enum class CapitalShipType {
    CORVETE, // fast, versatile, can operate solo, counter strike fighters
    FRIGATE, // large - groups, complements larger ships.  No strike craft, mostly guns
    DESTROYER, // attacks larger warships, think tank destroyer.  forward arcs.  needs some covering fire
    CRUISER, // versitile, weapons on all sides. can operate solo. A few strike fighters
    BATTLECRUISER, // giant capital ship, variety of weapons
    BATTLESHIP, // mountain of main guns and armor.  Armored, moving fortress
    CARRIER, // carries strike craft, point defense.  Needs support ships
    FLEET_CARRIER // can repair large ships
}