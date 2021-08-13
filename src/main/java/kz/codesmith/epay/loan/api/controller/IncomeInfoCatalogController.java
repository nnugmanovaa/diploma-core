package kz.codesmith.epay.loan.api.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import kz.codesmith.epay.core.shared.controller.qualifier.ApiPageable;
import kz.codesmith.epay.loan.api.model.IncomeInfoCatalogDto;
import kz.codesmith.epay.loan.api.model.IncomeInfoType;
import kz.codesmith.epay.loan.api.service.impl.IncomeInfoCatalogService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Logged
@RestController
@RequestMapping("/income-info-catalog")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MFO_ADMIN')")
public class IncomeInfoCatalogController {

  private final IncomeInfoCatalogService catalogService;

  @ApiPageable
  @GetMapping("/{type}")
  @PreAuthorize("hasAnyAuthority('CLIENT_USER', 'MFO_ADMIN')")
  public ResponseEntity<Page<IncomeInfoCatalogDto>> getAllCatalogItems(
      @PathVariable("type") IncomeInfoType type,
      @ApiIgnore Pageable pageable) {
    return ResponseEntity.ok(catalogService.getPagedAndSortedCatalogItems(type, pageable));
  }

  @PostMapping
  public ResponseEntity<IncomeInfoCatalogDto> addCatalogItem(
      @Valid @RequestBody IncomeInfoCatalogDto dto) {
    return ResponseEntity.ok(catalogService.upsertCatalogItem(dto));
  }

  @PutMapping
  public ResponseEntity<IncomeInfoCatalogDto> updateCatalogItem(
      @Valid @RequestBody IncomeInfoCatalogDto dto) {
    return ResponseEntity.ok(catalogService.upsertCatalogItem(dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCatalogItem(@PathVariable("id") @NotBlank Integer id) {
    catalogService.deleteCatalogItem(id);
    return ResponseEntity.ok("Entity deleted");
  }

  @GetMapping("/types")
  @PreAuthorize("hasAnyAuthority('CLIENT_USER', 'MFO_ADMIN')")
  public ResponseEntity<?> getIncomeInfoTypes() {
    return ResponseEntity.ok(IncomeInfoType.values());
  }

}
