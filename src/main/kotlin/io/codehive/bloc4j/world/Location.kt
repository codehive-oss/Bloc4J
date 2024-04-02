package io.codehive.bloc4j.world

import org.joml.Vector3f
import org.joml.Vector3i

class Location(
  var x: Float,
  var y: Float,
  var z: Float,
) {
  fun toVec3f(): Vector3f {
    return Vector3f(x, y, z)
  }

  fun toVec3i(): Vector3i {
    return Vector3i(x.toInt(), y.toInt(), z.toInt())
  }

  fun add(vector: Vector3f) {
    x += vector.x
    y += vector.y
    z += vector.z
  }

}

