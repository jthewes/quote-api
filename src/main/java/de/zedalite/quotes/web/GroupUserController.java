package de.zedalite.quotes.web;

import de.zedalite.quotes.data.model.ErrorResponse;
import de.zedalite.quotes.data.model.GroupUserRequest;
import de.zedalite.quotes.data.model.GroupUserResponse;
import de.zedalite.quotes.data.model.UserPrincipal;
import de.zedalite.quotes.service.GroupUserService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Groups", description = "Operations related to groups")
@RequestMapping("groups")
public class GroupUserController {

  private final GroupUserService service;

  public GroupUserController(final GroupUserService service) {
    this.service = service;
  }

  @Operation(
    summary = "Get all group users",
    description = "Get all group users",
    operationId = "getGroupUsers",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group user found",
        content = {
          @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = GroupUserResponse.class))
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
        description = "Group user not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isUserInGroup(principal,#id)")
  @GetMapping("{id}/users")
  public ResponseEntity<List<GroupUserResponse>> getUsers(@PathVariable("id") final Integer id) {
    return ResponseEntity.ok(service.findAll(id));
  }

  @Operation(
    summary = "Get a group user by its id",
    description = "Get a group user by its id",
    operationId = "getGroupUser",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group user found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupUserResponse.class))
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Principal is no group member",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group user not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isUserInGroup(principal,#id)")
  @GetMapping("{id}/users/{userId}")
  public ResponseEntity<GroupUserResponse> getUser(
    @PathVariable("id") final Integer id,
    @PathVariable("userId") final Integer userId
  ) {
    return ResponseEntity.ok(service.find(id, userId));
  }

  /**
   * @deprecated Use invitation code instead
   */
  @Operation(
    summary = "Create a new group user",
    description = "Create a new group user",
    operationId = "createGroupUser",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group user created",
        content = @Content(mediaType = "application/json")
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Group user not created",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Principal is no group member",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group/User not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isUserInGroup(principal,#id)")
  @PostMapping("{id}/users")
  @Deprecated(since = "1.6.0", forRemoval = true)
  public ResponseEntity<GroupUserResponse> createUser(
    @PathVariable("id") final Integer id,
    @RequestBody @Valid final GroupUserRequest request
  ) {
    // TODO is this endpoint necessary? -> invitation code should be enough?
    return ResponseEntity.ok(service.create(id, request));
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
  @PreAuthorize("@authorizer.isUserInGroup(principal,#id)")
  @DeleteMapping("{id}/users/me")
  public ResponseEntity<Void> leaveGroup(
    @PathVariable final Integer id,
    @AuthenticationPrincipal final UserPrincipal principal
  ) {
    service.leave(id, principal.getId());
    return ResponseEntity.noContent().build();
  }
}
