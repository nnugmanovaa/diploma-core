package kz.codesmith.epay.loan.api.domain.users;

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
import javax.validation.constraints.NotBlank;
import kz.codesmith.epay.core.shared.converter.Utils;
import kz.codesmith.epay.core.shared.model.OwnerType;
import kz.codesmith.epay.core.shared.model.SimpleStatus;

@Entity
@Table(
        name = "users"
)
public class UserEntity {
    @Id
    @Column(
            name = "user_id"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "users_seq"
    )
    @SequenceGenerator(
            name = "users_seq",
            sequenceName = "users_user_id_seq",
            allocationSize = 1
    )
    private Long userId;
    @NotBlank
    @Column(
            name = "username",
            nullable = false,
            unique = true,
            updatable = false
    )
    private String username;
    @NotBlank
    @Column(
            name = "password",
            nullable = false
    )
    private String password;
    @Column(
            name = "status"
    )
    @Enumerated(EnumType.STRING)
    private SimpleStatus status;
    @Column(
            name = "owner_type",
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;
    @Column(
            name = "owner_id",
            nullable = false
    )
    private Integer ownerId;
    @Column(
            name = "operators_id"
    )
    private Integer operatorsId;
    @Column(
            name = "firstname"
    )
    private String firstname;
    @Column(
            name = "lastname"
    )
    private String lastname;
    @Column(
            name = "email"
    )
    private String email;
    @Column(
            name = "inserted_time",
            updatable = false
    )
    private LocalDateTime insertedTime;
    @Column(
            name = "updated_time"
    )
    private LocalDateTime updatedTime;
    @Column(
            name = "updated_by"
    )
    private String updatedBy;
    @Column(
            name = "inserted_by",
            updatable = false
    )
    private String insertedBy;

    @PrePersist
    public void toCreate() {
        this.setInsertedTime(Utils.now());
        this.setUpdatedTime(this.getInsertedTime());
    }

    @PreUpdate
    public void toUpdate() {
        this.setUpdatedTime(Utils.now());
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public SimpleStatus getStatus() {
        return this.status;
    }

    public OwnerType getOwnerType() {
        return this.ownerType;
    }

    public Integer getOwnerId() {
        return this.ownerId;
    }

    public Integer getOperatorsId() {
        return this.operatorsId;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public String getEmail() {
        return this.email;
    }

    public LocalDateTime getInsertedTime() {
        return this.insertedTime;
    }

    public LocalDateTime getUpdatedTime() {
        return this.updatedTime;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public String getInsertedBy() {
        return this.insertedBy;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(SimpleStatus status) {
        this.status = status;
    }

    public void setOwnerType(OwnerType ownerType) {
        this.ownerType = ownerType;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public void setOperatorsId(Integer operatorsId) {
        this.operatorsId = operatorsId;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setInsertedTime(LocalDateTime insertedTime) {
        this.insertedTime = insertedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setInsertedBy(String insertedBy) {
        this.insertedBy = insertedBy;
    }

    public UserEntity(Long userId, String username, String password, SimpleStatus status, OwnerType ownerType, Integer ownerId, Integer operatorsId, String firstname, String lastname, String email, LocalDateTime insertedTime, LocalDateTime updatedTime, String updatedBy, String insertedBy) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.status = status;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.operatorsId = operatorsId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.insertedTime = insertedTime;
        this.updatedTime = updatedTime;
        this.updatedBy = updatedBy;
        this.insertedBy = insertedBy;
    }

    public UserEntity() {
    }
}

