package io.codehive.bloc4j.lib;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL3;

public interface GraphicsApplication {

  /**
   * This gets executed once after the window and OpenGL have been initialized
   */
  void init(GL3 gl);

  /**
   * Called by in the render loop to update the display
   */
  void display(GL3 gl);

  /**
   * Called when the window size changes
   */
  void reshape(GL3 gl, int width, int height);

  /**
   * Called when a key gets pressed
   */
  void keyPressed(KeyEvent keyEvent);

  /**
   * Called at the end
   */
  void end(GL3 gl);

  /**
   * The Program will exit once this returns true
   */
  boolean shouldQuit();

}
