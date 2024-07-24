package de.zedalite.quotes.repository;

import de.zedalite.quotes.data.jooq.quotes.tables.Groups;
import de.zedalite.quotes.data.jooq.quotes.tables.records.GroupsRecord;
import de.zedalite.quotes.data.mapper.GroupMapper;
import de.zedalite.quotes.data.model.Group;
import de.zedalite.quotes.data.model.GroupRequest;
import de.zedalite.quotes.data.model.GroupUpdateRequest;
import de.zedalite.quotes.exception.GroupNotFoundException;
import de.zedalite.quotes.exception.QuoteNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

/**
 * Provides methods for interacting with the database to perform CRUD operations on quotes.
 */
@Repository
public class GroupRepository {

  private static final GroupMapper GROUP_MAPPER = GroupMapper.INSTANCE;

  private static final String GROUP_NOT_FOUND = "Group not found";

  private static final Groups GROUPS = Groups.GROUPS.as("groups");

  private final DSLContext dsl;

  public GroupRepository(final DSLContext dsl) {
    this.dsl = dsl;
  }

  @CachePut(value = "groups", key = "#result.id()", unless = "#result == null")
  public Group save(final GroupRequest group, final Integer creatorId) throws QuoteNotFoundException {
    final Optional<GroupsRecord> savedGroup = dsl
      .insertInto(GROUPS)
      .set(GROUPS.INVITE_CODE, group.inviteCode())
      .set(GROUPS.DISPLAY_NAME, group.displayName())
      .set(GROUPS.CREATION_DATE, LocalDateTime.now())
      .set(GROUPS.CREATOR_ID, creatorId)
      .returning()
      .fetchOptionalInto(GroupsRecord.class);
    if (savedGroup.isEmpty()) throw new GroupNotFoundException(GROUP_NOT_FOUND);
    return GROUP_MAPPER.mapToGroup(savedGroup.get());
  }

  @Cacheable(value = "groups", key = "#id", unless = "#result == null")
  public Group findById(final Integer id) throws GroupNotFoundException {
    final Optional<GroupsRecord> group = dsl
      .selectFrom(GROUPS)
      .where(GROUPS.ID.eq(id))
      .fetchOptionalInto(GroupsRecord.class);
    if (group.isEmpty()) throw new GroupNotFoundException(GROUP_NOT_FOUND);
    return GROUP_MAPPER.mapToGroup(group.get());
  }

  @Cacheable(value = "groupcodes", key = "#code", unless = "#result == null")
  public Group findByCode(final String code) {
    final Optional<GroupsRecord> group = dsl
      .selectFrom(GROUPS)
      .where(GROUPS.INVITE_CODE.eq(code))
      .fetchOptionalInto(GroupsRecord.class);
    if (group.isEmpty()) throw new GroupNotFoundException(GROUP_NOT_FOUND);
    return GROUP_MAPPER.mapToGroup(group.get());
  }

  @Caching(
    cacheable = @Cacheable(value = "groups", key = "#id", unless = "#result == null"),
    evict = @CacheEvict(value = "groupcodes", allEntries = true)
  )
  @Cacheable(value = "groups", key = "#id", unless = "#result == null")
  public Group update(final Integer id, final GroupUpdateRequest request) {
    final Optional<GroupsRecord> updatedGroup = dsl
      .update(GROUPS)
      .set(GROUPS.DISPLAY_NAME, request.displayName())
      .set(GROUPS.INVITE_CODE, request.inviteCode())
      .where(GROUPS.ID.eq(id))
      .returning()
      .fetchOptionalInto(GroupsRecord.class);
    if (updatedGroup.isEmpty()) throw new GroupNotFoundException(GROUP_NOT_FOUND);
    return GROUP_MAPPER.mapToGroup(updatedGroup.get());
  }
}
