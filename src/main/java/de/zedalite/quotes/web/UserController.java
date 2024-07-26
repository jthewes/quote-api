package de.zedalite.quotes.web;

import de.zedalite.quotes.data.model.*;
import de.zedalite.quotes.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Users", description = "Operations related to users")
@RequestMapping("users")
public class UserController {

  private final UserService service;

  public UserController(final UserService service) {
    this.service = service;
  }

  @Operation(
    summary = "Create a new user",
    description = "Create a new user",
    operationId = "createUser",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Users created",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Users not created",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Unauthenticated",
        content = @Content(mediaType = "application/json")
      ),
      @ApiResponse(
        responseCode = "403",
        description = "User creation not allowed",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PostMapping
  public ResponseEntity<UserResponse> create(@Valid @RequestBody final UserRequest user) {
    return ResponseEntity.ok(service.create(user));
  }

  @Operation(
    summary = "Get your user details",
    description = "Get your user details",
    operationId = "getUser",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "User found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Unauthenticated",
        content = @Content(mediaType = "application/json")
      ),
      @ApiResponse(
        responseCode = "403",
        description = "User retrieval not allowed",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @GetMapping("me")
  public ResponseEntity<UserResponse> get(@AuthenticationPrincipal final UserPrincipal principal) {
    return ResponseEntity.ok(service.find(principal.getId()));
  }

  @Operation(
    summary = "Update own user",
    description = "Update own user",
    operationId = "updateOwnUser",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Name patched",
        content = @Content(mediaType = "application/json")
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Unauthenticated",
        content = @Content(mediaType = "application/json")
      ),
      @ApiResponse(
        responseCode = "403",
        description = "User patching not allowed",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PatchMapping("me")
  public ResponseEntity<UserResponse> update(
    @AuthenticationPrincipal final UserPrincipal principal,
    @RequestBody @Valid final UserUpdateRequest request
  ) {
    return ResponseEntity.ok(service.update(principal.getId(), request));
  }
}
