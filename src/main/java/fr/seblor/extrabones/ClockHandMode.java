package fr.seblor.extrabones;

import java.time.LocalTime;
import java.time.ZoneId;

public enum ClockHandMode {

    // ── Hour hand ─────────────────────────────────────────────────────────────

    /** Bone prefix {@code hour_} — snaps once per hour. */
    HOUR {
        @Override
        public long cacheKey() { return now().getHour(); }

        @Override
        public float getDegrees() {
            return (now().getHour() % 12) / 12f * 360f;
        }
    },

    /** Bone prefix {@code hour_s_} — progresses continuously through minutes. */
    HOUR_SMOOTH {
        @Override
        public long cacheKey() {
            LocalTime t = now();
            return (long) t.getHour() * 60 + t.getMinute();
        }

        @Override
        public float getDegrees() {
            LocalTime t = now();
            return ((t.getHour() % 12) * 60 + t.getMinute()) / 720f * 360f;
        }
    },

    /** Bone prefix {@code hour_ss_} — precise to the millisecond. */
    HOUR_SUPER_SMOOTH {
        @Override
        public long cacheKey() { return System.currentTimeMillis(); }

        @Override
        public float getDegrees() {
            LocalTime t = now();
            long ms = ((long) (t.getHour() % 12) * 3600 + t.getMinute() * 60L + t.getSecond()) * 1000L
                    + (System.currentTimeMillis() % 1000);
            return ms / 43200000f * 360f;
        }
    },

    // ── Minute hand ───────────────────────────────────────────────────────────

    /** Bone prefix {@code minute_} — snaps once per minute. */
    MINUTE {
        @Override
        public long cacheKey() { return now().getMinute(); }

        @Override
        public float getDegrees() {
            return now().getMinute() / 60f * 360f;
        }
    },

    /** Bone prefix {@code minute_s_} — progresses continuously through seconds. */
    MINUTE_SMOOTH {
        @Override
        public long cacheKey() {
            LocalTime t = now();
            return (long) t.getMinute() * 60 + t.getSecond();
        }

        @Override
        public float getDegrees() {
            LocalTime t = now();
            return (t.getMinute() * 60 + t.getSecond()) / 3600f * 360f;
        }
    },

    /** Bone prefix {@code minute_ss_} — precise to the millisecond. */
    MINUTE_SUPER_SMOOTH {
        @Override
        public long cacheKey() { return System.currentTimeMillis(); }

        @Override
        public float getDegrees() {
            LocalTime t = now();
            long ms = (t.getMinute() * 60L + t.getSecond()) * 1000L
                    + (System.currentTimeMillis() % 1000);
            return ms / 3600000f * 360f;
        }
    },

    // ── Second hand ───────────────────────────────────────────────────────────

    /** Bone prefix {@code second_} — snaps once per second. */
    SECOND {
        @Override
        public long cacheKey() { return now().getSecond(); }

        @Override
        public float getDegrees() {
            return now().getSecond() / 60f * 360f;
        }
    },

    /** Bone prefix {@code second_s_} — precise to the millisecond. */
    SECOND_SMOOTH {
        @Override
        public long cacheKey() { return System.currentTimeMillis(); }

        @Override
        public float getDegrees() {
            LocalTime t = now();
            long ms = t.getSecond() * 1000L + (System.currentTimeMillis() % 1000);
            return ms / 60000f * 360f;
        }
    },

    /** Bone prefix {@code second_ss_} — same as SECOND_SMOOTH (ms is the finest unit). */
    SECOND_SUPER_SMOOTH {
        @Override
        public long cacheKey() { return System.currentTimeMillis(); }

        @Override
        public float getDegrees() {
            LocalTime t = now();
            long ms = t.getSecond() * 1000L + (System.currentTimeMillis() % 1000);
            return ms / 60000f * 360f;
        }
    };

    /**
     * A value that changes only when this mode's angle needs to be recomputed.
     * The behavior caches the last key and skips quaternion creation when it matches.
     * Millisecond-precision modes return {@link System#currentTimeMillis()} so they
     * are always recomputed (ticks are ~50 ms apart).
     */
    public abstract long cacheKey();

    /** Returns the clockwise rotation in degrees for the current moment. */
    public abstract float getDegrees();

    /**
     * Resolves the mode from a bone name by checking prefixes longest-first so
     * {@code hour_ss_} matches before {@code hour_s_} and {@code hour_}.
     * Returns {@code null} if the bone carries no clock-hand prefix.
     */
    /** The timezone used by all clock-hand modes. Set by {@link ExtraBoneBehaviors} from config. */
    static ZoneId zoneId = ZoneId.systemDefault();

    private static LocalTime now() {
        return LocalTime.now(zoneId);
    }

    public static ClockHandMode fromBoneName(String name) {
        if (name.startsWith("hour_ss_"))    return HOUR_SUPER_SMOOTH;
        if (name.startsWith("hour_s_"))     return HOUR_SMOOTH;
        if (name.startsWith("hour_"))       return HOUR;
        if (name.startsWith("minute_ss_"))  return MINUTE_SUPER_SMOOTH;
        if (name.startsWith("minute_s_"))   return MINUTE_SMOOTH;
        if (name.startsWith("minute_"))     return MINUTE;
        if (name.startsWith("second_ss_"))  return SECOND_SUPER_SMOOTH;
        if (name.startsWith("second_s_"))   return SECOND_SMOOTH;
        if (name.startsWith("second_"))     return SECOND;
        return null;
    }
}
