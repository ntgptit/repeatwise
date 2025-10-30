package com.repeatwise.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * SRS Box Interval Utility
 *
 * Requirements:
 * - UC-024: Rate Card
 * - Provides box interval calculations for SRS system
 *
 * Box Intervals (MVP):
 * - Box 1: 1 minute (0 days - immediate)
 * - Box 2: 10 minutes (0 days - same session)
 * - Box 3: 3 days
 * - Box 4: 7 days
 * - Box 5: 14 days
 * - Box 6: 30 days
 * - Box 7: 60 days
 *
 * @author RepeatWise Team
 */
public final class SrsBoxIntervalUtil {

    private static final Map<Integer, Integer> BOX_INTERVALS = new HashMap<>();

    static {
        BOX_INTERVALS.put(1, 0);  // 1 minute (immediate)
        BOX_INTERVALS.put(2, 0);  // 10 minutes (same session)
        BOX_INTERVALS.put(3, 3);
        BOX_INTERVALS.put(4, 7);
        BOX_INTERVALS.put(5, 14);
        BOX_INTERVALS.put(6, 30);
        BOX_INTERVALS.put(7, 60);
    }

    private SrsBoxIntervalUtil() {
        // Utility class - private constructor
    }

    /**
     * Get interval days for a box
     *
     * @param box Box number (1-7)
     * @return Interval days
     */
    public static int getIntervalDays(final int box) {
        if (box < 1 || box > 7) {
            throw new IllegalArgumentException("Box must be between 1 and 7");
        }
        return BOX_INTERVALS.getOrDefault(box, 0);
    }

    /**
     * Calculate due date from box
     *
     * @param box Box number (1-7)
     * @return Due date (today + interval days)
     */
    public static LocalDate calculateDueDate(final int box) {
        final int intervalDays = getIntervalDays(box);
        if (intervalDays == 0) {
            // Box 1 or 2: due today (immediate)
            return LocalDate.now();
        }
        return LocalDate.now().plusDays(intervalDays);
    }

    /**
     * Calculate due date with hard penalty (70% of interval)
     *
     * @param box Current box
     * @return Due date with penalty
     */
    public static LocalDate calculateDueDateWithHardPenalty(final int box) {
        final int intervalDays = getIntervalDays(box);
        if (intervalDays == 0) {
            return LocalDate.now();
        }
        final int penaltyDays = (int) Math.ceil(intervalDays * 0.7);
        return LocalDate.now().plusDays(penaltyDays);
    }
}

