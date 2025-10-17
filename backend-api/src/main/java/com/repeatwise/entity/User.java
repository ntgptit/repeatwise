package com.repeatwise.entity;

import com.repeatwise.entity.base.SoftDeletableEntity;
import com.repeatwise.entity.enums.Language;
import com.repeatwise.entity.enums.Theme;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

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
@Where(clause = "deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends SoftDeletableEntity {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Column(name = "username", nullable = false, unique = true, length = 30)
    private String username;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Password hash is required")
    @Size(min = 60, max = 60, message = "Password hash must be exactly 60 characters (bcrypt)")
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 2)
    @Builder.Default
    private Language language = Language.EN;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false, length = 10)
    @Builder.Default
    private Theme theme = Theme.LIGHT;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    @Column(name = "bio", length = 500)
    private String bio;

    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    // Relationships will be added when needed (folders, decks, etc.)

    /**
     * Business method: Check if email is verified
     * Note: Email verification not in MVP, always returns true
     */
    public boolean isEmailVerified() {
        return true; // MVP: No email verification
    }

    /**
     * Business method: Get display name (email prefix if name is empty)
     */
    public String getDisplayName() {
        if (name != null && !name.isBlank()) {
            return name;
        }
        return email.substring(0, email.indexOf('@'));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        final User user = (User) o;
        return getId() != null && getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", language=" + language +
                '}';
    }
}
