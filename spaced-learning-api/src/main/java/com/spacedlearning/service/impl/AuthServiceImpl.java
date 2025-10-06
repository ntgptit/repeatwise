package com.spacedlearning.service.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.auth.AuthRequest;
import com.spacedlearning.dto.auth.AuthResponse;
import com.spacedlearning.dto.auth.RefreshTokenRequest;
import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.entity.Role;
import com.spacedlearning.entity.User;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.UserMapper;
import com.spacedlearning.repository.RoleRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.security.CustomUserDetails;
import com.spacedlearning.security.CustomUserDetailsService;
import com.spacedlearning.security.JwtTokenProvider;
import com.spacedlearning.service.AuthService;
import com.spacedlearning.service.EmailVerificationService;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AuthService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	private static final String DEFAULT_ROLE = "ROLE_USER";
	private static final String ERROR_AUTH_INVALID_TOKEN = "error.auth.invalidToken";
	private static final String RESOURCE_USER = "resource.user";
	private static final String EMAIL_FIELD = "email";

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	private final EmailVerificationService emailVerificationService;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider tokenProvider;
	private final MessageSource messageSource;
	private final CustomUserDetailsService userDetailsService;

	@Override
	@Transactional(readOnly = true)
	public AuthResponse authenticate(final AuthRequest request) {
		Objects.requireNonNull(request, "Auth request must not be null");
		Objects.requireNonNull(request.getUsernameOrEmail(), "Username or email must not be null");
		Objects.requireNonNull(request.getPassword(), "Password must not be null");

		log.debug("Authenticating user with username or email: {}", request.getUsernameOrEmail());

		try {
			// Authenticate user
			final Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Generate tokens
			final String accessToken = tokenProvider.generateToken(authentication);
			final String refreshToken = tokenProvider.generateRefreshToken(authentication);

			// Get user details from the authentication principal
			final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			final User user = userDetails.getUser();

			final UserResponse userResponse = userMapper.toDto(user);

			log.info("User authenticated successfully: {}", authentication.getName());
			return AuthResponse.builder().token(accessToken).refreshToken(refreshToken).user(userResponse).build();
		} catch (final Exception e) {
			log.error("Authentication failed for user {}: {}", request.getUsernameOrEmail(), e.getMessage());
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public String getUsernameFromToken(final String token) {
		if (StringUtils.isBlank(token)) {
			throw SpacedLearningException.validationError(messageSource, "error.auth.invalidToken");
		}

		try {
			return tokenProvider.getUsernameFromToken(token);
		} catch (final JwtException e) {
			log.error("Failed to extract username from token: {}", e.getMessage());
			throw SpacedLearningException.forbidden(messageSource, "error.auth.invalidToken");
		}
	}

	@Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(final RefreshTokenRequest request) {
        Objects.requireNonNull(request, "Refresh token request must not be null");
        Objects.requireNonNull(request.getRefreshToken(), "Refresh token must not be null");

        log.debug("Refreshing token");

        try {
            // Validate refresh token
            if (!tokenProvider.validateToken(request.getRefreshToken())
                    || !tokenProvider.isRefreshToken(request.getRefreshToken())) {
                throw SpacedLearningException.forbidden(messageSource, ERROR_AUTH_INVALID_TOKEN);
            }

            // Extract username and load user
            final String usernameOrEmail = tokenProvider.getUsernameFromToken(request.getRefreshToken());

            // Verify user exists in database
            final User user = userRepository.findByUsernameOrEmailWithRoles(usernameOrEmail)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                    messageSource, "resource.user", usernameOrEmail));

            try {
                // Load user details with proper exception handling
                final UserDetails userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);

                // Ensure userDetails is not null before proceeding
                if (userDetails == null) {
                    log.error("UserDetails is null for username: {}", usernameOrEmail);
                    throw SpacedLearningException.resourceNotFound(
                        messageSource, "resource.user.details", usernameOrEmail);
                }

                final Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

                // Generate new tokens
                final String accessToken = tokenProvider.generateToken(authentication);
                final String refreshToken = tokenProvider.generateRefreshToken(authentication);

                final UserResponse userResponse = userMapper.toDto(user);

                log.info("Token refreshed successfully for user: {}", usernameOrEmail);
                return AuthResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .user(userResponse)
                    .build();
            } catch (final Exception e) {
                log.error("Error creating authentication during token refresh for user: {}, Error: {}",
                    usernameOrEmail, e.getMessage());
                throw SpacedLearningException.forbidden(messageSource, "error.auth.failedAuthentication");
            }

        } catch (final JwtException e) {
            log.error("Failed to refresh token: {}", e.getMessage());
            throw SpacedLearningException.forbidden(messageSource, ERROR_AUTH_INVALID_TOKEN);
        }
    }

	@Override
    @Transactional
    public UserResponse register(final RegisterRequest request) {
        Objects.requireNonNull(request, "Register request must not be null");
        Objects.requireNonNull(request.getEmail(), "Email must not be null");
        Objects.requireNonNull(request.getPassword(), "Password must not be null");

        log.debug("Registering new user with email: {}", request.getEmail());

        // Check email exists first to provide specific error
        if (userRepository.existsByEmail(request.getEmail())) {
            throw SpacedLearningException.resourceAlreadyExists(
                messageSource, RESOURCE_USER, EMAIL_FIELD, request.getEmail());
        }

        try {
            // Create new user
            final User user = userMapper.registerRequestToEntity(request);

            // Assign default role
            final Role userRole = findDefaultRole();
            user.addRole(userRole);

            final User savedUser = userRepository.save(user);

            // Create and send email verification
            try {
                emailVerificationService.createEmailVerification(savedUser);
                log.info("Email verification created for user: {}", savedUser.getEmail());
            } catch (Exception e) {
                log.error("Failed to create email verification for user: {}, error: {}", 
                    savedUser.getEmail(), e.getMessage());
                // Continue with registration even if email verification fails
            }

            log.info("User registered successfully with ID: {}", savedUser.getId());
            return userMapper.toDto(savedUser);
        } catch (final DataIntegrityViolationException e) {
            // Handle race condition where another user registered with same email
            // between our check and save
            log.error("Data integrity violation during user registration: {}", e.getMessage());

            if (e.getMessage().contains("email")) {
                throw SpacedLearningException.resourceAlreadyExists(messageSource, RESOURCE_USER, EMAIL_FIELD,
                        request.getEmail());
            }

            throw e;
        }
    }

	@Override
	@Transactional(readOnly = true)
	public boolean validateToken(final String token) {
		if (StringUtils.isBlank(token)) {
			return false;
		}

		try {
			return tokenProvider.validateToken(token);
		} catch (final JwtException e) {
			log.debug("Token validation failed: {}", e.getMessage());
			return false;
		}
	}

	@Override
	@Transactional
	public boolean verifyEmail(String token) {
		log.debug("Verifying email with token: {}", token);
		return emailVerificationService.verifyEmail(token);
	}

	/**
	 * Find default user role
	 * 
	 * @return The default role
	 * @throws SpacedLearningException if default role not found
	 */
	private Role findDefaultRole() {
		return roleRepository.findByName(DEFAULT_ROLE)
				.orElseThrow(() -> new SpacedLearningException(messageSource.getMessage("error.role.defaultNotFound",
						null, "Default role not found", LocaleContextHolder.getLocale()),
						HttpStatus.INTERNAL_SERVER_ERROR));
	}
}