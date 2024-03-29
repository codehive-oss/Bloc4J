package io.codehive.bloc4j.graphics.lib

import com.jogamp.newt.event.*
import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLEventListener
import com.jogamp.opengl.GLProfile
import com.jogamp.opengl.util.Animator
import io.codehive.input.KeyboardInput
import org.joml.Vector2i

object Window : GLEventListener, KeyListener, MouseListener {

  private lateinit var application: GraphicsApplication
  private lateinit var window: GLWindow

  val width
    get() = window.width
  val height
    get() = window.height


  fun init(application: GraphicsApplication, title: String, initialSize: Vector2i) {
    Window.application = application
    val glProfile = GLProfile.get(GLProfile.GL3)
    val glCapabilities = GLCapabilities(glProfile)


    window = GLWindow.create(glCapabilities).apply {
      isUndecorated = false
      isAlwaysOnTop = false
      isFullscreen = false
      isPointerVisible = true
      confinePointer(false)
      this.title = title
      setSize(initialSize.x, initialSize.y)
      isVisible = true
    }

    window.addGLEventListener(this)
    window.addKeyListener(this)
    window.addKeyListener(KeyboardInput)
    window.addMouseListener(this)

    val animator = Animator()
    animator.add(window)
    animator.start()

    window.addWindowListener(object : WindowAdapter() {
      override fun windowDestroyed(e: WindowEvent) {
        Thread {
          //stop the animator thread when user close the window
          animator.stop()
          // This is actually redundant since the JVM will terminate when all threads are closed.
          // It's useful just in case you create a thread and you forget to stop it.
          System.exit(0)
        }.start()
      }
    })
  }

  override fun init(glAutoDrawable: GLAutoDrawable) {
    val gl = glAutoDrawable.gl.gL3
    application.init(gl)
  }

  override fun display(glAutoDrawable: GLAutoDrawable) {
    if (application.shouldQuit()) {
      quit()
      return
    }
    val gl = glAutoDrawable.gl.gL3
    application.display(gl, glAutoDrawable.animator.totalFPSFrames)
  }

  override fun reshape(glAutoDrawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
    val gl = glAutoDrawable.gl.gL3
    application.reshape(gl, Vector2i(width, height))
  }

  override fun keyPressed(keyEvent: KeyEvent) {
    application.keyPressed(keyEvent)
  }

  override fun dispose(glAutoDrawable: GLAutoDrawable) {
    val gl = glAutoDrawable.gl.gL3
    application.end(gl)
  }

  private fun quit() {
    Thread { window.destroy() }.start()
  }

  override fun keyReleased(keyEvent: KeyEvent) {
  }

  override fun mouseClicked(mouseEvent: MouseEvent) {
  }

  override fun mouseEntered(mouseEvent: MouseEvent) {
  }

  override fun mouseExited(mouseEvent: MouseEvent) {
  }

  override fun mousePressed(mouseEvent: MouseEvent) {
  }

  override fun mouseReleased(mouseEvent: MouseEvent) {
  }

  override fun mouseMoved(mouseEvent: MouseEvent) {
  }

  override fun mouseDragged(mouseEvent: MouseEvent) {
  }

  override fun mouseWheelMoved(mouseEvent: MouseEvent) {
  }
}
