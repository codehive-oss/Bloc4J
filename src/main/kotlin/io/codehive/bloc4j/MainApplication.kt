package io.codehive.bloc4j

import com.jogamp.newt.event.KeyEvent
import com.jogamp.opengl.GL
import com.jogamp.opengl.GL3
import com.jogamp.opengl.util.GLBuffers
import io.codehive.bloc4j.lib.GraphicsApplication
import io.codehive.bloc4j.lib.ShaderProgram
import org.joml.Vector2i
import java.nio.IntBuffer

class MainApplication : GraphicsApplication {
  private var shouldQuit = false

  private lateinit var vao: IntBuffer
  private lateinit var shaderProgram: ShaderProgram

  private var vertices: FloatArray = floatArrayOf(
    0.5f, -0.5f, 0.0f,
    -0.5f, -0.5f, 0.0f,
    -0.5f, 0.5f, 0.0f,
    0.5f, 0.5f, 0.0f
  )

  private var indices: IntArray = intArrayOf(
    0, 1, 2,
    2, 3, 0
  )

  override fun init(gl: GL3) {
    vao = GLBuffers.newDirectIntBuffer(1)
    gl.glGenVertexArrays(1, vao)
    gl.glBindVertexArray(vao.get(0))

    val ebo = GLBuffers.newDirectIntBuffer(1)
    gl.glGenBuffers(1, ebo)
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo[0])

    val indicesBuffer = GLBuffers.newDirectIntBuffer(indices)
    gl.glBufferData(
      GL.GL_ELEMENT_ARRAY_BUFFER, (indicesBuffer.capacity() * Integer.BYTES).toLong(),
      indicesBuffer, GL.GL_STATIC_DRAW
    )

    val vbo = GLBuffers.newDirectIntBuffer(1)
    gl.glGenBuffers(1, vbo)
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0])

    val floatBuffer = GLBuffers.newDirectFloatBuffer(vertices)
    gl.glBufferData(
      GL.GL_ARRAY_BUFFER, (floatBuffer.capacity() * java.lang.Float.BYTES).toLong(), floatBuffer,
      GL.GL_STATIC_DRAW
    )

    shaderProgram = ShaderProgram(gl, "/shader.vert", "/shader.frag")
    shaderProgram.build()
    shaderProgram.use()

    gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3 * java.lang.Float.BYTES, 0)
    gl.glEnableVertexAttribArray(0)
  }

  override fun display(gl: GL3) {
    gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
    gl.glClear(GL.GL_COLOR_BUFFER_BIT)

    shaderProgram.use()
    gl.glBindVertexArray(vao[0])

    gl.glDrawElements(GL.GL_TRIANGLES, 6, GL.GL_UNSIGNED_INT, 0)
  }

  override fun reshape(gl: GL3, dimensions: Vector2i) {
    gl.glViewport(0, 0, dimensions.x, dimensions.y)
  }

  override fun keyPressed(keyEvent: KeyEvent) {
    if (keyEvent.keyCode == KeyEvent.VK_ESCAPE) {
      shouldQuit = true
    }
  }

  override fun end(gl: GL3) {
    shaderProgram.delete()
    gl.glDeleteVertexArrays(1, vao)
  }

  override fun shouldQuit(): Boolean {
    return shouldQuit
  }
}
