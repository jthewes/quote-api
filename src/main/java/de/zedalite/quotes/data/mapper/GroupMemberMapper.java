package de.zedalite.quotes.data.mapper;

import de.zedalite.quotes.data.jooq.quotes.tables.records.GroupMembersRecord;
import de.zedalite.quotes.data.model.GroupMember;
import de.zedalite.quotes.data.model.GroupMemberResponse;
import de.zedalite.quotes.data.model.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { OptionalMapper.class, UserMapper.class })
public interface GroupMemberMapper {
  GroupMemberMapper INSTANCE = Mappers.getMapper(GroupMemberMapper.class);

  GroupMember mapToGroupMember(final GroupMembersRecord groupMembersRecord);

  List<GroupMember> mapToGroupMembers(List<GroupMembersRecord> users);

  @Mapping(target = "user", source = "user")
  @Mapping(target = "displayName", source = "memberDisplayName")
  GroupMemberResponse mapToResponse(final User user, final String memberDisplayName);
}
