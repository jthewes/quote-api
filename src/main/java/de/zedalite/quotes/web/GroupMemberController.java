package de.zedalite.quotes.web;

import de.zedalite.quotes.data.model.ErrorResponse;
import de.zedalite.quotes.data.model.GroupMemberResponse;
import de.zedalite.quotes.data.model.GroupMemberUpdateRequest;
import de.zedalite.quotes.data.model.UserPrincipal;
import de.zedalite.quotes.service.GroupMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Groups", description = "Operations related to groups")
@RequestMapping("groups")
public class GroupMemberController {

  private final GroupMemberService service;

  public GroupMemberController(final GroupMemberService service) {
    this.service = service;
  }

  @Operation(
    summary = "Get all group members",
    description = "Get all group members",
    operationId = "getGroupMembers",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group member found",
        content = {
          @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = GroupMemberResponse.class))
          ),
        }
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Principal is no group member",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group member not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isGroupMember(principal,#id)")
  @GetMapping("{id}/members")
  public ResponseEntity<List<GroupMemberResponse>> getMembers(@PathVariable("id") final Integer id) {
    return ResponseEntity.ok(service.findAll(id));
  }

  @Operation(
    summary = "Get a group member by its id",
    description = "Get a group member by its id",
    operationId = "getGroupMember",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group member found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupMemberResponse.class))
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Principal is no group member",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group member not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isGroupMember(principal,#id)")
  @GetMapping("{id}/members/{userId}")
  public ResponseEntity<GroupMemberResponse> getUser(
    @PathVariable("id") final Integer id,
    @PathVariable("userId") final Integer userId
  ) {
    return ResponseEntity.ok(service.find(id, userId));
  }

  @Operation(
    summary = "Update own group member",
    description = "Update own group member",
    operationId = "updateOwnGroupMember",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group member updated",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupMemberResponse.class))
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Principal is no group member",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group member not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isGroupMember(principal,#id)")
  @PatchMapping("{id}/members/me")
  public ResponseEntity<GroupMemberResponse> updateOwnMember(
    @PathVariable("id") final Integer id,
    @AuthenticationPrincipal final UserPrincipal principal,
    @Valid @RequestBody final GroupMemberUpdateRequest request
  ) {
    return ResponseEntity.ok(service.update(id, principal.getId(), request));
  }

  @Operation(
    summary = "Leave a group",
    description = "Leave a group",
    operationId = "leaveGroup",
    responses = {
      @ApiResponse(responseCode = "204", description = "Group left"),
      @ApiResponse(
        responseCode = "403",
        description = "Group leaving not allowed",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isGroupMember(principal,#id)")
  @DeleteMapping("{id}/members/me")
  public ResponseEntity<Void> leaveGroup(
    @PathVariable final Integer id,
    @AuthenticationPrincipal final UserPrincipal principal
  ) {
    service.leave(id, principal.getId());
    return ResponseEntity.noContent().build();
  }
}
