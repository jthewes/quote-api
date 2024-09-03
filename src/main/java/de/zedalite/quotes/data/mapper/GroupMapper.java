package de.zedalite.quotes.data.mapper;

import de.zedalite.quotes.data.jooq.quotes.tables.records.GroupsRecord;
import de.zedalite.quotes.data.model.Group;
import de.zedalite.quotes.data.model.GroupResponse;
import de.zedalite.quotes.data.model.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { OptionalMapper.class, UserMapper.class })
public interface GroupMapper {
  GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

  Group mapToGroup(final GroupsRecord groupsRecord);

  List<Group> mapToGroups(final List<GroupsRecord> groupsRecords);

  GroupResponse mapToResponse(final Group group, User creator);
}
