package com.milaboratory.util;

public final class ProgressAndStage implements CanReportProgressAndStage {
    volatile String stage;
    volatile double progress;
    volatile boolean finished;

    public ProgressAndStage(String stage) {
        this.stage = stage;
    }

    @Override
    public String getStage() {
        return stage;
    }

    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
