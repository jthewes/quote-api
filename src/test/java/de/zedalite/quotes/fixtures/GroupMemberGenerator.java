package de.zedalite.quotes.fixtures;

import de.zedalite.quotes.data.model.GroupMember;
import de.zedalite.quotes.data.model.GroupMemberResponse;
import de.zedalite.quotes.data.model.UserResponse;
import java.util.List;

public class GroupMemberGenerator {

  // TODO replace test fixures with mocks in test for better extensibility

  public static GroupMember getGroupMember() {
    return new GroupMember(1, 1, "KING OF THE WORLD");
  }

  public static GroupMember getGroupMemberWithoutDisplayName() {
    return new GroupMember(1, 1, null);
  }

  public static List<GroupMember> getGroupMembers() {
    return List.of(new GroupMember(1, 2, "BEST TEST USER"), new GroupMember(1, 3, null));
  }

  public static GroupMemberResponse getGroupMemberResponse() {
    final UserResponse userResponse = new UserResponse(1, "tester", "TESTER", null);
    return new GroupMemberResponse(userResponse, "KING OF THE WORLD");
  }
}
