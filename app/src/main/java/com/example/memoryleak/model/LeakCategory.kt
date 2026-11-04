package com.example.memoryleak.model

enum class LeakCategory(val displayName: String, val description: String) {
    CORE_COMPONENTS("Android Core Components", "Activity, Fragment, Service, BroadcastReceiver, ContentObserver leaks"),
    SYSTEM_SERVICES("System Services & Listeners", "SensorManager, LocationManager, AlarmManager, ViewTreeObserver leaks"),
    ASYNC_THREADING("Async, Handlers & Coroutines", "Handlers, Threads, Coroutine Scopes, Flow collections"),
    UI_VIEWS("UI Controls & Views", "Static Views, WebViews, Animators, TextWatchers, MediaPlayer"),
    SINGLETONS_CACHES("Singletons & Static References", "Static Context, Static Caches, Bitmaps"),
    NETWORKING_APIS("Networking & Async APIs", "Retrofit/OkHttp callbacks, EventBus, WebSocket listeners"),
    JETPACK_COMPOSE("Jetpack Compose & Effects", "Remember, DisposableEffect without onDispose, Scope leaks")
}
