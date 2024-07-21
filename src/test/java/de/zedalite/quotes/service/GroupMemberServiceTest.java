package de.zedalite.quotes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import de.zedalite.quotes.data.model.Group;
import de.zedalite.quotes.data.model.GroupMember;
import de.zedalite.quotes.data.model.GroupMemberResponse;
import de.zedalite.quotes.data.model.GroupMemberUpdateRequest;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.exception.GroupNotFoundException;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import de.zedalite.quotes.exception.UserNotFoundException;
import de.zedalite.quotes.fixtures.GroupGenerator;
import de.zedalite.quotes.fixtures.GroupMemberGenerator;
import de.zedalite.quotes.fixtures.UserGenerator;
import de.zedalite.quotes.repository.GroupMemberRepository;
import de.zedalite.quotes.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupMemberServiceTest {

  @InjectMocks
  private GroupMemberService instance;

  @Mock
  private GroupMemberRepository repository;

  @Mock
  private UserRepository userRepository;

  @Test
  @DisplayName("Should find group member")
  void shouldFindGroupMember() {
    final GroupMember expectedGroupMember = GroupMemberGenerator.getGroupMember();
    willReturn(expectedGroupMember).given(repository).findById(1, 2);

    final GroupMemberResponse result = instance.find(1, 2);

    then(repository).should().findById(1, 2);
    assertThat(result).isNotNull();
    assertThat(result.displayName()).isEqualTo(expectedGroupMember.displayName());
  }

  @Test
  @DisplayName("Should find group member with no extra displayname")
  void shouldFindGroupMemberWithNoExtraDisplayname() {
    final GroupMember expectedGroupMember = GroupMemberGenerator.getGroupMemberWithoutDisplayName();
    final User expectedUser = UserGenerator.getUser();
    willReturn(expectedGroupMember).given(repository).findById(1, 1);
    willReturn(expectedUser).given(userRepository).findById(1);

    final GroupMemberResponse result = instance.find(1, 1);

    then(repository).should().findById(1, 1);
    assertThat(result).isNotNull();
    assertThat(result.displayName()).isEqualTo(expectedUser.displayName());
  }

  @Test
  @DisplayName("Should not find group member when user non exist")
  void shouldNotFindGroupMemberWhenUserNonExist() {
    willThrow(UserNotFoundException.class).given(repository).findById(1, 2);

    assertThatCode(() -> instance.find(1, 2)).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Should find group members")
  void shouldFindGroupMembers() {
    final List<GroupMember> expectedMembers = GroupMemberGenerator.getGroupMembers();
    final User expectedUser = UserGenerator.getUser();
    willReturn(expectedMembers).given(repository).findMembers(1);
    willReturn(expectedUser).given(userRepository).findById(anyInt());

    final List<GroupMemberResponse> result = instance.findAll(1);

    then(repository).should().findMembers(1);
    assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    assertThat(result.getFirst()).isNotNull();
    assertThat(result.getFirst().displayName()).isEqualTo(expectedMembers.getFirst().displayName());
    assertThat(result.get(1)).isNotNull();
    assertThat(result.get(1).displayName()).isEqualTo("TESTER");
  }

  @Test
  @DisplayName("Should not find group members when user non exist")
  void shouldNotFindGroupMembersWhenUserNonExist() {
    willThrow(UserNotFoundException.class).given(repository).findMembers(1);

    assertThatCode(() -> instance.findAll(1)).isInstanceOf(ResourceNotFoundException.class);
  }

  @ParameterizedTest(name = "User in Group: {0}")
  @ValueSource(booleans = { true, false })
  @DisplayName("Should determine if user is in group")
  void shouldDetermineIfUserIsInGroup(final Boolean isInGroup) {
    willReturn(isInGroup).given(repository).isMember(1, 2);

    final boolean result = instance.isGroupMember(1, 2);

    assertThat(result).isEqualTo(isInGroup);
  }

  @Test
  @DisplayName("Should update group member")
  void shouldUpdateGroupMember() {
    final GroupMember member = mock(GroupMember.class);
    final GroupMember updatedMember = mock(GroupMember.class);
    final GroupMemberUpdateRequest request = mock(GroupMemberUpdateRequest.class);

    given(repository.findById(anyInt(), anyInt())).willReturn(member);
    given(repository.update(anyInt(), anyInt(), any(GroupMemberUpdateRequest.class))).willReturn(updatedMember);
    given(userRepository.findById(anyInt())).willReturn(mock(User.class));
    given(member.displayName()).willReturn("Tester");
    given(updatedMember.displayName()).willReturn("NEW TESTER");
    given(request.displayName()).willReturn("Tester");

    final GroupMemberResponse result = instance.update(member.groupId(), member.userId(), request);

    then(repository).should().findById(member.groupId(), member.userId());
    then(repository).should().update(anyInt(), anyInt(), any(GroupMemberUpdateRequest.class));
    then(userRepository).should().findById(0);
    assertThat(result).isNotNull();
    assertThat(result.displayName()).isEqualTo("NEW TESTER");
  }

  @Test
  @DisplayName("Should update group member when displayName unset")
  void shouldUpdateGroupMemberWhenDisplayNameUnset() {
    final GroupMember member = mock(GroupMember.class);
    final GroupMember updatedMember = mock(GroupMember.class);
    final GroupMemberUpdateRequest request = mock(GroupMemberUpdateRequest.class);

    given(repository.findById(anyInt(), anyInt())).willReturn(member);
    given(repository.update(anyInt(), anyInt(), any(GroupMemberUpdateRequest.class))).willReturn(updatedMember);
    given(userRepository.findById(anyInt())).willReturn(mock(User.class));
    given(member.displayName()).willReturn(null);
    given(updatedMember.displayName()).willReturn("NEW TESTER");
    given(request.displayName()).willReturn(null);

    final GroupMemberResponse result = instance.update(member.groupId(), member.userId(), request);

    then(repository).should().findById(member.groupId(), member.userId());
    then(repository).should().update(anyInt(), anyInt(), any(GroupMemberUpdateRequest.class));
    then(userRepository).should().findById(0);
    assertThat(result).isNotNull();
    assertThat(result.displayName()).isEqualTo("NEW TESTER");
  }

  @Test
  @DisplayName("Should not update group member when group member not found")
  void shouldNotUpdateGroupMemberWhenGroupMemberNotFound() {
    final GroupMember member = mock(GroupMember.class);
    final GroupMemberUpdateRequest request = mock(GroupMemberUpdateRequest.class);

    given(repository.findById(anyInt(), anyInt())).willReturn(member);
    given(member.displayName()).willReturn("TESTER");

    willThrow(new GroupNotFoundException("Group member not found"))
      .given(repository)
      .update(anyInt(), anyInt(), any(GroupMemberUpdateRequest.class));

    assertThatCode(() -> instance.update(0, 0, request)).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Should leave group")
  void shouldLeaveGroup() {
    final Group expectedGroup = GroupGenerator.getGroup();
    final Integer userId = 1;

    willReturn(true).given(repository).isMember(anyInt(), anyInt());

    instance.leave(expectedGroup.id(), userId);

    then(repository).should().delete(expectedGroup.id(), userId);
  }

  @Test
  @DisplayName("Should throw exception when leaving user is not a member")
  void shouldThrowExceptionWhenLeavingUserIsNotAMember() {
    final Integer expectedGroupId = GroupGenerator.getGroup().id();
    final Integer userId = 1;

    willReturn(false).given(repository).isMember(anyInt(), anyInt());

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> instance.leave(expectedGroupId, userId))
      .withMessage("User is not a group member");
  }

  @Test
  @DisplayName("Should throw exception when leaving group is not found")
  void shouldThrowExceptionWhenLeavingGroupIsNotFound() {
    final Integer userId = 1;

    willThrow(new GroupNotFoundException("Group not found")).given(repository).isMember(anyInt(), anyInt());

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> instance.leave(1, userId))
      .withMessage("Group not found");
  }
}
