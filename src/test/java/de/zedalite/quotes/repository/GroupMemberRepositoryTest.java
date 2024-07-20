package de.zedalite.quotes.repository;

import static org.assertj.core.api.Assertions.assertThat;

import de.zedalite.quotes.TestEnvironmentProvider;
import de.zedalite.quotes.data.model.Group;
import de.zedalite.quotes.data.model.GroupMember;
import de.zedalite.quotes.data.model.GroupMemberRequest;
import de.zedalite.quotes.data.model.GroupMemberUpdateRequest;
import de.zedalite.quotes.data.model.GroupRequest;
import de.zedalite.quotes.data.model.UserRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(value = "classpath:test-no-cache.properties")
class GroupMemberRepositoryTest extends TestEnvironmentProvider {

  @Autowired
  private GroupMemberRepository instance;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private UserRepository userRepository;

  private Integer userId;

  private Integer groupId;

  //TODO refactor Usergenerator with random usersnames
  @BeforeAll
  void setup() {
    userId = userRepository.save(new UserRequest("grouper", "test", "Grouper")).id();
    groupId = groupRepository.save(new GroupRequest("groupers-group", "Groupers"), userId).id();
    final GroupMemberRequest request = new GroupMemberRequest(userId, "GROUPER");
    instance.save(groupId, request);
  }

  @Test
  @DisplayName("Should save group member")
  void shouldSaveGroupMember() {
    final Integer newUserId = userRepository.save(new UserRequest("real operator", "op", "Real Operator")).id();
    final Integer newGroupId = groupRepository.save(new GroupRequest("random-group", "sfsfefs"), userId).id();
    final GroupMemberRequest request = new GroupMemberRequest(newUserId, "REAL OPERATOR");

    final GroupMember result = instance.save(newGroupId, request);

    assertThat(result).isEqualTo(new GroupMember(newGroupId, newUserId, "REAL OPERATOR"));
  }

  @Test
  @DisplayName("Should find all group members")
  void shouldFindAllGroupMembers() {
    final List<GroupMember> groupMembers = instance.findMembers(groupId);

    assertThat(groupMembers).hasSizeGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Should find group member by id")
  void shouldFindGroupMemberById() {
    final GroupMember groupMember = instance.findById(groupId, userId);

    assertThat(groupMember).isNotNull();
    assertThat(groupMember.groupId()).isEqualTo(groupId);
    assertThat(groupMember.userId()).isEqualTo(userId);
    assertThat(groupMember.displayName()).isEqualTo("GROUPER");
  }

  @Test
  @DisplayName("Should find all groups by user id")
  void shouldFindAllGroupsByUserId() {
    final List<Group> groups = instance.findGroups(userId);

    assertThat(groups).hasSizeGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Should update group member")
  void shouldUpdateGroupMember() {
    final Integer newUserId = userRepository.save(new UserRequest("update me", "update@email.me", "me")).id();
    final GroupMemberRequest memberRequest = new GroupMemberRequest(newUserId, "UPDATE me");
    instance.save(groupId, memberRequest);

    final GroupMemberUpdateRequest request = new GroupMemberUpdateRequest("UPDATED");

    final GroupMember result = instance.update(groupId, userId, request);

    assertThat(result.displayName()).isEqualTo("UPDATED");
  }

  @Test
  @DisplayName("Should delete group member")
  void shouldDeleteGroupMember() {
    final Integer newUserId = userRepository.save(new UserRequest("operator", "op", "Operator")).id();
    final Integer newGroupId = groupRepository.save(new GroupRequest("new-group", "NewGr"), userId).id();
    final GroupMemberRequest request = new GroupMemberRequest(newUserId, "OPERATOR");
    instance.save(newGroupId, request);

    final boolean isInGroup = instance.isMember(newGroupId, newUserId);
    assertThat(isInGroup).isTrue();

    instance.delete(newGroupId, newUserId);

    final boolean isInGroupAfterDelete = instance.isMember(newGroupId, newUserId);
    assertThat(isInGroupAfterDelete).isFalse();
  }

  @Test
  @DisplayName("Should return true when user is in group")
  void shouldReturnTrueWhenUserIsInGroup() {
    final boolean isInGroup = instance.isMember(groupId, userId);

    assertThat(isInGroup).isTrue();
  }

  @Test
  @DisplayName("Should return false when user is not in group")
  void shouldReturnFalseWhenUserIsNotInGroup() {
    final boolean isInGroup = instance.isMember(9876, 345);

    assertThat(isInGroup).isFalse();
  }
}
