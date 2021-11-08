package kz.codesmith.epay.loan.api.repository;

import kz.codesmith.epay.loan.api.domain.PhoneVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerificationEntity, Long> {

  PhoneVerificationEntity getByMsisdn(String msisdn);
}
