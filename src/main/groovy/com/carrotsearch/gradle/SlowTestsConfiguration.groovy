package com.carrotsearch.gradle

class SlowTestsConfiguration {
    int heartbeat = 0
    int summarySize = 0

    void heartbeat(int heartbeat) {
        this.heartbeat = heartbeat
    }

    void summarySize(int summarySize) {
        this.summarySize = summarySize
    }
}
