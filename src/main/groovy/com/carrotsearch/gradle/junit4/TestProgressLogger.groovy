package com.carrotsearch.gradle.junit4

import com.carrotsearch.ant.tasks.junit4.JUnit4
import com.carrotsearch.ant.tasks.junit4.dependencies.com.google.common.eventbus.Subscribe
import com.carrotsearch.ant.tasks.junit4.events.aggregated.AggregatedStartEvent
import com.carrotsearch.ant.tasks.junit4.events.aggregated.AggregatedSuiteResultEvent
import com.carrotsearch.ant.tasks.junit4.listeners.AggregatedEventListener
import org.gradle.logging.ProgressLogger
import org.gradle.logging.ProgressLoggerFactory
import org.junit.runner.Description

import java.util.concurrent.atomic.AtomicInteger

import static com.carrotsearch.ant.tasks.junit4.FormattingUtils.formatDurationInSeconds

class TestProgressLogger implements AggregatedEventListener {

    /** Factory to build a progress logger when testing starts */
    ProgressLoggerFactory factory
    ProgressLogger progressLogger
    int totalSuites;
    AtomicInteger suitesCompleted = new AtomicInteger()
    AtomicInteger testsCompleted = new AtomicInteger()
    AtomicInteger testsFailed = new AtomicInteger()
    AtomicInteger testsIgnored = new AtomicInteger()

    @Subscribe
    void onStart(AggregatedStartEvent e) throws IOException {
        totalSuites = e.getSuiteCount();
        progressLogger = factory.newOperation(TestProgressLogger)
        progressLogger.setDescription('Randomized test runner')
        progressLogger.started()
        progressLogger.progress('Starting JUnit4 with ' + e.getSlaveCount() + ' jvms')
    }

    @Subscribe
    void onSuiteResult(AggregatedSuiteResultEvent e) throws IOException {
        final int suitesCompleted = suitesCompleted.incrementAndGet();
        final int testsCompleted = testsCompleted.addAndGet(e.getDescription().testCount())
        final int testsFailed = testsFailed.addAndGet(e.getErrorCount() + e.getFailureCount())
        final int testsIgnored = testsIgnored.addAndGet(e.getIgnoredCount())
        Description description = e.getDescription()
        String suiteName = description.getDisplayName();
        suiteName = suiteName.substring(suiteName.lastIndexOf('.') + 1);
        progressLogger.progress('Suites [' + suitesCompleted + '/' + totalSuites + '], Tests [' + testsCompleted + '|' + testsFailed + '|' + testsIgnored + '], ' + suiteName + ' on J' + e.getSlave().id + ' in ' + formatDurationInSeconds(e.getExecutionTime()))
    }

    @Override
    void setOuter(JUnit4 junit) {}
}
