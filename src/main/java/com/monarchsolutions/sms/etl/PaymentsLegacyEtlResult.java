package com.monarchsolutions.sms.etl;

public class PaymentsLegacyEtlResult {

    private final long lastLegacyPkBefore;
    private final long lastLegacyPkAfter;
    private final long rowsRead;
    private final long rowsInserted;
    private final long rowsSkipped;
    private final long issuesCreated;
    private final long durationMs;

    public PaymentsLegacyEtlResult(
            long lastLegacyPkBefore,
            long lastLegacyPkAfter,
            long rowsRead,
            long rowsInserted,
            long rowsSkipped,
            long issuesCreated,
            long durationMs
    ) {
        this.lastLegacyPkBefore = lastLegacyPkBefore;
        this.lastLegacyPkAfter = lastLegacyPkAfter;
        this.rowsRead = rowsRead;
        this.rowsInserted = rowsInserted;
        this.rowsSkipped = rowsSkipped;
        this.issuesCreated = issuesCreated;
        this.durationMs = durationMs;
    }

    public long getLastLegacyPkBefore() {
        return lastLegacyPkBefore;
    }

    public long getLastLegacyPkAfter() {
        return lastLegacyPkAfter;
    }

    public long getRowsRead() {
        return rowsRead;
    }

    public long getRowsInserted() {
        return rowsInserted;
    }

    public long getRowsSkipped() {
        return rowsSkipped;
    }

    public long getIssuesCreated() {
        return issuesCreated;
    }

    public long getDurationMs() {
        return durationMs;
    }
}
