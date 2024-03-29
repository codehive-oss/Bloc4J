package io.codehive.bloc4j.entitiy

import io.codehive.bloc4j.world.Location

open class Entity(
  var location: Location
) {

  constructor() : this(Location(0.0, 0.0, 0.0))

}
