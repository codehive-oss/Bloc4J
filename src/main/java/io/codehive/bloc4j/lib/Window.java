package io.codehive.bloc4j.lib;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import io.codehive.bloc4j.math.Vec2i;

public class Window implements GLEventListener, KeyListener, MouseListener {

  private final GraphicsApplication application;
  private final String title;
  private final Vec2i initialSize;

  private GLWindow window;

  public Window(GraphicsApplication application, String title, Vec2i initialSize) {
    this.application = application;
    this.title = title;
    this.initialSize = initialSize;
  }

  public void init() {
    GLProfile glProfile = GLProfile.get(GLProfile.GL3);
    GLCapabilities glCapabilities = new GLCapabilities(glProfile);

    window = GLWindow.create(glCapabilities);

    window.setUndecorated(false);
    window.setAlwaysOnTop(false);
    window.setFullscreen(false);
    window.setPointerVisible(true);
    window.confinePointer(false);
    window.setTitle(title);
    window.setSize(initialSize.x(), initialSize.y());

    window.setVisible(true);

    window.addGLEventListener(this);
    window.addKeyListener(this);
    window.addMouseListener(this);

    Animator animator = new Animator();
    animator.add(window);
    animator.start();

    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowDestroyed(WindowEvent e) {
        new Thread(() -> {

          //stop the animator thread when user close the window
          animator.stop();
          // This is actually redundant since the JVM will terminate when all threads are closed.
          // It's useful just in case you create a thread and you forget to stop it.
          System.exit(0);
        }).start();
      }
    });
  }

  @Override
  public void init(GLAutoDrawable glAutoDrawable) {
    GL3 gl = glAutoDrawable.getGL().getGL3();
    application.init(gl);
  }

  @Override
  public void display(GLAutoDrawable glAutoDrawable) {
    if (application.shouldQuit()) {
      quit();
      return;
    }
    GL3 gl = glAutoDrawable.getGL().getGL3();
    application.display(gl);
  }

  @Override
  public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
    GL3 gl = glAutoDrawable.getGL().getGL3();
    application.reshape(gl, width, height);
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    application.keyPressed(keyEvent);
  }

  @Override
  public void dispose(GLAutoDrawable glAutoDrawable) {
    GL3 gl = glAutoDrawable.getGL().getGL3();
    application.end(gl);
  }

  private void quit() {
    new Thread(() -> window.destroy()).start();
  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {

  }

  @Override
  public void mouseClicked(MouseEvent mouseEvent) {

  }

  @Override
  public void mouseEntered(MouseEvent mouseEvent) {

  }

  @Override
  public void mouseExited(MouseEvent mouseEvent) {

  }

  @Override
  public void mousePressed(MouseEvent mouseEvent) {

  }

  @Override
  public void mouseReleased(MouseEvent mouseEvent) {

  }

  @Override
  public void mouseMoved(MouseEvent mouseEvent) {

  }

  @Override
  public void mouseDragged(MouseEvent mouseEvent) {

  }

  @Override
  public void mouseWheelMoved(MouseEvent mouseEvent) {

  }

}
