package de.zedalite.quotes.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import de.zedalite.quotes.TestEnvironmentProvider;
import de.zedalite.quotes.data.model.Group;
import de.zedalite.quotes.data.model.GroupRequest;
import de.zedalite.quotes.data.model.UserRequest;
import de.zedalite.quotes.exception.GroupNotFoundException;
import de.zedalite.quotes.fixtures.GroupGenerator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(value = "classpath:test-no-cache.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupRepositoryTest extends TestEnvironmentProvider {

  @Autowired
  private GroupRepository instance;

  @Autowired
  private UserRepository userRepository;

  @BeforeAll
  void setup() {
    final Integer userId = userRepository.save(new UserRequest("grouptester", "test", "Group Tester")).id();
    final Integer userId2 = userRepository.save(new UserRequest("grouptester2", "test2", "Group Tester 2")).id();

    instance.save(new GroupRequest("test-group", "TESTGR"), userId);
    instance.save(new GroupRequest("best-quoter", "BstQtr"), userId2);
  }

  @Test
  @DisplayName("Should save group")
  void shouldSaveGroup() {
    final GroupRequest groupRequest = new GroupRequest("test-group", "testcode");

    final Group savedGroup = instance.save(groupRequest, 1);

    assertThat(savedGroup).isNotNull();
    assertThat(savedGroup.id()).isNotNull();
    assertThat(savedGroup.inviteCode()).isEqualTo("testcode");
    assertThat(savedGroup.displayName()).isEqualTo("test-group");
    assertThat(savedGroup.creatorId()).isEqualTo(Optional.of(1));
  }

  @Test
  @DisplayName("Should find group by id")
  void shouldFindGroupById() {
    final Group group = instance.findById(2);

    assertThat(group).isNotNull();
    assertThat(group.id()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should throw exception finding group by non-existing id")
  void shouldThrowExceptionFindingGroupByNonExistingId() {
    assertThatCode(() -> instance.findById(999)).isInstanceOf(GroupNotFoundException.class);
  }

  @Test
  @DisplayName("Should find all groups")
  void shouldFindAllGroups() {
    final List<Group> users = instance.findAll();

    assertThat(users).hasSizeGreaterThanOrEqualTo(2);
  }

  @Test
  @Disabled("implement separate group to be able to test this case")
  @DisplayName("Should throw exception finding all groups")
  void shouldThrowExceptionFindingAllGroups() {
    assertThatCode(() -> instance.findAll()).isInstanceOf(GroupNotFoundException.class);
  }

  @Test
  @DisplayName("Should find all groups ids")
  void shouldFindAllGroupsIds() {
    final List<Integer> ids = instance.findAllIds();

    assertThat(ids).hasSizeGreaterThanOrEqualTo(2);
  }

  @Test
  @Disabled("implement separate group to be able to test this case")
  @DisplayName("Should throw exception finding all group ids")
  void shouldThrowExceptionFindingAllGroupIds() {
    assertThatCode(() -> instance.findAllIds()).isInstanceOf(GroupNotFoundException.class);
  }

  @Test
  @DisplayName("Should find group by code")
  void shouldFindGroupByCode() {
    final Group group = instance.findByCode("TESTGR");

    assertThat(group).isNotNull();
    assertThat(group.inviteCode()).isEqualTo("TESTGR");
  }

  @Test
  @DisplayName("Should throw exception finding group by non-existing code")
  void shouldThrowExceptionFindingGroupByNonExistingCode() {
    assertThatCode(() -> instance.findByCode("NONEXISTING")).isInstanceOf(GroupNotFoundException.class);
  }

  @Test
  @DisplayName("Should update group")
  void shouldUpdateGroup() {
    final Integer groupId = instance.save(new GroupRequest("displayName", "joinMe"), 1).id();
    final GroupRequest groupRequest = new GroupRequest("LALALLA", "LALALLA");

    final Group updatedGroup = instance.update(groupId, groupRequest);

    assertThat(updatedGroup).isNotNull();
    assertThat(updatedGroup.id()).isEqualTo(groupId);
    assertThat(updatedGroup.inviteCode()).isEqualTo("LALALLA");
    assertThat(updatedGroup.displayName()).isEqualTo("LALALLA");
  }

  @Test
  @DisplayName("Should throw exception updating non-existing group")
  void shouldThrowExceptionUpdatingNonExistingGroup() {
    final GroupRequest request = GroupGenerator.getGroupRequest();
    assertThatCode(() -> instance.update(999, request)).isInstanceOf(GroupNotFoundException.class);
  }
}
