package de.zedalite.quotes.repository;

import de.zedalite.quotes.data.jooq.quotes.tables.GroupMembers;
import de.zedalite.quotes.data.jooq.quotes.tables.Groups;
import de.zedalite.quotes.data.jooq.quotes.tables.records.GroupMembersRecord;
import de.zedalite.quotes.data.mapper.GroupMemberMapper;
import de.zedalite.quotes.data.model.Group;
import de.zedalite.quotes.data.model.GroupMember;
import de.zedalite.quotes.data.model.GroupMemberRequest;
import de.zedalite.quotes.data.model.GroupMemberUpdateRequest;
import de.zedalite.quotes.exception.GroupNotFoundException;
import de.zedalite.quotes.exception.UserNotFoundException;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

@Repository
public class GroupMemberRepository {

  private static final GroupMemberMapper GROUP_MEMBER_MAPPER = GroupMemberMapper.INSTANCE;
  private static final Groups GROUPS = Groups.GROUPS.as("groups");
  private static final GroupMembers GROUP_MEMBER = GroupMembers.GROUP_MEMBERS.as("group_members");
  private static final String GROUP_NOT_FOUND = "Group not found";
  private static final String GROUP_MEMBER_NOT_FOUND = "Group member not found";

  private final DSLContext dsl;

  public GroupMemberRepository(final DSLContext dsl) {
    this.dsl = dsl;
  }

  @Caching(
    put = @CachePut(value = "groupmembers", key = "{#result.groupId(),#result.userId()}", unless = "#result = null"),
    evict = {
      @CacheEvict(value = "groupmembers_members", key = "{#result.groupId()}"),
      @CacheEvict(value = "groupmembers_groups", key = "{#result.userId()}"),
      @CacheEvict(value = "groupmembers_verification", key = "{#result.groupId(),#result.userId()}"),
    }
  )
  public GroupMember save(final Integer id, final GroupMemberRequest request) {
    final Optional<GroupMembersRecord> savedGroupMember = dsl
      .insertInto(GROUP_MEMBER)
      .set(GROUP_MEMBER.GROUP_ID, id)
      .set(GROUP_MEMBER.USER_ID, request.userId())
      .set(GROUP_MEMBER.DISPLAY_NAME, request.displayName())
      .returning()
      .fetchOptionalInto(GroupMembersRecord.class);

    if (savedGroupMember.isEmpty()) throw new UserNotFoundException(GROUP_MEMBER_NOT_FOUND);

    return GROUP_MEMBER_MAPPER.mapToGroupMember(savedGroupMember.get());
  }

  @Cacheable(value = "groupmembers", key = "{#id,#userId}", unless = "#result = null")
  public GroupMember findById(final Integer id, final Integer userId) {
    final Optional<GroupMembersRecord> user = dsl
      .selectFrom(GROUP_MEMBER)
      .where(GROUP_MEMBER.GROUP_ID.eq(id).and(GROUP_MEMBER.USER_ID.eq(userId)))
      .fetchOptionalInto(GroupMembersRecord.class);

    if (user.isEmpty()) throw new UserNotFoundException(GROUP_MEMBER_NOT_FOUND);

    return GROUP_MEMBER_MAPPER.mapToGroupMember(user.get());
  }

  @Cacheable(value = "groupmembers_members", key = "{#groupId}", unless = "#result = null")
  public List<GroupMember> findMembers(final Integer groupId) {
    final List<GroupMembersRecord> members = dsl
      .selectFrom(GROUP_MEMBER)
      .where(GROUP_MEMBER.GROUP_ID.eq(groupId))
      .fetchInto(GroupMembersRecord.class);
    if (members.isEmpty()) throw new UserNotFoundException(GROUP_MEMBER_NOT_FOUND);
    return GROUP_MEMBER_MAPPER.mapToGroupMembers(members);
  }

  @Cacheable(value = "groupmembers_groups", key = "{#userId}", unless = "#result = null")
  public List<Group> findGroups(final Integer userId) {
    final List<Group> groups = dsl
      .select(GROUPS)
      .from(GROUP_MEMBER.join(GROUPS).on(GROUP_MEMBER.GROUP_ID.eq(GROUPS.ID)))
      .where(GROUP_MEMBER.USER_ID.eq(userId))
      .fetchInto(Group.class);
    if (groups.isEmpty()) throw new GroupNotFoundException(GROUP_NOT_FOUND);
    return groups;
  }

  @CachePut(value = "groupmembers", key = "{#result.groupId(),#result.userId()}", unless = "#result = null")
  public GroupMember update(final Integer id, final Integer userId, final GroupMemberUpdateRequest request) {
    final Optional<GroupMembersRecord> member = dsl
      .update(GROUP_MEMBER)
      .set(GROUP_MEMBER.DISPLAY_NAME, request.displayName())
      .where(GROUP_MEMBER.GROUP_ID.eq(id))
      .and(GROUP_MEMBER.USER_ID.eq(userId))
      .returning()
      .fetchOptionalInto(GroupMembersRecord.class);
    if (member.isEmpty()) throw new GroupNotFoundException(GROUP_MEMBER_NOT_FOUND);
    return GROUP_MEMBER_MAPPER.mapToGroupMember(member.get());
  }

  @Caching(
    evict = {
      @CacheEvict(value = "groupmembers", key = "{#id,#userId}"),
      @CacheEvict(value = "groupmembers_members", key = "{#id}"),
      @CacheEvict(value = "groupmembers_groups", key = "{#userId}"),
      @CacheEvict(value = "groupmembers_verification", key = "{#id,#userId}"),
    }
  )
  public void delete(final Integer id, final Integer userId) {
    final boolean isDeleted =
      dsl.deleteFrom(GROUP_MEMBER).where(GROUP_MEMBER.GROUP_ID.eq(id)).and(GROUP_MEMBER.USER_ID.eq(userId)).execute() ==
      1;
    if (!isDeleted) throw new GroupNotFoundException(GROUP_MEMBER_NOT_FOUND);
  }

  @Cacheable(value = "groupmembers_verification", key = "{#id,#userId}", unless = "#result = null")
  public boolean isMember(final Integer id, final Integer userId) {
    return dsl.fetchExists(
      dsl.selectFrom(GROUP_MEMBER).where(GROUP_MEMBER.GROUP_ID.eq(id)).and(GROUP_MEMBER.USER_ID.eq(userId))
    );
  }
}
