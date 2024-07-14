package de.zedalite.quotes.web;

import de.zedalite.quotes.data.model.CountResponse;
import de.zedalite.quotes.data.model.ErrorResponse;
import de.zedalite.quotes.data.model.FilterKey;
import de.zedalite.quotes.data.model.QuoteRequest;
import de.zedalite.quotes.data.model.QuoteResponse;
import de.zedalite.quotes.data.model.SortField;
import de.zedalite.quotes.data.model.SortMode;
import de.zedalite.quotes.data.model.UserPrincipal;
import de.zedalite.quotes.service.GroupQuoteService;
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
public class GroupQuoteController {

  private final GroupQuoteService service;

  public GroupQuoteController(final GroupQuoteService service) {
    this.service = service;
  }

  @Operation(
    summary = "Get all group quotes",
    description = "Get all group quotes",
    operationId = "getGroupQuotes",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group quote found",
        content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = QuoteResponse.class))
        )
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Principal is no group member",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group quotes not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isGroupMember(principal,#id)")
  @GetMapping("{id}/quotes")
  public ResponseEntity<List<QuoteResponse>> getAll(
    @PathVariable("id") final Integer id,
    @RequestParam(defaultValue = "NONE") @Valid final FilterKey filter,
    @RequestParam(required = false) @Size(max = 32) @Valid final String filterValue,
    @RequestParam(defaultValue = "NONE") @Valid final SortField sortField,
    @RequestParam(defaultValue = "NONE") @Valid final SortMode sortMode
  ) {
    return ResponseEntity.ok(service.findAll(id, filter, filterValue, sortField, sortMode));
  }

  @Operation(
    summary = "Get a group quote by its id",
    description = "Get a group quote by its id",
    operationId = "getGroupQuote",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group quote found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuoteResponse.class))
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Principal is no group member",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group quote not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isGroupMember(principal,#id)")
  @GetMapping("{id}/quotes/{quoteId}")
  public ResponseEntity<QuoteResponse> get(
    @PathVariable("id") final Integer id,
    @PathVariable("quoteId") final Integer quoteId
  ) {
    return ResponseEntity.ok(service.find(id, quoteId));
  }

  @Operation(
    summary = "Get the count of saved quotes",
    description = "Get the count of saved quotes",
    operationId = "getGroupQuoteCount",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group quotes count found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountResponse.class))
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Principal is no group member",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group quotes not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isGroupMember(principal,#id)")
  @GetMapping("{id}/quotes/count")
  public ResponseEntity<CountResponse> getQuotesCount(@PathVariable("id") final Integer id) {
    return ResponseEntity.ok(service.count(id));
  }

  @Operation(
    summary = "Create a new group quote",
    description = "Create a new group quote",
    operationId = "createGroupQuote",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group quote created",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuoteResponse.class))
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Group quote not created",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Principal is no group member",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group quote not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isGroupMember(principal,#id)")
  @PostMapping("{id}/quotes")
  public ResponseEntity<QuoteResponse> createQuote(
    @PathVariable("id") final Integer id,
    @RequestBody @Valid final QuoteRequest request,
    @AuthenticationPrincipal final UserPrincipal principal
  ) {
    return ResponseEntity.ok(service.create(id, request, principal.getId()));
  }
}
