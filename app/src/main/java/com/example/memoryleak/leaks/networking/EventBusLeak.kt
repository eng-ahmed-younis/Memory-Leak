package com.example.memoryleak.leaks.networking

interface EventSubscriber {
    fun onEvent(event: String)
}

object EventBusLeak {
    // LEAK: Singleton Event Bus retaining strong references to listeners/subscribers without unregister.
    private val subscribers = mutableListOf<EventSubscriber>()

    fun triggerLeak(subscriber: EventSubscriber): String {
        subscribers.add(subscriber)
        return "Subscribed listener '${subscriber.javaClass.simpleName}' to Singleton EventBus without calling unregister."
    }

    fun triggerFix(subscriber: EventSubscriber): String {
        subscribers.remove(subscriber)
        return "Fixed! Unregistered listener from EventBus in onDestroy()/onCleared()."
    }
}
