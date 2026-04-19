
package com.example.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class PasswordUtils {

  private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

  private PasswordUtils() {
  }

  
  public static String hash(String rawPassword) {
    if (rawPassword == null || rawPassword.isBlank()) {
      throw new IllegalArgumentException("密码不能为空");
    }
    return ENCODER.encode(rawPassword);
  }

  
  public static boolean matches(String rawPassword, String hashedPassword) {
    if (rawPassword == null || hashedPassword == null) {
      return false;
    }
    return ENCODER.matches(rawPassword, hashedPassword);
  }
}
