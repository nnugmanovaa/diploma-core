package kz.codesmith.epay.loan.api.domain.clients;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import kz.codesmith.epay.core.shared.converter.Utils;
import kz.codesmith.epay.core.shared.model.SimpleStatus;
import kz.codesmith.epay.core.shared.model.clients.ClientIdentificationStatus;
import kz.codesmith.epay.loan.api.domain.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "clients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "JsonClientValidationDataType", typeClass = ValidateData.class)
public class ClientEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clients_id_seq")
    @SequenceGenerator(
            name = "clients_id_seq", sequenceName = "clients_clients_id_seq", allocationSize = 1)
    @Column(name = "clients_id")
    private Integer clientsId;

    @Column(name = "clientname")
    private String clientName;

    @Column(name = "name")
    private String firstName;

    @Column(name = "lname")
    private String lastName;

    @Column(name = "mname")
    private String middleName;

    @Column(name = "iin")
    private String iin;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SimpleStatus status;

    @Column(name = "status_time")
    private LocalDateTime statusTime;

    @Column(name = "status_comment")
    private String statusComment;

    @Column(name = "status_by")
    private String statusBy;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "validate_data")
    @Type(type = "JsonClientValidationDataType")
    private ValidateData validateData;

    @Column(name = "identification_status")
    @Enumerated(EnumType.STRING)
    private ClientIdentificationStatus identificationStatus;

    @Column(name = "notification_token")
    private String notificationToken;

    @Column(name = "avatar_url")
    private String avatar;

    @PrePersist
    public void toCreate() {
        super.toCreate();
        setStatus(SimpleStatus.ACTIVE);
        setStatusTime(getInsertedTime());
        setStatusBy(getInsertedBy());
    }

    @PreUpdate
    public void toUpdate() {
        setUpdatedTime(Utils.now());
    }

}
