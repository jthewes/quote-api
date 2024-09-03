package de.zedalite.quotes.security;

import de.zedalite.quotes.data.mapper.RequestMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLogger implements HandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogger.class);
  private static final String START_TIME = "requestStartTime";
  private static final RequestMapper REQUEST_MAPPER = RequestMapper.INSTANCE;

  @Override
  public boolean preHandle(
    final HttpServletRequest request,
    final @NotNull HttpServletResponse response,
    final @NotNull Object handler
  ) {
    final long startTime = System.currentTimeMillis();
    request.setAttribute(START_TIME, startTime);
    return true;
  }

  @Override
  public void afterCompletion(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final @NotNull Object handler,
    final Exception ex
  ) {
    final long startTime = (Long) request.getAttribute(START_TIME);
    final long endTime = System.currentTimeMillis();
    final long executeTime = endTime - startTime;
    final String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anon";

    final String requestMessage = REQUEST_MAPPER.mapRequest(
      request,
      response.getStatus(),
      username,
      executeTime
    ).toString();
    LOGGER.info(requestMessage);
  }
}
