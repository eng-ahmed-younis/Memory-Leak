# Android Memory Leak Playground & Reference Guide 📱 Memory Leak Detector & Catalog

An educational Android application designed to demonstrate, trigger, detect, and fix **40 common Android memory leaks** across core Android components, system services, asynchronous threading, Jetpack Compose, singletons, networking, and permission handling architecture.

Integrated with **LeakCanary** for real-time memory leak detection and heap dump analysis.

---

## 📑 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Architecture & Package Structure](#project-architecture--package-structure)
- [Memory Leak Catalog (40 Scenarios)](#memory-leak-catalog-40-scenarios)
  - [1. Core Android Components](#1-core-android-components)
  - [2. System Services](#2-system-services)
  - [3. Async & Threading](#3-async--threading)
  - [4. UI & Views](#4-ui--views)
  - [5. Singletons & Caches](#5-singletons--caches)
  - [6. Networking & APIs](#6-networking--apis)
  - [7. Jetpack Compose](#7-jetpack-compose)
- [Rotation & Leak-Free Permission Architecture](#rotation--leak-free-permission-architecture)
- [How Leak Canaries & Heap Dumps Work](#how-leak-canaries--heap-dumps-work)
- [Building & Running](#building--running)

---

## 🌟 Overview

Memory leaks in Android occur when destroyed objects (such as `Activity`, `Fragment`, or `View` instances) remain strongly referenced in memory by long-lived GC roots (like static fields, singletons, active threads, or registered callbacks).

This application provides a hands-on playground to:
1. **Trigger Memory Leaks**: Simulate realistic bad coding practices across 40 different scenarios.
2. **Detect with LeakCanary**: Inspect GC root retention paths and retained bytes in real time.
3. **Execute Solutions**: Apply industry-standard Android fixes and observe memory reclamation.

---

## 🛠 Tech Stack

- **Language**: Kotlin 1.9+
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Architecture**: Single Activity (`MainActivity`) with state-driven Compose navigation & MVVM pattern.
- **Memory Inspection**: [LeakCanary](https://square.github.io/leakcanary/) (`leakcanary.AppWatcher`, `ObjectWatcher`)
- **Concurrency**: Kotlin Coroutines (`Flow`, `StateFlow`, `SharedFlow`, `Channel`), RxJava, Executors, Threads.

---

## 📂 Project Architecture & Package Structure

```
com.example.memoryleak
├── MainActivity.kt                 # App entry point hosting MemoryLeakApp
├── model/
│   ├── LeakCategory.kt             # Enum categories for memory leaks
│   ├── MemoryLeakInfo.kt           # Data model storing title, summary, bad/fixed code
│   └── MemoryLeakRepository.kt     # Repository containing all 40 leak definitions
├── ui/
│   ├── HomeScreen.kt               # Category filter list screen
│   └── LeakDetailScreen.kt         # Detailed view with Trigger Leak & Trigger Fix buttons
├── leaks/
│   ├── LeakTriggerExecutor.kt      # Executor dispatching triggerLeak/triggerFix calls
│   ├── core/                       # Core components (Activity, Receiver, ContentObserver, etc.)
│   ├── services/                   # System Services (Sensors, Location, AlarmManager, etc.)
│   ├── async/                      # Threading (Coroutines, Handlers, Threads, RxJava, etc.)
│   ├── uiviews/                    # Views & UI (Animators, WebView, MediaPlayer, ViewPager, etc.)
│   ├── singletons/                 # Caches (Bitmap Cache, Room Observer, WebSocket, etc.)
│   ├── networking/                 # APIs (Retrofit, EventBus)
│   └── compose/                    # Jetpack Compose (remember, DisposableEffect, LaunchedEffect)
└── permission/                     # Rotation & Leak-Free Permission Architecture
    ├── PermissionActivity.kt       # Activity demonstrating permission requests
    ├── PermissionBridge.kt         # Process-wide Channel/Flow result bridge (Zero Activity context)
    ├── RotatablePermissionHelper.kt # Lifecycle-aware observer managing ActivityResultLauncher
    └── PermissionLeakDocumentation.md # Detailed architectural guide on suspendCoroutine leaks
```

---

## 📋 Memory Leak Catalog (40 Scenarios)

### 1. Core Android Components
1. **Static Reference to Activity Context**: Holding Activity context in a static object or companion object field.
2. **Unregistered BroadcastReceiver**: Registering a `BroadcastReceiver` without calling `unregisterReceiver()` in `onStop()`/`onDestroy()`.
3. **Unregistered ContentObserver**: Registering `ContentObserver` on `ContentResolver` without `unregisterContentObserver()`.
4. **Bound ServiceConnection Leak**: Calling `bindService()` without matching `unbindService()`.
5. **Fragment ViewBinding / View Leak**: Retaining `_binding` reference past Fragment `onDestroyView()`.
6. **Activity Registry Leak**: Storing `Activity` instances in a static list/registry using strong references instead of `WeakReference`.
7. **PhoneStateListener Telephony Leak**: Registering `PhoneStateListener` on `TelephonyManager` without `PhoneStateListener.LISTEN_NONE`.
8. **ClipboardManager Listener Leak**: Registering `OnPrimaryClipChangedListener` without `removePrimaryClipChangedListener()`.
9. **WindowManager Floating View Leak**: Adding floating views via `WindowManager.addView()` without `removeViewImmediate()`.
10. **SpeechRecognizer Resource Leak**: Instantiating `SpeechRecognizer` without calling `destroy()`.
11. **Camera Hardware Subsystem Leak**: Opening native `CameraDevice` sessions without calling `cameraDevice.close()`.

### 2. System Services
12. **Unregistered SensorEventListener**: Registering `SensorEventListener` with `SensorManager` without unregistering on pause.
13. **Unregistered LocationListener**: Subscribing to `LocationManager.requestLocationUpdates()` without calling `removeUpdates()`.
14. **ViewTreeObserver Listener Leak**: Adding `OnGlobalLayoutListener` without `removeOnGlobalLayoutListener()`.
15. **AlarmManager PendingIntent Leak**: Setting repeating alarms with `PendingIntent` holding Context without calling `alarmManager.cancel()`.

### 3. Async & Threading
16. **Non-Static Handler / Runnable Leak**: Posting delayed messages on a non-static Handler without `removeCallbacksAndMessages(null)`.
17. **Unmanaged Thread Leak**: Starting raw `Thread` instances that run continuously captured in outer Activity scope.
18. **GlobalScope Coroutine Leak**: Launching long-running jobs in `GlobalScope` instead of `lifecycleScope`/`viewModelScope`.
19. **Hot Flow Uncancelled Collection**: Collecting hot `StateFlow`/`SharedFlow` in unmanaged coroutine scopes without `repeatOnLifecycle`.
20. **ExecutorService Thread Pool Leak**: Instantiating `Executors.newFixedThreadPool()` without calling `shutdown()`/`shutdownNow()`.
21. **LiveData observeForever Leak**: Calling `liveData.observeForever()` without `removeObserver()`.
22. **RxJava / Reactive Stream Disposable Leak**: Subscribing to `Observable.interval()` without calling `disposable.dispose()`.
23. **AsyncTask Non-Static Inner Class Leak**: Legacy `AsyncTask` inner classes holding implicit references to dead Activity hosts.

### 4. UI & Views
24. **Static View Reference Leak**: Storing a `View` (which retains host `Context`) in a static field.
25. **Infinite ValueAnimator Leak**: Running infinite `ValueAnimator` loops without calling `cancel()` in `onDestroy()`.
26. **TextWatcher Listener Leak**: Adding `TextWatcher` to `EditText` without calling `removeTextChangedListener()`.
27. **Android WebView Memory Leak**: Instantiating `WebView` without detaching from parent layout and calling `webView.destroy()`.
28. **MediaPlayer Native Resource Leak**: Allocating native `MediaPlayer` hardware audio drivers without calling `player.release()`.
29. **Custom View Callback Leak**: Custom views retaining strong listener callbacks without clearing them in `onDetachedFromWindow()`.
30. **ViewPager View / Fragment Cache Leak**: Caching instantiated View references inside adapter lists.
31. **Continuous Lottie Vector Animation Leak**: Continuous Lottie animations running frame callbacks without `cancelAnimation()`.

### 5. Singletons & Caches
32. **Singleton Activity Context Leak**: Passing Activity context into Singleton constructors instead of `context.applicationContext`.
33. **Unbounded Static Bitmap Cache**: Storing Bitmaps in static maps without `LruCache` or memory bounds.
34. **Room Database InvalidationTracker Leak**: Registering `InvalidationTracker.Observer` with Room DB singleton without `removeObserver()`.
35. **Persistent WebSocket Connection Leak**: Background WebSocket connections holding UI callbacks without calling `webSocket.close()`.

### 6. Networking & APIs
36. **Async Network Callback Leak**: Asynchronous HTTP callbacks executing on background threads holding outer Activity scope.
37. **EventBus Listener Leak**: Calling `EventBus.getDefault().register(this)` without matching `unregister(this)`.

### 7. Jetpack Compose
38. **Compose remember { } Context Capture**: Capturing `Activity` context inside `remember { context }` instead of using `LocalContext.current`.
39. **DisposableEffect Missing onDispose Cleanup**: Registering event listeners in `DisposableEffect` with an empty `onDispose { }` block.
40. **LaunchedEffect Key State Capture Leak**: Long-running `LaunchedEffect(Unit)` loops capturing stale variables without `rememberUpdatedState`.

---

## 🔄 Rotation & Leak-Free Permission Architecture

The `com.example.memoryleak.permission` package demonstrates how to safely handle runtime permissions across Activity screen rotations without memory leaks and without infinite dialog re-spawning bugs:

### Key Components:
- **`PermissionBridge`**: A process-wide singleton using a Kotlin `Channel`/`Flow` that holds **zero** `Activity` or `Context` references.
- **`RotatablePermissionHelper`**: A `DefaultLifecycleObserver` attached to `ComponentActivity` that registers `ActivityResultLauncher` in `onCreate()` and nullifies references in `onDestroy()`.
- **`PermissionActivity`**: Executable sample activity demonstrating sequential async permission requests.

---

## 🔍 How LeakCanary Works

When a leak is triggered in this app, LeakCanary tracks object instances using `AppWatcher.objectWatcher.expectWeaklyReachable(target, reason)`.

When an Activity is destroyed:
1. LeakCanary waits 5 seconds and forces a Garbage Collection.
2. If the retained object is still reachable via a **GC Root** chain, LeakCanary dumps the heap (`.hprof`) and prints the reference trace.

---

## 🚀 Building & Running

### Requirements
- Android Studio Ladybug / Jellyfish (2024.1+) or newer
- JDK 17
- Android SDK 34+

### Build via Command Line
```bash
# Clone the repository
git clone https://github.com/example/memory-leak-android.git
cd memory-leak-android

# Build Debug APK
./gradlew assembleDebug
```

---
*Created as a comprehensive reference guide for Android developers to understand and prevent memory leaks.*
