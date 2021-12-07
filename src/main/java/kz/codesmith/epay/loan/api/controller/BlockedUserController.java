package kz.codesmith.epay.loan.api.controller;

import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.domain.BlockedUserEntity;
import kz.codesmith.epay.loan.api.model.BlockedUserDto;
import kz.codesmith.epay.loan.api.service.IBlockedUserService;
import kz.codesmith.epay.security.model.UserContext;
import kz.codesmith.epay.security.model.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/blocked-users")
public class BlockedUserController {

  private final IBlockedUserService blockedUserService;
  private final UserContextHolder userContextHolder;

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteBlockedUser(@PathVariable("id") @NotNull Long id) {
    blockedUserService.deleteBlockedUser(id);
    return ResponseEntity.ok("Entity deleted");
  }

  @PostMapping
  public ResponseEntity<BlockedUserDto> createBlockedUser(
      @Valid @RequestBody BlockedUserDto blockedUserDto) {
    UserContext userContext = userContextHolder.getContext();
    blockedUserDto.setAuthor(userContext.getUsername());
    return ResponseEntity.ok(blockedUserService.createBlockedUser(blockedUserDto));
  }

  @GetMapping
  public ResponseEntity<Page<BlockedUserDto>> getBlockedUsers(@ApiIgnore Pageable pageable) {
    return ResponseEntity.ok(blockedUserService.getBlockedUsers(pageable));
  }

  @GetMapping("/by-iin/{iin}")
  public ResponseEntity<BlockedUserEntity> getBlockedUserByIin(
      @PathVariable("iin") @NotBlank String iin) {
    var entity = blockedUserService.findBlockedUserByIin(iin);
    if (Objects.isNull(entity)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.ok(entity);
  }
}
