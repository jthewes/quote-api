package de.zedalite.quotes.data.mapper;

import de.zedalite.quotes.data.jooq.users.tables.records.UsersRecord;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  User mapToUser(final UsersRecord usersRecord);

  List<User> mapToUserList(final List<UsersRecord> users);

  UserResponse mapToResponse(final User user);

  List<UserResponse> mapToResponses(final List<User> user);
}
