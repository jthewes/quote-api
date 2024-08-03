package de.zedalite.quotes.service;

import de.zedalite.quotes.data.mapper.GroupMapper;
import de.zedalite.quotes.data.model.Group;
import de.zedalite.quotes.data.model.GroupMemberRequest;
import de.zedalite.quotes.data.model.GroupRequest;
import de.zedalite.quotes.data.model.GroupResponse;
import de.zedalite.quotes.data.model.GroupUpdateRequest;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.exception.GroupNotFoundException;
import de.zedalite.quotes.exception.ResourceAlreadyExitsException;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import de.zedalite.quotes.exception.UserNotFoundException;
import de.zedalite.quotes.repository.GroupMemberRepository;
import de.zedalite.quotes.repository.GroupRepository;
import de.zedalite.quotes.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  private static final GroupMapper GROUP_MAPPER = GroupMapper.INSTANCE;

  private final GroupRepository repository;
  private final UserRepository userRepository;
  private final GroupMemberRepository groupMemberRepository;

  public GroupService(
    final GroupRepository repository,
    final UserRepository userRepository,
    final GroupMemberRepository groupMemberRepository
  ) {
    this.repository = repository;
    this.userRepository = userRepository;
    this.groupMemberRepository = groupMemberRepository;
  }

  public GroupResponse create(final GroupRequest request, final Integer creatorId) {
    try {
      final Group group = repository.save(request, creatorId);
      groupMemberRepository.save(group.id(), new GroupMemberRequest(creatorId, null));
      return getResponse(group, getUser(group.creatorId()));
    } catch (final GroupNotFoundException | UserNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public GroupResponse find(final Integer id) {
    try {
      final Group group = repository.findById(id);
      return getResponse(group, getUser(group.creatorId()));
    } catch (final GroupNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public List<GroupResponse> findAllByUser(final Integer userId) {
    try {
      final List<Group> groups = groupMemberRepository.findGroups(userId);
      return getResponses(groups);
    } catch (final GroupNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  private Optional<User> getUser(final Optional<Integer> creatorId) {
    if (creatorId.isEmpty()) {
      return Optional.empty();
    }

    Optional<User> creator;
    try {
      creator = Optional.of(userRepository.findById(creatorId.get()));
    } catch (final UserNotFoundException ex) {
      creator = Optional.empty();
    }
    return creator;
  }

  public GroupResponse join(final String code, final Integer userId) {
    try {
      final Group group = repository.findByCode(code);

      if (groupMemberRepository.isMember(group.id(), userId)) {
        throw new ResourceAlreadyExitsException("User is already a group member");
      }
      groupMemberRepository.save(group.id(), new GroupMemberRequest(userId, null));
      return getResponse(group, getUser(group.creatorId()));
    } catch (final GroupNotFoundException | UserNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public GroupResponse update(final Integer id, final GroupUpdateRequest request) {
    if (request.inviteCode() != null && !request.inviteCode().isBlank()) {
      try {
        repository.findByCode(request.inviteCode());
        throw new ResourceAlreadyExitsException("Invite code already exists");
      } catch (final GroupNotFoundException ex) {
        // do nothing
      }
    }

    try {
      final Group group = repository.findById(id);

      final GroupUpdateRequest updateRequest = new GroupUpdateRequest(
        Objects.requireNonNullElse(request.displayName(), group.displayName()),
        Objects.requireNonNullElse(request.inviteCode(), group.inviteCode())
      ); // only update updated fields

      final Group updatedGroup = repository.update(id, updateRequest);
      return getResponse(updatedGroup, getUser(group.creatorId()));
    } catch (final GroupNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  private GroupResponse getResponse(final Group group, final Optional<User> creator) {
    return GROUP_MAPPER.mapToResponse(group, creator.orElse(null));
  }

  private List<GroupResponse> getResponses(final List<Group> groups) {
    return groups.stream().map(group -> getResponse(group, getUser(group.creatorId()))).toList();
  }
}
