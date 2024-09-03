package de.zedalite.quotes.data.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.zedalite.quotes.data.jooq.quotes.tables.records.GroupMembersRecord;
import de.zedalite.quotes.data.model.GroupMember;
import de.zedalite.quotes.data.model.GroupMemberResponse;
import de.zedalite.quotes.data.model.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class GroupMemberMapperTest {

  private static final GroupMemberMapper instance = GroupMemberMapper.INSTANCE;

  @Test
  @DisplayName("Should map groupsRecord to group")
  void shouldMapGroupsRecordToGroup() {
    final GroupMembersRecord groupMembersRecord = new GroupMembersRecord(0, 1, "user");

    final GroupMember groupMember = instance.mapToGroupMember(groupMembersRecord);

    assertThat(groupMember).isNotNull();
    assertThat(groupMember.groupId()).isZero();
    assertThat(groupMember.userId()).isEqualTo(1);
    assertThat(groupMember.displayName()).isEqualTo("user");
  }

  @ParameterizedTest
  @DisplayName("Should map empty groupRecord to null")
  @NullSource
  void shouldMapEmptyGroupRecordToNull(final GroupMembersRecord groupsRecord) {
    final GroupMember groupMember = instance.mapToGroupMember(groupsRecord);

    assertThat(groupMember).isNull();
  }

  @Test
  @DisplayName("Should map groupRecords to groups")
  void shouldMapGroupRecordsToGroups() {
    final GroupMembersRecord groupMembersRecord = new GroupMembersRecord(0, 1, "user");
    final GroupMembersRecord groupMembersRecord2 = new GroupMembersRecord(1, 2, "user2");

    final List<GroupMember> groupMembers = instance.mapToGroupMembers(List.of(groupMembersRecord, groupMembersRecord2));

    assertThat(groupMembers).hasSize(2);
    assertThat(groupMembers.getFirst().groupId()).isZero();
    assertThat(groupMembers.getLast().groupId()).isOne();
  }

  @ParameterizedTest
  @DisplayName("Should map empty groupRecords to null")
  @NullSource
  void shouldMapEmptyGroupRecordsToNull(final List<GroupMembersRecord> groupsRecords) {
    final List<GroupMember> groupMembers = instance.mapToGroupMembers(groupsRecords);

    assertThat(groupMembers).isNull();
  }

  @Test
  @DisplayName("Should map group member and displayname to response")
  void shouldMapGroupMemberAndDisplaynameToResponse() {
    final User user = new User(0, "username", "User", LocalDateTime.MIN);
    final String displayName = "cat";

    final GroupMemberResponse groupMemberResponse = instance.mapToResponse(user, displayName);

    assertThat(groupMemberResponse).isNotNull();
    assertThat(groupMemberResponse.user()).isNotNull();
    assertThat(groupMemberResponse.user().id()).isZero();
    assertThat(groupMemberResponse.displayName()).isEqualTo("cat");
  }

  @ParameterizedTest
  @DisplayName("Should map empty group to null")
  @NullSource
  void shouldMapEmptyGroupMemberToNull(final User user) {
    final GroupMemberResponse result = instance.mapToResponse(user, null);

    assertThat(result).isNull();
  }
}
