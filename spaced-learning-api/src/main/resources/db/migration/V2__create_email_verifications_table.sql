-- Create email_verifications table for UC-001: User Registration email verification
CREATE TABLE email_verifications (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    verification_token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    verified_at TIMESTAMP,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT pk_email_verifications PRIMARY KEY (id),
    CONSTRAINT fk_email_verifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_email_verifications_token ON email_verifications(verification_token);
CREATE INDEX idx_email_verifications_user_id ON email_verifications(user_id);
CREATE INDEX idx_email_verifications_expires_at ON email_verifications(expires_at);
CREATE INDEX idx_email_verifications_is_verified ON email_verifications(is_verified);

-- Add comment to table
COMMENT ON TABLE email_verifications IS 'Email verification tokens for user registration verification';
COMMENT ON COLUMN email_verifications.verification_token IS 'Unique verification token sent to user email';
COMMENT ON COLUMN email_verifications.expires_at IS 'Token expiration time (24 hours from creation)';
COMMENT ON COLUMN email_verifications.verified_at IS 'Time when email was verified';
COMMENT ON COLUMN email_verifications.is_verified IS 'Whether the email has been verified';

