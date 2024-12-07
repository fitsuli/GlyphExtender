package com.fitsuli.glyphextender.notification

object NotificationProcessorRegistry {
    private val processors = mutableListOf<NotificationProcessor>()

    fun register(processor: NotificationProcessor) {
        processors.add(processor)
    }

    fun hasActiveHigherPriorityProcessor(current: NotificationProcessor): Boolean {
        return processors.any {
            it.priority > current.priority && it.isActive
        }
    }

    fun getActiveProcessor(): NotificationProcessor? {
        return processors.filter { it.isActive }
            .maxByOrNull { it.priority }
    }
}
