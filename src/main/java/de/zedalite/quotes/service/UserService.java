package de.zedalite.quotes.service;

import de.zedalite.quotes.data.mapper.UserMapper;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserResponse;
import de.zedalite.quotes.data.model.UserUpdateRequest;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import de.zedalite.quotes.exception.UserNotFoundException;
import de.zedalite.quotes.repository.UserRepository;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

  private final UserRepository repository;

  public UserService(final UserRepository repository) {
    this.repository = repository;
  }

  public UserResponse find(final Integer id) throws ResourceNotFoundException {
    try {
      return getResponse(repository.findById(id));
    } catch (final UserNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public UserResponse update(final Integer id, final UserUpdateRequest request) {
    try {
      final User user = repository.findById(id);

      final UserUpdateRequest updateRequest = new UserUpdateRequest(
        Objects.requireNonNullElse(request.displayName(), user.displayName())
      ); // only update updated fields

      return getResponse(repository.update(id, updateRequest));
    } catch (final UserNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  private UserResponse getResponse(final User user) {
    return USER_MAPPER.mapToResponse(user);
  }
}
