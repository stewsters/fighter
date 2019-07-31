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
import com.stewsters.fighter.actor.Actor
import com.stewsters.fighter.actor.DamageCollider
import com.stewsters.fighter.actor.Faction
import com.stewsters.fighter.actor.Life
import com.stewsters.fighter.actor.pilot.AiPilot
import com.stewsters.fighter.actor.pilot.DriftPilot
import com.stewsters.fighter.actor.pilot.HumanPilot
import com.stewsters.fighter.actor.weapon.Cannon
import com.stewsters.fighter.actor.weapon.MissileRack
import com.stewsters.fighter.types.*
import java.util.*

enum class Place(
        environmentSetup: () -> Environment,
        val props: (game: FighterGame) -> Unit,
        val ships: (fighterGame: FighterGame, controllers: List<Controller>) -> Unit,
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
                            val pos = Vector3(
                                    x.toFloat() * 40f - (3 * 40),
                                    y.toFloat() * 40f - (3 * 40),
                                    z.toFloat() * 40f - (3 * 40)
                            )

                            it.actors.add(Actor(
                                    pos,
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
            { fighterGame: FighterGame, controllers: List<Controller> ->
                // players
                controllers.forEachIndexed { i, controller ->
                    com.badlogic.gdx.Gdx.app.log("Controller Found, Assigning ship", controller.name)
                    val aircraftType = AircraftType.SWORDFISH // if(i%2==0)  AircraftType.SWORDFISH else AircraftType.TILAPIA
                    val playerStart = PlayerStart.values()[i]
                    val actor = Actor(
                            position = playerStart.pos.cpy(),
                            rotation = playerStart.rotation.cpy(),
                            velocity = 300f,
                            model = aircraftType.model,
                            pilot = HumanPilot(controller),
                            life = Life(aircraftType.life),
                            aircraftType = aircraftType,
                            primaryWeapon = Cannon(BulletType.RAILGUN),
                            secondaryWeapon = MissileRack(MissileType.VIPER_MK2),
                            radius = aircraftType.radius,
                            collider = DamageCollider(4f),
                            respawnable = true
                    )
                    fighterGame.actors.add(actor)
                    fighterGame.players.add(actor)
                }

                // add some computers
                for (i in controllers.size until PlayerStart.values().size) {
                    val aircraftType = AircraftType.TILAPIA
                    val playerStart = PlayerStart.values()[i]
                    fighterGame.actors.add(
                            Actor(
                                    position = playerStart.pos.cpy(),
                                    rotation = playerStart.rotation.cpy(),
                                    velocity = 300f,
                                    model = aircraftType.model,
                                    pilot = AiPilot(),
                                    life = Life(aircraftType.life),
                                    aircraftType = aircraftType,
                                    primaryWeapon = Cannon(BulletType.UGS_8),
                                    secondaryWeapon = MissileRack(MissileType.COBRA),
                                    collider = DamageCollider(4f),
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
                            val pos = Vector3(
                                    x.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40),
                                    y.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40),
                                    z.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40)
                            )
                            val size = r.nextInt(asteroidModels.size)
                            it.actors.add(Actor(
                                    pos,
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
            { fighterGame: FighterGame, controllers: List<Controller> ->
                // players
                controllers.forEachIndexed { i, controller ->
                    com.badlogic.gdx.Gdx.app.log("Controller Found, Assigning ship", controller.name)
                    val aircraftType = AircraftType.SWORDFISH // if(i%2==0)  AircraftType.SWORDFISH else AircraftType.TILAPIA
                    val playerStart = PlayerStart.values()[i]
                    val actor = Actor(
                            position = playerStart.pos.cpy(),
                            rotation = playerStart.rotation.cpy(),
                            velocity = 300f,
                            model = aircraftType.model,
                            pilot = HumanPilot(controller),
                            life = Life(aircraftType.life),
                            aircraftType = aircraftType,
                            primaryWeapon = Cannon(BulletType.RAILGUN),
                            secondaryWeapon = MissileRack(MissileType.VIPER_MK2),
                            radius = aircraftType.radius,
                            collider = DamageCollider(4f),
                            respawnable = true
                    )
                    fighterGame.actors.add(actor)
                    fighterGame.players.add(actor)
                }

                // add some computers
                for (i in controllers.size until PlayerStart.values().size) {
                    val aircraftType = AircraftType.SWORDFISH
                    val playerStart = PlayerStart.values()[i]
                    fighterGame.actors.add(
                            Actor(
                                    position = playerStart.pos.cpy(),
                                    rotation = playerStart.rotation.cpy(),
                                    velocity = 300f,
                                    model = aircraftType.model,
                                    pilot = AiPilot(),
                                    life = Life(aircraftType.life),
                                    aircraftType = aircraftType,
                                    primaryWeapon = Cannon(BulletType.UGS_8),
                                    secondaryWeapon = MissileRack(MissileType.COBRA),
                                    collider = DamageCollider(4f),
                                    respawnable = true
                            )
                    )
                }
            }
    ),
    OCEAN(
            {
                val environment = Environment()
                environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f))
                environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
                environment
            },
            {},
            { fighterGame: FighterGame, controllers: List<Controller> ->

            }
    )

}

class Campaign(val missions: List<Mission>)

class Mission(
        val name: String,
        val description: String,
        val place: Place,
        val flightGroups: List<FlightGroup>
)

class FlightGroup(
        val faction: Faction,
        val aircraftType: AircraftType,
        val qty: Int = 1,
        val players: Boolean = false,
        val placement: Placement = Placement.RANDOM
)

// Where entities start
enum class Placement {
    GRID, // grid, centered at 0, 0
    RANDOM,
    X_POS,
    X_NEG
//    Y_POS,
//    Y_NEG,
//    Z_POS,
//    Z_NEG;
}

val campaign = Campaign(
        arrayListOf(
                Mission(
                        "Prelude to War",
                        "Diplomacy has broken down and war declared between the Aldroni Federation and the Dominion." +
                                " Attack and destroy all Dominion colonist supplies",
                        Place.DEEP_SPACE,
                        listOf(
                                FlightGroup(
                                        faction = Faction.ALDRONI,
                                        aircraftType = AircraftType.SWORDFISH,
                                        qty = 4,
                                        players = true,
                                        placement = Placement.X_POS
                                ),
                                FlightGroup(
                                        Faction.DOMINION,
                                        aircraftType = AircraftType.TILAPIA,
                                        qty = 10,
                                        placement = Placement.X_NEG
                                ),
                                FlightGroup(
                                        Faction.DOMINION,
                                        aircraftType = AircraftType.STORAGE,
                                        qty = 20,
                                        placement = Placement.GRID
                                )
                        )

                ),
                Mission(
                        "Retribution",
                        "After the initial strike on their supplies, the Dominion fleet has been detected nearby. " +
                                " Prepare for an attack. ",
                        Place.ASTEROIDS,
                        listOf(
                                FlightGroup(
                                        Faction.ALDRONI,
                                        aircraftType = AircraftType.SWORDFISH,
                                        qty = 4
                                ),
                                FlightGroup(
                                        Faction.DOMINION,
                                        aircraftType = AircraftType.TILAPIA,
                                        qty = 8
                                ),
                                FlightGroup(
                                        Faction.DOMINION,
                                        aircraftType = AircraftType.TILAPIA,
                                        qty = 8
                                )
                        )
                ),
                Mission(
                        "Strike at Kathos",
                        "",
                        Place.OCEAN,
                        listOf(
                                FlightGroup(
                                        Faction.ALDRONI,
                                        aircraftType = AircraftType.SWORDFISH,
                                        qty = 4
                                ),
                                FlightGroup(
                                        Faction.DOMINION,
                                        aircraftType = AircraftType.TILAPIA,
                                        qty = 10
                                )
                        )
                )
        )
)


// fighter / interceptor /
//
// patrol torpedo boats
// bomber

enum class CapitalShipType {
    CORVETTE, // fast, versatile, can operate solo, counter strike fighters
    FRIGATE, // large - groups, complements larger ships.  No strike craft, mostly guns
    DESTROYER, // attacks larger warships, think tank destroyer.  forward arcs.  needs some covering fire
    CRUISER, // versatile, weapons on all sides. can operate solo. A few strike fighters
    BATTLECRUISER, // giant capital ship, variety of weapons
    BATTLESHIP, // mountain of main guns and armor.  Armored, moving fortress
    CARRIER, // carries strike craft, point defense.  Needs support ships
    FLEET_CARRIER // can repair large ships
}