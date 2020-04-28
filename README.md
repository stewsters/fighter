# Starfighter

This is a game where you pilot a small ship in a dogfight.  If you plug in xbox360 controllers and run this, you can
have up to 4 players playing.

I did this to work on my 3d math skills, and because I like those 90's style space fighter games.

This is pretty low poly because I am not great at modeling and had limited time.


## How to Run

You are going to want a JDK (8 and 11 are tested) installed.  You will want to plug in 1 to 4 xbox 360 controllers.

Clone the repo and run.

```bash
git clone git@github.com:stewsters/fighter.git
cd fighter
./gradlew run
```


## TODO

### Game

* Turn manipulation - can turn better at lower speeds
* Ammo limitations - cannot keep shooting missiles - they are too good.  Also will need counters
* missile /ship avoidance of asteroids - probably use some flocking stuff here
* squadrons - allies that can fly in formation.  probably need to solidify factions first
* capital ships (flak cannons?, long range torpedoes) 
* alternative environments - asteroid field, open space, ground
* DSL for setting up encounters


### Visual
 
* reticle for aiming?
* minimap - show where opponents are
* tracking system - show where enemies are around you
* explosions
* particles - make things seem more alive?
* actual models - make it look nicer
* planets

## Credits
Audio from https://opengameart.org/content/63-digital-sound-effects-lasers-phasers-space-etc

Skybox https://wwwtyro.github.io/space-3d/