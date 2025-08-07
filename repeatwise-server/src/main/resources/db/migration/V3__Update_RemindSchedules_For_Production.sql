-- ==============================================
-- UPDATE REMIND_SCHEDULES FOR PRODUCTION WORKFLOW
-- ==============================================

-- Add new columns for production workflow
ALTER TABLE remind_schedules 
ADD COLUMN sent_at TIMESTAMPTZ,
ADD COLUMN completed_at TIMESTAMPTZ,
ADD COLUMN review_score INTEGER,
ADD COLUMN review_notes TEXT;

-- Add indexes for production queries
CREATE INDEX idx_remind_schedules_status_date ON remind_schedules(status, scheduled_date, deleted_at);
CREATE INDEX idx_remind_schedules_user_status ON remind_schedules(user_id, status, deleted_at);
CREATE INDEX idx_remind_schedules_sent_at ON remind_schedules(sent_at, deleted_at);
CREATE INDEX idx_remind_schedules_completed_at ON remind_schedules(completed_at, deleted_at);

-- Add comments for new columns
COMMENT ON COLUMN remind_schedules.sent_at IS 'Timestamp when notification was sent';
COMMENT ON COLUMN remind_schedules.completed_at IS 'Timestamp when user completed the review';
COMMENT ON COLUMN remind_schedules.review_score IS 'Score from 0-100 when user completes review';
COMMENT ON COLUMN remind_schedules.review_notes IS 'Optional notes from user about the review';

-- Update existing reminders to have proper status if needed
UPDATE remind_schedules 
SET status = 'PENDING' 
WHERE status IS NULL AND deleted_at IS NULL;

-- Create a function to get next available date for a user
CREATE OR REPLACE FUNCTION get_next_available_date_for_user(user_uuid UUID)
RETURNS DATE AS $$
DECLARE
    next_date DATE := CURRENT_DATE;
    reminder_count INTEGER;
BEGIN
    -- Find the next date with less than 3 reminders
    LOOP
        SELECT COUNT(*) INTO reminder_count
        FROM remind_schedules
        WHERE user_id = user_uuid 
        AND scheduled_date = next_date 
        AND deleted_at IS NULL;
        
        IF reminder_count < 3 THEN
            RETURN next_date;
        END IF;
        
        next_date := next_date + INTERVAL '1 day';
        
        -- Safety check to prevent infinite loop
        IF next_date > CURRENT_DATE + INTERVAL '30 days' THEN
            RETURN next_date;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Create a function to auto-reschedule overflow reminders
CREATE OR REPLACE FUNCTION auto_reschedule_overflow_reminders(target_date DATE)
RETURNS INTEGER AS $$
DECLARE
    reminder_record RECORD;
    new_date DATE;
    rescheduled_count INTEGER := 0;
BEGIN
    -- Find reminders that exceed daily limit
    FOR reminder_record IN 
        SELECT rs.*, ROW_NUMBER() OVER (PARTITION BY rs.user_id ORDER BY rs.created_at) as rn
        FROM remind_schedules rs
        WHERE rs.scheduled_date = target_date 
        AND rs.status = 'PENDING' 
        AND rs.deleted_at IS NULL
    LOOP
        -- If this is the 4th or later reminder for the user on this date
        IF reminder_record.rn > 3 THEN
            -- Find next available date for this user
            new_date := get_next_available_date_for_user(reminder_record.user_id);
            
            -- Update the reminder
            UPDATE remind_schedules 
            SET scheduled_date = new_date,
                status = 'RESCHEDULED',
                rescheduled_at = NOW(),
                reschedule_reason = 'Auto-rescheduled due to daily limit overflow',
                updated_at = NOW()
            WHERE id = reminder_record.id;
            
            rescheduled_count := rescheduled_count + 1;
        END IF;
    END LOOP;
    
    RETURN rescheduled_count;
END;
$$ LANGUAGE plpgsql; 