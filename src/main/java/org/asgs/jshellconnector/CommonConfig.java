package org.asgs.jshellconnector;

import java.io.IOException;
import java.util.Properties;

/** Created by asgs on 06-01-2017. */
public class CommonConfig {

  private static Properties config = new Properties();

  static {
    try {
      config.load(
          Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
    } catch (IOException e) {
      // Assuming it's a JDK.
      config.put("JDK_HOME", System.getProperty("java.home"));
      config.put("WS_PORT", 8080);
    }
  }

  public static String getValue(String key) {
    return config.getProperty(key);
  }
}
