package report;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qwefgh90
 */
public abstract class BaseErrorReport<T> {
    final List<T> errorList;
    final TimeBetween timeBetween;

    public BaseErrorReport(List<T> errorList, LocalDateTime diagnosisStart, LocalDateTime diagnosisEnd) {
        this.errorList = errorList;
        timeBetween = new TimeBetween(diagnosisStart, diagnosisEnd);
    }

    public TimeBetween getTimeBetween() {
        return timeBetween;
    }

    public LocalDateTime getDiagnosisStart() {
        return timeBetween.getStart();
    }

    public LocalDateTime getDiagnosisEnd() {
        return timeBetween.getEnd();
    }

    public Duration getElapsedTime() {
        return timeBetween.getElapsedTime();
    }

    public List<T> getErrorList() {
        return errorList;
    }

    @Override
    public String toString() {
        return timeBetween.toString();
    }
}
