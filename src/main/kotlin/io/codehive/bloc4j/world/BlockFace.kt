package io.codehive.bloc4j.world

import org.joml.Vector3i

enum class BlockFace(val dir: Vector3i) {
  TOP(Vector3i(0, 1, 0)),
  BOTTOM(Vector3i(0, -1, 0)),
  NORTH(Vector3i(0, 0, -1)),
  SOUTH(Vector3i(0, 0, 1)),
  EAST(Vector3i(1, 0, 0)),
  WEST(Vector3i(-1, 0, 0))
}
