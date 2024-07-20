package de.zedalite.quotes.fixtures;

import de.zedalite.quotes.data.model.Group;
import de.zedalite.quotes.data.model.GroupRequest;
import de.zedalite.quotes.data.model.GroupResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class GroupGenerator {

  public static GroupRequest getGroupRequest() {
    return new GroupRequest("test-group", "TestGrou");
  }

  public static Group getGroup() {
    return new Group(1, "TestGrou", "test-group", LocalDateTime.now(), Optional.of(1));
  }

  public static Group getGroupNoCreator() {
    return new Group(1, "TestGrou", "test-group", LocalDateTime.now(), Optional.empty());
  }

  public static GroupResponse getGroupResponse() {
    return new GroupResponse(getGroup(), Optional.empty());
  }

  public static List<Group> getGroups() {
    return List.of(
      new Group(1, "space", "Space", LocalDateTime.now(), Optional.of(1)),
      new Group(2, "outerspa", "Outer Space", LocalDateTime.now(), Optional.of(1))
    );
  }
}
