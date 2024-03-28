package io.codehive.bloc4j

import io.codehive.bloc4j.lib.Window
import org.joml.Vector2i

fun main() {
  val application = MainApplication()

  Window.init(application, "Bloc4J", Vector2i(800, 600))
}
