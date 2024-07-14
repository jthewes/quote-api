package de.zedalite.quotes.service;

import de.zedalite.quotes.data.mapper.GroupUserMapper;
import de.zedalite.quotes.data.model.GroupUser;
import de.zedalite.quotes.data.model.GroupUserRequest;
import de.zedalite.quotes.data.model.GroupUserResponse;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.exception.GroupNotFoundException;
import de.zedalite.quotes.exception.ResourceAlreadyExitsException;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import de.zedalite.quotes.exception.UserNotFoundException;
import de.zedalite.quotes.repository.GroupUserRepository;
import de.zedalite.quotes.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GroupUserService {

  private static final GroupUserMapper GROUP_USER_MAPPER = GroupUserMapper.INSTANCE;

  private static final String GROUP_USER_ALREADY_EXITS = "Group user already exits";

  private final GroupUserRepository repository;

  private final UserRepository userRepository;

  public GroupUserService(final GroupUserRepository repository, final UserRepository userRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
  }

  public GroupUserResponse create(final Integer id, final GroupUserRequest request) {
    if (userRepository.doesUserNonExist(request.userId())) throw new ResourceNotFoundException("USER_NOT_FOUND");

    // TODO add push notifcation: send to specific group topic or user notification token
    try {
      if (repository.isUserInGroup(id, request.userId())) {
        throw new ResourceAlreadyExitsException(GROUP_USER_ALREADY_EXITS);
      } else {
        final GroupUser groupUser = repository.save(id, request);
        final User user = userRepository.findById(groupUser.userId());
        return GROUP_USER_MAPPER.mapToResponse(user, groupUser.userDisplayName());
      }
    } catch (final UserNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public GroupUserResponse find(final Integer id, final Integer userId) {
    try {
      final GroupUser groupUser = repository.findById(id, userId);
      final User user = userRepository.findById(groupUser.userId());
      if (groupUser.userDisplayName() == null) {
        return GROUP_USER_MAPPER.mapToResponse(user, user.displayName());
      }
      return GROUP_USER_MAPPER.mapToResponse(user, groupUser.userDisplayName());
    } catch (final UserNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public List<GroupUserResponse> findAll(final Integer id) {
    try {
      return repository
        .findUsers(id)
        .stream()
        .map(groupUser -> {
          final User user = userRepository.findById(groupUser.userId());

          if (groupUser.userDisplayName() == null) {
            return GROUP_USER_MAPPER.mapToResponse(user, user.displayName());
          }
          return GROUP_USER_MAPPER.mapToResponse(user, groupUser.userDisplayName());
        })
        .toList();
    } catch (final UserNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public void leave(final Integer id, final Integer userId) {
    try {
      if (!repository.isUserInGroup(id, userId)) {
        throw new ResourceNotFoundException("User is not a group member");
      }

      repository.delete(id, userId);
    } catch (final GroupNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public boolean isUserInGroup(final Integer id, final Integer userId) {
    return repository.isUserInGroup(id, userId);
  }
}
