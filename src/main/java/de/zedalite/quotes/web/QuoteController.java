package de.zedalite.quotes.web;

import de.zedalite.quotes.data.model.CountResponse;
import de.zedalite.quotes.data.model.ErrorResponse;
import de.zedalite.quotes.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Quotes", description = "Operations related to quotes")
@RequestMapping("quotes")
public class QuoteController {

  private final QuoteService service;

  public QuoteController(final QuoteService service) {
    this.service = service;
  }

  @Operation(
    summary = "Get the count of saved quotes",
    description = "Get the count of saved quotes",
    operationId = "getQuoteCount",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Quotes found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountResponse.class))
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Unauthenticated",
        content = @Content(mediaType = "application/json")
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Quotes not found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      ),
    }
  )
  @GetMapping("count")
  public ResponseEntity<CountResponse> getCount() {
    return ResponseEntity.ok(service.count());
  }
}
