package com.repeatwise.entity;

import org.hibernate.annotations.SQLRestriction;

import com.repeatwise.entity.base.SoftDeletableEntity;
import com.repeatwise.entity.enums.Language;
import com.repeatwise.entity.enums.Theme;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * User entity - Core entity for authentication and user management
 *
 * Requirements:
 * - UC-001: User Registration
 * - UC-002: User Login
 * - Entity Specifications: users table
 *
 * Security:
 * - password_hash: bcrypt hashed (60 chars, cost 12)
 * - username: unique, indexed for fast lookup
 * - email: unique, indexed for fast lookup
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_email", columnList = "email")
})
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends SoftDeletableEntity {

    @NotBlank(message = "{error.user.username.required}")
    @Size(min = 3, max = 30, message = "{error.user.username.length}")
    @Column(name = "username", nullable = false, unique = true, length = 30)
    private String username;

    @NotBlank(message = "{error.user.name.required}")
    @Size(max = 100, message = "{error.user.name.too.long}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "{error.user.email.required}")
    @Email(message = "{error.user.email.invalid}")
    @Size(max = 255, message = "{error.user.email.too.long}")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "{error.user.password.hash.required}")
    @Size(min = 60, max = 60, message = "{error.user.password.hash.length}")
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 2)
    @Builder.Default
    private Language language = Language.EN;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false, length = 10)
    @Builder.Default
    private Theme theme = Theme.SYSTEM;

    @NotBlank(message = "{error.user.timezone.required}")
    @Size(max = 50, message = "{error.user.timezone.length}")
    @Column(name = "timezone", nullable = false, length = 50)
    @Builder.Default
    private String timezone = "Asia/Ho_Chi_Minh";

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final User user)) {
            return false;
        }
        return (getId() != null) && getId().equals(user.getId());
    }

    /**
     * Business method: Get display name (email prefix if name is empty)
     */
    public String getDisplayName() {
        if ((this.name != null) && !this.name.isBlank()) {
            return this.name;
        }
        return this.email.substring(0, this.email.indexOf('@'));
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Business method: Check if email is verified
     * Note: Email verification not in MVP, always returns true
     */
    public boolean isEmailVerified() {
        return true; // MVP: No email verification
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + this.username + '\'' +
                ", email='" + this.email + '\'' +
                ", name='" + this.name + '\'' +
                ", language=" + this.language +
                '}';
    }
}
