package io.codehive.bloc4j.entity

import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class Camera(
  var target: Entity
) {
  var front: Vector3f = Vector3f(0f, 0f, 1f)

  fun updateFront() {
    val rot = target.rotation
    front = Vector3f(
      (cos(Math.toRadians(rot.yaw.toDouble())) * cos(Math.toRadians(rot.pitch.toDouble()))).toFloat(),
      sin(Math.toRadians(rot.pitch.toDouble())).toFloat(),
      (sin(Math.toRadians(rot.yaw.toDouble())) * cos(Math.toRadians(rot.pitch.toDouble()))).toFloat()
    )
  }
}
