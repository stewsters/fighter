package com.stewsters.fighter

import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight

// This is a prototype for generating scripted missions

enum class Place(
        environmentSetup: () -> Environment,
        val environment: Environment = environmentSetup()
) {

    DEEP_SPACE({
        val environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        environment
    }),
    ASTEROIDS({
        val environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        environment
    }),
    OCEAN({
        val environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        environment
    })
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