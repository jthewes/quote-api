package de.zedalite.quotes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyInt;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;

import de.zedalite.quotes.data.model.Group;
import de.zedalite.quotes.data.model.GroupDisplayNameRequest;
import de.zedalite.quotes.data.model.GroupMemberRequest;
import de.zedalite.quotes.data.model.GroupRequest;
import de.zedalite.quotes.data.model.GroupResponse;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.exception.GroupNotFoundException;
import de.zedalite.quotes.exception.ResourceAlreadyExitsException;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import de.zedalite.quotes.exception.UserNotFoundException;
import de.zedalite.quotes.fixtures.GroupGenerator;
import de.zedalite.quotes.fixtures.UserGenerator;
import de.zedalite.quotes.repository.GroupMemberRepository;
import de.zedalite.quotes.repository.GroupRepository;
import de.zedalite.quotes.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

  @InjectMocks
  private GroupService instance;

  @Mock
  private GroupRepository groupRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private GroupMemberRepository groupMemberRepository;

  @Test
  @DisplayName("Should create group with creator")
  void shouldCreateGroupWithCreator() {
    final GroupRequest groupRequest = GroupGenerator.getGroupRequest();
    final Group expectedGroup = GroupGenerator.getGroup();
    final User expectedUser = UserGenerator.getUser();
    willReturn(expectedGroup).given(groupRepository).save(any(GroupRequest.class), anyInt());
    willReturn(expectedUser).given(userRepository).findById(anyInt());

    final GroupResponse result = instance.create(groupRequest, 1);

    then(groupRepository).should().save(groupRequest, 1);
    then(groupMemberRepository).should().save(expectedGroup.id(), new GroupMemberRequest(1, null));
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Should throw exception when group not created")
  void shouldThrowExceptionWhenGroupNotCreated() {
    final GroupRequest groupRequest = GroupGenerator.getGroupRequest();
    willThrow(GroupNotFoundException.class).given(groupRepository).save(any(GroupRequest.class), anyInt());

    assertThatCode(() -> instance.create(groupRequest, 1)).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Should throw exception when group created but user not joined")
  void shouldThrowExceptionWhenGroupCreatedButUserNotJoined() {
    final GroupRequest groupRequest = GroupGenerator.getGroupRequest();
    final Group expectedGroup = GroupGenerator.getGroup();
    willReturn(expectedGroup).given(groupRepository).save(any(GroupRequest.class), anyInt());
    willThrow(UserNotFoundException.class).given(groupMemberRepository).save(anyInt(), any(GroupMemberRequest.class));

    assertThatCode(() -> instance.create(groupRequest, 1)).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Should find group by id")
  void shouldFindGroupById() {
    final Group expectedGroup = GroupGenerator.getGroup();
    final User expectedUser = UserGenerator.getUser();
    willReturn(expectedGroup).given(groupRepository).findById(anyInt());
    willReturn(expectedUser).given(userRepository).findById(anyInt());

    final GroupResponse group = instance.find(1);

    assertThat(group.group().id()).isEqualTo(expectedGroup.id());
    assertThat(group.creator()).isPresent();
  }

  @Test
  @DisplayName("Should find group by id without creator")
  void shouldFindGroupByIdWithoutCreator() {
    final Group expectedGroup = GroupGenerator.getGroupNoCreator();
    willReturn(expectedGroup).given(groupRepository).findById(anyInt());

    final GroupResponse group = instance.find(1);

    assertThat(group.group().id()).isEqualTo(expectedGroup.id());
    assertThat(group.creator()).isEmpty();
  }

  @Test
  @DisplayName("Should still find group by id when creator throws exception")
  void shouldFindGroupByIdWhenCreatorThrowsException() {
    final Group expectedGroup = GroupGenerator.getGroup();
    willReturn(expectedGroup).given(groupRepository).findById(anyInt());
    willThrow(UserNotFoundException.class).given(userRepository).findById(anyInt());

    final GroupResponse group = instance.find(1);

    assertThat(group.group().id()).isEqualTo(expectedGroup.id());
    assertThat(group.creator()).isEmpty();
  }

  @Test
  @DisplayName("Should throw exception when group not found")
  void shouldThrowExceptionWhenGroupNotFound() {
    willThrow(GroupNotFoundException.class).given(groupRepository).findById(anyInt());

    assertThatCode(() -> instance.find(1)).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Should find all groups by user")
  void shouldFindAllGroupsByUser() {
    final List<Group> expectedGroups = GroupGenerator.getGroups();
    final User expectedUser = UserGenerator.getUser();
    willReturn(expectedGroups).given(groupMemberRepository).findGroups(anyInt());
    willReturn(expectedUser).given(userRepository).findById(anyInt());

    final List<GroupResponse> result = instance.findAllByUser(1);

    then(groupMemberRepository).should().findGroups(1);
    assertThat(result).hasSizeGreaterThanOrEqualTo(1);
    assertThat(result.getFirst().group().id()).isEqualTo(expectedGroups.getFirst().id());
    assertThat(result.getFirst().creator()).isPresent();
  }

  @Test
  @DisplayName("Should throw exception when user has no groups")
  void shouldThrowExceptionWhenUserHasNoGroups() {
    willThrow(GroupNotFoundException.class).given(groupMemberRepository).findGroups(anyInt());

    assertThatCode(() -> instance.findAllByUser(1)).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Should find all group ids")
  void shouldFindAllGroupIds() {
    final List<Integer> expectedIds = GroupGenerator.getGroups().stream().map(Group::id).toList();
    willReturn(expectedIds).given(groupRepository).findAllIds();

    final List<Integer> result = instance.findAllIds();

    then(groupRepository).should().findAllIds();
    assertThat(result).hasSizeGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Should throw exception when group ids not found")
  void shouldThrowExceptionWhenGroupIdsNotFound() {
    willThrow(GroupNotFoundException.class).given(groupRepository).findAllIds();

    assertThatCode(() -> instance.findAllIds()).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Should allow user to join group")
  void shouldAllowUserToJoinGroup() {
    final Group expectedGroup = GroupGenerator.getGroup();
    final User expectedUser = UserGenerator.getUser();
    final String code = "testCode";
    final Integer userId = 1;

    willReturn(expectedGroup).given(groupRepository).findByCode(anyString());
    willReturn(false).given(groupMemberRepository).isMember(anyInt(), anyInt());
    willReturn(expectedUser).given(userRepository).findById(anyInt());

    final GroupResponse result = instance.join(code, userId);

    then(groupRepository).should().findByCode(code);
    then(groupMemberRepository).should().save(expectedGroup.id(), new GroupMemberRequest(userId, null));
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Should throw exception when user already in group")
  void shouldThrowExceptionWhenUserAlreadyInGroup() {
    final Group expectedGroup = GroupGenerator.getGroup();
    final String code = "testCode";
    final Integer userId = 1;

    willReturn(expectedGroup).given(groupRepository).findByCode(anyString());
    willReturn(true).given(groupMemberRepository).isMember(anyInt(), anyInt());

    assertThatExceptionOfType(ResourceAlreadyExitsException.class)
      .isThrownBy(() -> instance.join(code, userId))
      .withMessage("User is already a group member");
  }

  @Test
  @DisplayName("Should throw exception when group does not exist")
  void shouldThrowExceptionWhenGroupDoesNotExist() {
    final String code = "nonExistentCode";
    final Integer userId = 1;

    willThrow(new GroupNotFoundException("Group not found")).given(groupRepository).findByCode(anyString());

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> instance.join(code, userId))
      .withMessage("Group not found");
  }

  @Test
  @DisplayName("Should update group when group exists")
  void shouldUpdateGroupWhenGroupExists() {
    final Group expectedGroup = GroupGenerator.getGroup();
    final GroupRequest groupRequest = GroupGenerator.getGroupRequest();
    willReturn(expectedGroup).given(groupRepository).update(anyInt(), any(GroupRequest.class));

    final Group result = instance.update(1, groupRequest);

    then(groupRepository).should().update(1, groupRequest);
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Should throw exception when updating non existing group")
  void shouldThrowExceptionWhenUpdatingNonExistingGroup() {
    final GroupRequest groupRequest = GroupGenerator.getGroupRequest();
    willThrow(GroupNotFoundException.class).given(groupRepository).update(anyInt(), any(GroupRequest.class));

    assertThatCode(() -> instance.update(1, groupRequest)).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Should update displayname when group exists")
  void shouldUpdateDisplaynameWhenGroupExists() {
    final Group expectedGroup = GroupGenerator.getGroup();
    final User expectedUser = UserGenerator.getUser();
    willReturn(expectedGroup).given(groupRepository).findById(anyInt());
    willReturn(expectedGroup).given(groupRepository).update(anyInt(), any(GroupRequest.class));
    willReturn(expectedUser).given(userRepository).findById(anyInt());

    final GroupResponse result = instance.updateDisplayName(1, new GroupDisplayNameRequest("newName"));

    then(groupRepository).should().findById(1);

    then(groupRepository).should().update(1, new GroupRequest("newName", expectedGroup.inviteCode()));
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Should throw exception when updating displayname of non existing group")
  void shouldThrowExceptionWhenUpdatingDisplaynameOfNonExistingGroup() {
    willThrow(GroupNotFoundException.class).given(groupRepository).findById(anyInt());

    final GroupDisplayNameRequest groupDisplayNameRequest = new GroupDisplayNameRequest("newName");

    assertThatCode(() -> instance.updateDisplayName(1, groupDisplayNameRequest)).isInstanceOf(
      ResourceNotFoundException.class
    );
  }
}
