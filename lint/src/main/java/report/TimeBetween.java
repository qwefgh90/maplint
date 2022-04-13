package report;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author qwefgh90
 */
public class TimeBetween {
    protected final LocalDateTime start;
    protected final LocalDateTime end;
    protected final Duration elapsedTime;

    public TimeBetween(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
        this.elapsedTime = Duration.between(start, end);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public Duration getElapsedTime() {
        return elapsedTime;
    }

    public static final class TimeBetweenBuilder {
        protected LocalDateTime start;
        protected LocalDateTime end;

        private TimeBetweenBuilder() {
        }

        public static TimeBetweenBuilder aTimeBetween() {
            return new TimeBetweenBuilder();
        }

        public TimeBetweenBuilder start(LocalDateTime start) {
            this.start = start;
            return this;
        }

        public TimeBetweenBuilder end(LocalDateTime end) {
            this.end = end;
            return this;
        }

        public boolean isComplete(){
            return start != null && end != null;
        }

        public TimeBetween build() {
            assert isComplete();
            TimeBetween timeBetween = new TimeBetween(start, end);
            return timeBetween;
        }
    }

    @Override
    public String toString() {
        return "TimeBetween{" +
                "▶ start=" + start +
                ", ⏹ end=" + end +
                ", ⌛ elapsedTime=" + (((double)elapsedTime.toNanos()) / 1_000_000_000) + "seconds" +
                '}';
    }
}
