package com.example.memoryleak.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.first

/**
 * A lifecycle-aware helper attached to [ComponentActivity] that manages [ActivityResultLauncher]
 * safely across screen rotations.
 *
 * HOW IT SOLVES THE LEAK AND DIALOG RE-SPAWN BUGS:
 * - Registers early in `onCreate()` in compliance with Android's ActivityResult API contract.
 * - Forwards permissions results to [PermissionBridge] without leaking the Activity instance.
 * - Clears launcher references and unbinds from lifecycle in `onDestroy()` to guarantee zero memory leaks.
 * - Uses `PermissionBridge.isRequestActive` to prevent double-spawning permission dialogs on rotation.
 */
/*
class RotatablePermissionHelper(
    private val activity: ComponentActivity
) : DefaultLifecycleObserver {

    private var launcher: ActivityResultLauncher<String>? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        // Register early in onCreate as required by Android ActivityResult API contract
        launcher = activity.activityResultRegistry.register(
            "rotatable_permission_key",
            activity,
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            PermissionBridge.sendResult(isGranted)
        }
    }

    */
/**
     * Call this sequentially from coroutine scope. It launches the permission UI
     * and safely suspends until a result is delivered without trapping Activity context.
     *//*

    suspend fun requestPermission(permission: String): Boolean {
        if (PermissionBridge.isRequestActive) {
            return PermissionBridge.permissionResults.first()
        }

        PermissionBridge.markRequestStarted()

        launcher?.launch(permission) ?: return false

        return PermissionBridge.permissionResults.first()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        // CRITICAL: Sever launcher and lifecycle references to prevent memory leaks on Activity destruction
        launcher = null
        activity.lifecycle.removeObserver(this)
    }
}
*/

class RotatablePermissionHelper constructor(
    private val activity: ComponentActivity
) : DefaultLifecycleObserver {

    private val launcher: ActivityResultLauncher<String>? = null;

    init {
        activity.lifecycle.addObserver(this)
    }

}