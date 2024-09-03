package de.zedalite.quotes.service;

import de.zedalite.quotes.data.mapper.GroupMemberMapper;
import de.zedalite.quotes.data.model.GroupMember;
import de.zedalite.quotes.data.model.GroupMemberResponse;
import de.zedalite.quotes.data.model.GroupMemberUpdateRequest;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.exception.GroupNotFoundException;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import de.zedalite.quotes.exception.UserNotFoundException;
import de.zedalite.quotes.repository.GroupMemberRepository;
import de.zedalite.quotes.repository.UserRepository;
import de.zedalite.quotes.utils.ObjectUtils;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GroupMemberService {

  private static final GroupMemberMapper GROUP_MEMBER_MAPPER = GroupMemberMapper.INSTANCE;

  private final GroupMemberRepository repository;

  private final UserRepository userRepository;

  public GroupMemberService(final GroupMemberRepository repository, final UserRepository userRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
  }

  public GroupMemberResponse find(final Integer id, final Integer userId) {
    try {
      final GroupMember groupMember = repository.findById(id, userId);
      final User user = userRepository.findById(groupMember.userId());
      if (groupMember.displayName() == null) {
        return GROUP_MEMBER_MAPPER.mapToResponse(user, user.displayName());
      }
      return GROUP_MEMBER_MAPPER.mapToResponse(user, groupMember.displayName());
    } catch (final UserNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public List<GroupMemberResponse> findAll(final Integer id) {
    try {
      return repository
        .findMembers(id)
        .stream()
        .map(member -> {
          final User user = userRepository.findById(member.userId());

          if (member.displayName() == null) {
            return GROUP_MEMBER_MAPPER.mapToResponse(user, user.displayName());
          }
          return GROUP_MEMBER_MAPPER.mapToResponse(user, member.displayName());
        })
        .toList();
    } catch (final UserNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public GroupMemberResponse update(final Integer id, final Integer userId, final GroupMemberUpdateRequest request) {
    try {
      final GroupMember member = repository.findById(id, userId);

      final GroupMemberUpdateRequest updateRequest = new GroupMemberUpdateRequest(
        ObjectUtils.requireNonNullElse(request.displayName(), member.displayName())
      ); // only update updated fields

      final GroupMember updatedMember = repository.update(id, userId, updateRequest);
      return GROUP_MEMBER_MAPPER.mapToResponse(
        userRepository.findById(updatedMember.userId()),
        updatedMember.displayName()
      );
    } catch (final GroupNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public void leave(final Integer id, final Integer userId) {
    try {
      if (!repository.isMember(id, userId)) {
        throw new ResourceNotFoundException("User is not a group member");
      }

      repository.delete(id, userId);
    } catch (final GroupNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public boolean isGroupMember(final Integer id, final Integer userId) {
    return repository.isMember(id, userId);
  }
}
