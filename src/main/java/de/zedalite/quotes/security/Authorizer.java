package de.zedalite.quotes.security;

import de.zedalite.quotes.data.model.UserPrincipal;
import de.zedalite.quotes.service.GroupMemberService;
import org.springframework.stereotype.Component;

@Component
public class Authorizer {

  private final GroupMemberService groupMemberService;

  public Authorizer(final GroupMemberService groupMemberService) {
    this.groupMemberService = groupMemberService;
  }

  /**
   * Checks if a user is in a specified group.
   *
   * @param principal the UserPrincipal
   * @param groupId   the ID of the group to check if the user is in
   * @return true if the user is in the group, false otherwise
   */
  public boolean isGroupMember(final UserPrincipal principal, final Integer groupId) {
    return groupMemberService.isGroupMember(groupId, principal.getId());
  }
}
