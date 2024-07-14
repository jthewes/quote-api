package de.zedalite.quotes.web;

import de.zedalite.quotes.data.model.ErrorResponse;
import de.zedalite.quotes.data.model.QuoteResponse;
import de.zedalite.quotes.service.GroupQuoteOfTheDayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Groups", description = "Operations related to groups")
@RequestMapping("groups")
public class GroupQuoteOfTheDayController {

  private final GroupQuoteOfTheDayService service;

  public GroupQuoteOfTheDayController(final GroupQuoteOfTheDayService service) {
    this.service = service;
  }

  @Operation(
    summary = "Get group quote of the day",
    description = "Get group quote of the day",
    operationId = "getGroupQuoteOfTheDay",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Group quote of the day found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuoteResponse.class))
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Principal is no group member",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Group quote of the day not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @PreAuthorize("@authorizer.isGroupMember(principal,#id)")
  @GetMapping("{id}/qotd")
  public ResponseEntity<QuoteResponse> getQuoteOfTheDay(@PathVariable("id") final Integer id) {
    return ResponseEntity.ok(service.findQuoteOfTheDay(id));
  }
}
