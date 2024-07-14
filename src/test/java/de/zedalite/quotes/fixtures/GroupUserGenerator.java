package de.zedalite.quotes.fixtures;

import de.zedalite.quotes.data.model.GroupUser;
import de.zedalite.quotes.data.model.GroupUserResponse;
import de.zedalite.quotes.data.model.UserResponse;
import java.util.List;

public class GroupUserGenerator {

  public static GroupUser getGroupUser() {
    return new GroupUser(1, 1, "KING OF THE WORLD");
  }

  public static GroupUser getGroupUserWithoutDisplayName() {
    return new GroupUser(1, 1, null);
  }

  public static List<GroupUser> getGroupUsers() {
    return List.of(new GroupUser(1, 2, "BEST TEST USER"), new GroupUser(1, 3, null));
  }

  public static GroupUserResponse getGroupUserResponse() {
    final UserResponse userResponse = new UserResponse(1, "tester", "TESTER", null);
    return new GroupUserResponse(userResponse, "KING OF THE WORLD");
  }
}
