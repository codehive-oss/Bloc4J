package io.codehive.bloc4j.entity

import com.bulletphysics.dynamics.RigidBody
import io.codehive.bloc4j.world.Location
import io.codehive.bloc4j.world.Rotation

open class Entity(
  var location: Location,
  var rotation: Rotation,
) {

  var rigidBody: RigidBody? = null

  constructor() : this(Location(0.0f, 10.0f, 0.0f), Rotation(0.0f, 0.0f))

}
