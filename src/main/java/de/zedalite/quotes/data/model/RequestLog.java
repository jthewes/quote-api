package de.zedalite.quotes.data.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestLog(
  @NotBlank String method,

  @NotBlank String uri,

  @NotBlank String query,

  @NotNull Integer status,

  @NotBlank String client,

  @NotBlank String user,

  @NotNull Long time
) {
  @Override
  public String toString() {
    return String.format(
      "request [%s %s%s, status=%s, client=%s, user=%s, duration=%sms]",
      method,
      uri,
      query,
      status,
      client,
      user,
      time
    );
  }
}
