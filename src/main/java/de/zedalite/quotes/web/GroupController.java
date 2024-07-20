package de.zedalite.quotes.web;

import de.zedalite.quotes.data.model.ErrorResponse;
import de.zedalite.quotes.data.model.GroupDisplayNameRequest;
import de.zedalite.quotes.data.model.GroupRequest;
import de.zedalite.quotes.data.model.GroupResponse;
import de.zedalite.quotes.data.model.UserPrincipal;
import de.zedalite.quotes.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Groups", description = "Operations related to groups")
@RequestMapping("groups")
@Validated
public class GroupController {

  private final GroupService service;

  public GroupController(final GroupService service) {
    this.service = service;
  }

  @Operation(
    summary = "Get a group by its id",
    description = "Get a group by its id",
    operationId = "getGroup",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupResponse.class))
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Group retrieval not allowed",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @GetMapping("{id}")
  public ResponseEntity<GroupResponse> getGroup(@PathVariable("id") final Integer id) {
    return ResponseEntity.ok(service.find(id));
  }

  @Operation(
    summary = "Get own groups",
    description = "Get own groups",
    operationId = "getOwnGroups",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Groups found",
        content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = GroupResponse.class))
        )
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Group retrieval not allowed",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @GetMapping("me")
  public ResponseEntity<List<GroupResponse>> getGroups(@AuthenticationPrincipal final UserPrincipal principal) {
    return ResponseEntity.ok(service.findAllByUser(principal.getId()));
  }

  @Operation(
    summary = "Create a new group",
    description = "Create a new group",
    operationId = "createGroup",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group created",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupResponse.class))
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Group not created",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Group creation not allowed",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PostMapping
  public ResponseEntity<GroupResponse> createGroup(
    @RequestBody @Valid final GroupRequest request,
    @AuthenticationPrincipal final UserPrincipal principal
  ) {
    return ResponseEntity.ok(service.create(request, principal.getId()));
  }

  @Operation(
    summary = "Join a group by invite code",
    description = "Join a group by invite code",
    operationId = "joinGroupByCode",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group joined",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupResponse.class))
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invite code not valid",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Group entry not allowed",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PostMapping("invite")
  public ResponseEntity<GroupResponse> joinGroup(
    @RequestParam @Size(min = 1, max = 8) final String code,
    @AuthenticationPrincipal final UserPrincipal principal
  ) {
    return ResponseEntity.ok(service.join(code, principal.getId()));
  }

  @PreAuthorize("@authorizer.isGroupMember(principal,#id)")
  @PatchMapping("{id}/displayname")
  public ResponseEntity<GroupResponse> updateDisplayName(
    @PathVariable("id") final Integer id,
    @RequestBody @Valid final GroupDisplayNameRequest displayName
  ) {
    return ResponseEntity.ok(service.updateDisplayName(id, displayName));
  }
}
