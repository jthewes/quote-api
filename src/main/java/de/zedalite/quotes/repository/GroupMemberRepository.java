package de.zedalite.quotes.repository;

import de.zedalite.quotes.data.jooq.quotes.tables.GroupMembers;
import de.zedalite.quotes.data.jooq.quotes.tables.Groups;
import de.zedalite.quotes.data.jooq.quotes.tables.records.GroupMembersRecord;
import de.zedalite.quotes.data.mapper.GroupMemberMapper;
import de.zedalite.quotes.data.model.Group;
import de.zedalite.quotes.data.model.GroupMember;
import de.zedalite.quotes.data.model.GroupMemberRequest;
import de.zedalite.quotes.exception.GroupNotFoundException;
import de.zedalite.quotes.exception.UserNotFoundException;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class GroupMemberRepository {

  private static final GroupMemberMapper GROUP_USER_MAPPER = GroupMemberMapper.INSTANCE;
  private static final Groups GROUPS = Groups.GROUPS.as("groups");
  private static final GroupMembers GROUP_USERS = GroupMembers.GROUP_MEMBERS.as("group_users");
  private static final String GROUP_USER_NOT_FOUND = "Group member not found";

  private final DSLContext dsl;

  public GroupMemberRepository(final DSLContext dsl) {
    this.dsl = dsl;
  }

  public GroupMember save(final Integer id, final GroupMemberRequest request) {
    final Optional<GroupMembersRecord> savedGroupMember = dsl
      .insertInto(GROUP_USERS)
      .set(GROUP_USERS.GROUP_ID, id)
      .set(GROUP_USERS.USER_ID, request.userId())
      .set(GROUP_USERS.DISPLAY_NAME, request.displayName())
      .returning()
      .fetchOptionalInto(GroupMembersRecord.class);

    if (savedGroupMember.isEmpty()) throw new UserNotFoundException(GROUP_USER_NOT_FOUND);

    return GROUP_USER_MAPPER.mapToGroupMember(savedGroupMember.get());
  }

  @Cacheable(value = "group_users", key = "{#id,#userId}", unless = "#result = null")
  public GroupMember findById(final Integer id, final Integer userId) {
    final Optional<GroupMembersRecord> user = dsl
      .selectFrom(GROUP_USERS)
      .where(GROUP_USERS.GROUP_ID.eq(id).and(GROUP_USERS.USER_ID.eq(userId)))
      .fetchOptionalInto(GroupMembersRecord.class);

    if (user.isEmpty()) throw new UserNotFoundException(GROUP_USER_NOT_FOUND);

    return GROUP_USER_MAPPER.mapToGroupMember(user.get());
  }

  public List<GroupMember> findUsers(final Integer groupId) {
    final List<GroupMembersRecord> users = dsl
      .selectFrom(GROUP_USERS)
      .where(GROUP_USERS.GROUP_ID.eq(groupId))
      .fetchInto(GroupMembersRecord.class);
    if (users.isEmpty()) throw new UserNotFoundException(GROUP_USER_NOT_FOUND);
    return GROUP_USER_MAPPER.mapToGroupMembers(users);
  }

  public List<Group> findGroups(final Integer userId) {
    final List<Group> groups = dsl
      .select(GROUPS)
      .from(GROUP_USERS.join(GROUPS).on(GROUP_USERS.GROUP_ID.eq(GROUPS.ID)))
      .where(GROUP_USERS.USER_ID.eq(userId))
      .fetchInto(Group.class);
    if (groups.isEmpty()) throw new GroupNotFoundException(GROUP_USER_NOT_FOUND);
    return groups;
  }

  public void delete(final Integer id, final Integer userId) {
    final boolean isDeleted =
      dsl.deleteFrom(GROUP_USERS).where(GROUP_USERS.GROUP_ID.eq(id)).and(GROUP_USERS.USER_ID.eq(userId)).execute() == 1;
    if (!isDeleted) throw new GroupNotFoundException(GROUP_USER_NOT_FOUND);
  }

  public boolean isUserInGroup(final Integer id, final Integer userId) {
    return dsl.fetchExists(
      dsl.selectFrom(GROUP_USERS).where(GROUP_USERS.GROUP_ID.eq(id)).and(GROUP_USERS.USER_ID.eq(userId))
    );
  }
}
