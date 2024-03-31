package io.codehive.bloc4j.graphics.lib

import com.jogamp.newt.event.KeyEvent
import com.jogamp.newt.event.MouseEvent
import com.jogamp.opengl.GL3
import org.joml.Vector2i

interface GraphicsApplication {
  fun init(gl: GL3)

  fun display(gl: GL3, frame: Int)

  fun reshape(gl: GL3, dimensions: Vector2i)

  fun keyPressed(keyEvent: KeyEvent)

  fun mouseMoved(mouseEvent: MouseEvent)

  fun end(gl: GL3)

  fun shouldQuit(): Boolean
}
