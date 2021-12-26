package com.example.newversiontest.debug;

public class TestTiming {
    long taskId;
    long startTime;
    long duration;

    public TestTiming(long taskId, long startTime, long duration) {
        this.taskId = taskId;
        this.startTime = startTime;
        this.duration = duration;
    }
}
