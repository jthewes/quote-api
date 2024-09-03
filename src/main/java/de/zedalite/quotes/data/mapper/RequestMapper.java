package de.zedalite.quotes.data.mapper;

import de.zedalite.quotes.data.model.AuthLog;
import de.zedalite.quotes.data.model.RequestLog;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RequestMapper {
  RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

  @Mapping(target = "uri", source = "request.requestURI")
  @Mapping(target = "query", source = "request.queryString", qualifiedByName = "mapQuery")
  @Mapping(target = "client", source = "request", qualifiedByName = "mapClient")
  RequestLog mapRequest(final HttpServletRequest request, final Integer status, final String user, final Long time);

  @Mapping(target = "uri", source = "request.requestURI")
  @Mapping(target = "query", source = "request.queryString", qualifiedByName = "mapQuery")
  @Mapping(target = "client", source = "request", qualifiedByName = "mapClient")
  AuthLog mapAuth(final HttpServletRequest request, final String status, final String user, final String reason);

  @Named("mapClient")
  default String mapClient(final HttpServletRequest request) {
    return Objects.requireNonNullElse(request.getHeader("X-Forwarded-For"), request.getRemoteAddr());
  }

  @Named("mapQuery")
  default String mapQuery(final String query) {
    return (query != null && !query.isBlank()) ? "?" + query : "";
  }
}
