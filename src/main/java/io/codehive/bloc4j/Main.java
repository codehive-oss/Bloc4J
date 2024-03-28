package io.codehive.bloc4j;

import io.codehive.bloc4j.lib.Window;
import io.codehive.bloc4j.math.Vec2i;

public class Main {

  public static void main(String[] args) {
    MainApplication app = new MainApplication();

    Window window = new Window(app, "Bloc4J", new Vec2i(800, 600));

    window.init();
  }

}
