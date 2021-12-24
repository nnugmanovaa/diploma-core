package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.constraints.NotBlank;
import kz.codesmith.epay.loan.api.model.acquiring.CardSaveDto;
import kz.codesmith.epay.loan.api.model.acquiring.OrderSummaryDto;
import kz.codesmith.epay.loan.api.model.acquiring.SaveCardRequestDto;
import kz.codesmith.epay.loan.api.service.IAcquiringService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Logged
@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CLIENT_USER')")
public class CardsController {
  private final IAcquiringService acquiringService;

  @ApiOperation(
      value = "Init card save",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<OrderSummaryDto> initCardSave(@RequestBody SaveCardRequestDto requestDto) {
    return ResponseEntity.ok(acquiringService.saveCard(requestDto));
  }

  @GetMapping
  @ApiOperation(
      value = "Lists all cards by client reference",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<List<CardSaveDto>> listSavedCards(
      @RequestParam @NotBlank String extClientRef
  ) {
    return ResponseEntity.ok(acquiringService.getAllSavedCards(extClientRef));
  }

  @DeleteMapping
  @ApiOperation(
      value = "Deletes saved card",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<String> deleteSavedCard(@RequestParam @NotBlank String cardsId) {
    acquiringService.deleteSavedCard(cardsId);
    return ResponseEntity.ok().build();
  }
}
