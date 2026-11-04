# Runtime Permission Requests Across Activity Rotation

## Problem Description

When wrapping callback-based APIs (such as runtime permission requests using `ActivityResultLauncher`) inside `suspendCoroutine`, a memory leak occurs during screen rotation.

### 1. How `suspendCoroutine` Captures Context
When using `suspendCoroutine`, the Kotlin compiler generates a `Continuation` object to suspend execution:
- The lambda inside `suspendCoroutine` captures variables in its closure, including `ActivityResultLauncher`.
- `ActivityResultLauncher` holds an internal strong reference to its host `Activity`.
- The `Continuation` object remains alive in the Coroutine framework while waiting for user interaction.

### 2. The Rotation Leak
When the user rotates the screen while the system permission dialog is displayed:
1. Android destroys the old `Activity` instance.
2. The active `Continuation` holds: `Continuation -> Lambda Closure -> Launcher -> Old Activity`.
3. The Garbage Collector cannot reclaim the old `Activity`, causing a **Memory Leak**.

### 3. The Re-Launch Infinite Loop Bug
Attempting to fix this by re-calling `launcher.launch(permission)` inside `onCreate()` when rotation happens triggers an **infinite dialog spawning loop**, because Android's OS already re-renders system permission dialogs automatically.

---

## The Solution

To solve both memory leaks and rotation bugs, we decouple state management from the Activity lifecycle using **`PermissionBridge`** and **`RotatablePermissionHelper`**.

### Architecture Workflow

```
[ Coroutine Caller ] ---> calls requestPermission()
                               │
                               ▼
                   [ RotatablePermissionHelper ] (Lifecycle Observer)
                               │ (launches UI)
                               ▼
                   [ System Permission Dialog ]
                               │ (user responds)
                               ▼
                   [ Activity Result Callback ]
                               │
                               ▼
                    [ PermissionBridge ] (Global Channel/Flow, NO Activity Context)
                               │
                               ▼
                     emits result over Flow
```

---

## Code Implementation

### 1. `PermissionBridge.kt` (State Channel / Flow)

```kotlin
package com.example.memoryleak.permission

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object PermissionBridge {
    private val resultChannel = Channel<Boolean>(Channel.CONFLATED)
    val permissionResults = resultChannel.receiveAsFlow()

    var isRequestActive: Boolean = false
        private set

    fun markRequestStarted() {
        isRequestActive = true
    }

    fun sendResult(isGranted: Boolean) {
        isRequestActive = false
        resultChannel.trySend(isGranted)
    }
}
```

### 2. `RotatablePermissionHelper.kt` (Lifecycle-Aware UI Attachment Layer)

```kotlin
package com.example.memoryleak.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.first

class RotatablePermissionHelper(
    private val activity: ComponentActivity
) : DefaultLifecycleObserver {

    private var launcher: ActivityResultLauncher<String>? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        // Register early in onCreate as required by Android contract
        launcher = activity.activityResultRegistry.register(
            "rotatable_permission_key",
            activity,
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            PermissionBridge.sendResult(isGranted)
        }
    }

    suspend fun requestPermission(permission: String): Boolean {
        if (PermissionBridge.isRequestActive) {
            return PermissionBridge.permissionResults.first()
        }

        PermissionBridge.markRequestStarted()
        launcher?.launch(permission) ?: return false

        return PermissionBridge.permissionResults.first()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        // CRITICAL: Sever launcher and lifecycle references to prevent memory leaks on Activity destroy
        launcher = null
        activity.lifecycle.removeObserver(this)
    }
}
```

---

## Why This Fix Works

1. **Zero Memory Leaks:** `PermissionBridge` holds no `Activity` or `Context` references. `RotatablePermissionHelper` strips `launcher` and lifecycle observers in `onDestroy()`.
2. **No Infinite Dialog Loop:** Avoids re-launching permission dialogs in `onCreate()`.
3. **Sequential Coroutine Execution:** Calling `.first()` on `permissionResults` suspends the calling coroutine cleanly without trapping Activity references in a `Continuation`.
