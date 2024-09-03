package de.zedalite.quotes.data.mapper;

import java.util.Optional;
import org.mapstruct.Mapper;

@Mapper
public interface OptionalMapper {
  default <T> Optional<T> wrap(T value) {
    return Optional.ofNullable(value);
  }
}
