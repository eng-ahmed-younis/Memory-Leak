package com.example.memoryleak.leaks

import android.app.Activity
import android.widget.EditText
import android.widget.TextView
import com.example.memoryleak.leaks.async.AsyncTaskInnerClassLeak
import com.example.memoryleak.leaks.async.CoroutineGlobalScopeLeak
import com.example.memoryleak.leaks.async.ExecutorServiceLeak
import com.example.memoryleak.leaks.async.FlowListenerLeak
import com.example.memoryleak.leaks.async.InnerClassHandlerLeak
import com.example.memoryleak.leaks.async.LiveDataObserveForeverLeak
import com.example.memoryleak.leaks.async.RxJavaDisposableLeak
import com.example.memoryleak.leaks.async.ThreadLeak
import com.example.memoryleak.leaks.compose.ComposeDisposableEffectLeak
import com.example.memoryleak.leaks.compose.ComposeLaunchedEffectLeak
import com.example.memoryleak.leaks.compose.ComposeRememberLeak
import com.example.memoryleak.leaks.core.ActivityRegistryLeak
import com.example.memoryleak.leaks.core.CameraHardwareLeak
import com.example.memoryleak.leaks.core.ClipboardListenerLeak
import com.example.memoryleak.leaks.core.ContentObserverLeak
import com.example.memoryleak.leaks.core.FragmentViewBindingLeak
import com.example.memoryleak.leaks.core.ServiceConnectionLeak
import com.example.memoryleak.leaks.core.SpeechRecognizerLeak
import com.example.memoryleak.leaks.core.StaticActivityLeak
import com.example.memoryleak.leaks.core.TelephonyListenerLeak
import com.example.memoryleak.leaks.core.UnregisteredReceiverLeak
import com.example.memoryleak.leaks.core.WindowManagerViewLeak
import com.example.memoryleak.leaks.networking.EventBusLeak
import com.example.memoryleak.leaks.networking.EventSubscriber
import com.example.memoryleak.leaks.networking.NetworkCallback
import com.example.memoryleak.leaks.networking.RetrofitCallbackLeak
import com.example.memoryleak.leaks.services.AlarmManagerLeak
import com.example.memoryleak.leaks.services.LocationListenerLeak
import com.example.memoryleak.leaks.services.UnregisteredSensorLeak
import com.example.memoryleak.leaks.services.ViewTreeObserverLeak
import com.example.memoryleak.leaks.singletons.BitmapCacheLeak
import com.example.memoryleak.leaks.singletons.RoomObserverLeak
import com.example.memoryleak.leaks.singletons.SingletonContextLeak
import com.example.memoryleak.leaks.singletons.WebSocketListenerLeak
import com.example.memoryleak.leaks.uiviews.AnimatorLeak
import com.example.memoryleak.leaks.uiviews.CustomViewListenerLeak
import com.example.memoryleak.leaks.uiviews.LottieAnimationLeak
import com.example.memoryleak.leaks.uiviews.MediaPlayerLeak
import com.example.memoryleak.leaks.uiviews.StaticViewLeak
import com.example.memoryleak.leaks.uiviews.TextWatcherLeak
import com.example.memoryleak.leaks.uiviews.ViewPagerFragmentLeak
import com.example.memoryleak.leaks.uiviews.WebViewLeak
import leakcanary.AppWatcher
import java.util.UUID

object LeakTriggerExecutor {

    // Retained leak instances for toggling fix
    private var activeReceiverLeak: UnregisteredReceiverLeak? = null
    private var activeObserverLeak: ContentObserverLeak? = null
    private var activeServiceConnectionLeak: ServiceConnectionLeak? = null
    private var activeTelephonyLeak: TelephonyListenerLeak? = null
    private var activeClipboardLeak: ClipboardListenerLeak? = null
    private var activeWindowManagerLeak: WindowManagerViewLeak? = null
    private var activeSpeechLeak: SpeechRecognizerLeak? = null
    private var activeCameraLeak: CameraHardwareLeak? = null
    private var activeSensorLeak: UnregisteredSensorLeak? = null
    private var activeLocationLeak: LocationListenerLeak? = null
    private var activeViewTreeObserverLeak: ViewTreeObserverLeak? = null
    private var activeAlarmLeak: AlarmManagerLeak? = null
    private var activeHandlerLeak: InnerClassHandlerLeak? = null
    private var activeThreadLeak: ThreadLeak? = null
    private var activeCoroutineLeak: CoroutineGlobalScopeLeak? = null
    private var activeFlowLeak: FlowListenerLeak? = null
    private var activeExecutorLeak: ExecutorServiceLeak? = null
    private var activeLiveDataLeak: LiveDataObserveForeverLeak? = null
    private var activeRxJavaLeak: RxJavaDisposableLeak? = null
    private var activeAsyncTaskLeak: AsyncTaskInnerClassLeak? = null
    private var activeAnimatorLeak: AnimatorLeak? = null
    private var activeTextWatcherLeak: TextWatcherLeak? = null
    private var activeWebViewLeak: WebViewLeak? = null
    private var activeMediaPlayerLeak: MediaPlayerLeak? = null
    private var activeCustomViewLeak: CustomViewListenerLeak? = null
    private var activeViewPagerLeak: ViewPagerFragmentLeak? = null
    private var activeLottieLeak: LottieAnimationLeak? = null
    private var activeRoomLeak: RoomObserverLeak? = null
    private var activeWebSocketLeak: WebSocketListenerLeak? = null
    private var activeRetrofitLeak: RetrofitCallbackLeak? = null
    private var activeEventSubscriber: EventSubscriber? = null

    private fun watchWithLeakCanary(target: Any, reason: String) {
        try {
            AppWatcher.objectWatcher.expectWeaklyReachable(target, reason)
        } catch (_: Throwable) {
            // LeakCanary watch fallback
        }
    }

    fun triggerLeak(id: String, activity: Activity): String {
        // Explicitly register activity with LeakCanary watcher for instant detection
        watchWithLeakCanary(activity, "Memory Leak: $id")
        return try {
            when (id) {
                "static_activity" -> StaticActivityLeak.triggerLeak(activity)
                "unregistered_receiver" -> {
                    activeReceiverLeak = UnregisteredReceiverLeak(activity)
                    activeReceiverLeak!!.triggerLeak()
                }
                "content_observer" -> {
                    activeObserverLeak = ContentObserverLeak(activity)
                    activeObserverLeak!!.triggerLeak()
                }
                "service_connection" -> {
                    activeServiceConnectionLeak = ServiceConnectionLeak(activity)
                    activeServiceConnectionLeak!!.triggerLeak()
                }
                "fragment_view_binding" -> {
                    val dummyView = TextView(activity)
                    FragmentViewBindingLeak().triggerLeak(dummyView)
                }
                "activity_registry" -> ActivityRegistryLeak.triggerLeak(activity)
                "telephony_listener" -> {
                    activeTelephonyLeak = TelephonyListenerLeak(activity)
                    activeTelephonyLeak!!.triggerLeak()
                }
                "clipboard_listener" -> {
                    activeClipboardLeak = ClipboardListenerLeak(activity)
                    activeClipboardLeak!!.triggerLeak()
                }
                "window_manager_view" -> {
                    activeWindowManagerLeak = WindowManagerViewLeak(activity)
                    activeWindowManagerLeak!!.triggerLeak()
                }
                "speech_recognizer" -> {
                    activeSpeechLeak = SpeechRecognizerLeak(activity)
                    activeSpeechLeak!!.triggerLeak()
                }
                "camera_hardware" -> {
                    activeCameraLeak = CameraHardwareLeak(activity)
                    activeCameraLeak!!.triggerLeak()
                }
                "unregistered_sensor" -> {
                    activeSensorLeak = UnregisteredSensorLeak(activity)
                    activeSensorLeak!!.triggerLeak()
                }
                "location_listener" -> {
                    activeLocationLeak = LocationListenerLeak(activity)
                    activeLocationLeak!!.triggerLeak()
                }
                "view_tree_observer" -> {
                    activeViewTreeObserverLeak = ViewTreeObserverLeak()
                    val dummyView = TextView(activity)
                    activeViewTreeObserverLeak!!.triggerLeak(dummyView)
                }
                "alarm_manager" -> {
                    activeAlarmLeak = AlarmManagerLeak(activity)
                    activeAlarmLeak!!.triggerLeak()
                }
                "inner_class_handler" -> {
                    activeHandlerLeak = InnerClassHandlerLeak()
                    activeHandlerLeak!!.triggerLeak()
                }
                "thread_leak" -> {
                    activeThreadLeak = ThreadLeak()
                    activeThreadLeak!!.triggerLeak()
                }
                "coroutine_global_scope" -> {
                    activeCoroutineLeak = CoroutineGlobalScopeLeak()
                    activeCoroutineLeak!!.triggerLeak()
                }
                "flow_listener" -> {
                    activeFlowLeak = FlowListenerLeak()
                    activeFlowLeak!!.triggerLeak()
                }
                "executor_service" -> {
                    activeExecutorLeak = ExecutorServiceLeak()
                    activeExecutorLeak!!.triggerLeak()
                }
                "live_data_observe_forever" -> {
                    activeLiveDataLeak = LiveDataObserveForeverLeak()
                    activeLiveDataLeak!!.triggerLeak()
                }
                "rxjava_disposable" -> {
                    activeRxJavaLeak = RxJavaDisposableLeak()
                    activeRxJavaLeak!!.triggerLeak()
                }
                "async_task_inner" -> {
                    activeAsyncTaskLeak = AsyncTaskInnerClassLeak()
                    activeAsyncTaskLeak!!.triggerLeak()
                }
                "static_view" -> StaticViewLeak.triggerLeak(activity)
                "animator_leak" -> {
                    activeAnimatorLeak = AnimatorLeak()
                    activeAnimatorLeak!!.triggerLeak()
                }
                "text_watcher" -> {
                    activeTextWatcherLeak = TextWatcherLeak()
                    val dummyEditText = EditText(activity)
                    activeTextWatcherLeak!!.triggerLeak(dummyEditText)
                }
                "web_view_leak" -> {
                    activeWebViewLeak = WebViewLeak(activity)
                    activeWebViewLeak!!.triggerLeak()
                }
                "media_player_leak" -> {
                    activeMediaPlayerLeak = MediaPlayerLeak(activity)
                    activeMediaPlayerLeak!!.triggerLeak()
                }
                "custom_view_listener" -> {
                    activeCustomViewLeak = CustomViewListenerLeak(activity)
                    activeCustomViewLeak!!.triggerLeak()
                }
                "view_pager_fragment" -> {
                    activeViewPagerLeak = ViewPagerFragmentLeak()
                    val dummyView = TextView(activity)
                    activeViewPagerLeak!!.triggerLeak(dummyView)
                }
                "lottie_animation" -> {
                    activeLottieLeak = LottieAnimationLeak()
                    activeLottieLeak!!.triggerLeak()
                }
                "singleton_context" -> SingletonContextLeak.triggerLeak(activity)
                "bitmap_cache" -> BitmapCacheLeak.triggerLeak(UUID.randomUUID().toString())
                "room_observer" -> {
                    activeRoomLeak = RoomObserverLeak()
                    activeRoomLeak!!.triggerLeak()
                }
                "web_socket_listener" -> {
                    activeWebSocketLeak = WebSocketListenerLeak()
                    activeWebSocketLeak!!.triggerLeak()
                }
                "retrofit_callback" -> {
                    activeRetrofitLeak = RetrofitCallbackLeak()
                    val callback = object : NetworkCallback {
                        override fun onSuccess(data: String) {}
                        override fun onError(error: String) {}
                    }
                    activeRetrofitLeak!!.triggerLeak(callback)
                }
                "event_bus_leak" -> {
                    val subscriber = object : EventSubscriber {
                        override fun onEvent(event: String) {}
                    }
                    activeEventSubscriber = subscriber
                    EventBusLeak.triggerLeak(subscriber)
                }
                "compose_remember" -> ComposeRememberLeak.getExplanation()
                "compose_disposable_effect" -> ComposeDisposableEffectLeak.getExplanation()
                "compose_launched_effect" -> ComposeLaunchedEffectLeak.getExplanation()
                else -> "Triggered simulated leak for '$id'."
            }
        } catch (e: Exception) {
            "Error triggering leak '$id': ${e.localizedMessage}"
        }
    }

    fun triggerFix(id: String, activity: Activity): String {
        return try {
            when (id) {
                "static_activity" -> StaticActivityLeak.triggerFix(activity)
                "unregistered_receiver" -> {
                    val res = activeReceiverLeak?.triggerFix() ?: "Cleaned up BroadcastReceiver."
                    activeReceiverLeak = null
                    res
                }
                "content_observer" -> {
                    val res = activeObserverLeak?.triggerFix() ?: "Cleaned up ContentObserver."
                    activeObserverLeak = null
                    res
                }
                "service_connection" -> {
                    val res = activeServiceConnectionLeak?.triggerFix() ?: "Unbound ServiceConnection."
                    activeServiceConnectionLeak = null
                    res
                }
                "fragment_view_binding" -> FragmentViewBindingLeak().triggerFix()
                "activity_registry" -> ActivityRegistryLeak.triggerFix(activity)
                "telephony_listener" -> {
                    val res = activeTelephonyLeak?.triggerFix() ?: "Unregistered PhoneStateListener."
                    activeTelephonyLeak = null
                    res
                }
                "clipboard_listener" -> {
                    val res = activeClipboardLeak?.triggerFix() ?: "Removed ClipboardListener."
                    activeClipboardLeak = null
                    res
                }
                "window_manager_view" -> {
                    val res = activeWindowManagerLeak?.triggerFix() ?: "Removed WindowManager View."
                    activeWindowManagerLeak = null
                    res
                }
                "speech_recognizer" -> {
                    val res = activeSpeechLeak?.triggerFix() ?: "Destroyed SpeechRecognizer."
                    activeSpeechLeak = null
                    res
                }
                "camera_hardware" -> {
                    val res = activeCameraLeak?.triggerFix() ?: "Closed Camera hardware."
                    activeCameraLeak = null
                    res
                }
                "unregistered_sensor" -> {
                    val res = activeSensorLeak?.triggerFix() ?: "Unregistered SensorEventListener."
                    activeSensorLeak = null
                    res
                }
                "location_listener" -> {
                    val res = activeLocationLeak?.triggerFix() ?: "Removed LocationListener."
                    activeLocationLeak = null
                    res
                }
                "view_tree_observer" -> {
                    val res = activeViewTreeObserverLeak?.triggerFix() ?: "Removed ViewTreeObserver listener."
                    activeViewTreeObserverLeak = null
                    res
                }
                "alarm_manager" -> {
                    val res = activeAlarmLeak?.triggerFix() ?: "Canceled AlarmManager PendingIntent."
                    activeAlarmLeak = null
                    res
                }
                "inner_class_handler" -> {
                    val res = activeHandlerLeak?.triggerFix() ?: "Cleared Handler callbacks."
                    activeHandlerLeak = null
                    res
                }
                "thread_leak" -> {
                    val res = activeThreadLeak?.triggerFix() ?: "Interrupted leaky Thread."
                    activeThreadLeak = null
                    res
                }
                "coroutine_global_scope" -> {
                    val res = activeCoroutineLeak?.triggerFix() ?: "Canceled Coroutine Job."
                    activeCoroutineLeak = null
                    res
                }
                "flow_listener" -> {
                    val res = activeFlowLeak?.triggerFix() ?: "Canceled Flow collection Job."
                    activeFlowLeak = null
                    res
                }
                "executor_service" -> {
                    val res = activeExecutorLeak?.triggerFix() ?: "Shutdown ExecutorService thread pool."
                    activeExecutorLeak = null
                    res
                }
                "live_data_observe_forever" -> {
                    val res = activeLiveDataLeak?.triggerFix() ?: "Removed LiveData observer."
                    activeLiveDataLeak = null
                    res
                }
                "rxjava_disposable" -> {
                    val res = activeRxJavaLeak?.triggerFix() ?: "Disposed RxJava stream."
                    activeRxJavaLeak = null
                    res
                }
                "async_task_inner" -> {
                    val res = activeAsyncTaskLeak?.triggerFix() ?: "Cancelled AsyncTask."
                    activeAsyncTaskLeak = null
                    res
                }
                "static_view" -> StaticViewLeak.triggerFix()
                "animator_leak" -> {
                    val res = activeAnimatorLeak?.triggerFix() ?: "Canceled ValueAnimator."
                    activeAnimatorLeak = null
                    res
                }
                "text_watcher" -> {
                    val res = activeTextWatcherLeak?.triggerFix() ?: "Removed TextWatcher."
                    activeTextWatcherLeak = null
                    res
                }
                "web_view_leak" -> {
                    val res = activeWebViewLeak?.triggerFix() ?: "Destroyed WebView cleanly."
                    activeWebViewLeak = null
                    res
                }
                "media_player_leak" -> {
                    val res = activeMediaPlayerLeak?.triggerFix() ?: "Released MediaPlayer resources."
                    activeMediaPlayerLeak = null
                    res
                }
                "custom_view_listener" -> {
                    val res = activeCustomViewLeak?.triggerFix() ?: "Cleared custom view listener."
                    activeCustomViewLeak = null
                    res
                }
                "view_pager_fragment" -> {
                    val res = activeViewPagerLeak?.triggerFix() ?: "Cleared ViewPager view cache."
                    activeViewPagerLeak = null
                    res
                }
                "lottie_animation" -> {
                    val res = activeLottieLeak?.triggerFix() ?: "Cancelled Lottie animation."
                    activeLottieLeak = null
                    res
                }
                "singleton_context" -> SingletonContextLeak.triggerFix(activity)
                "bitmap_cache" -> BitmapCacheLeak.triggerFix()
                "room_observer" -> {
                    val res = activeRoomLeak?.triggerFix() ?: "Removed Room observer."
                    activeRoomLeak = null
                    res
                }
                "web_socket_listener" -> {
                    val res = activeWebSocketLeak?.triggerFix() ?: "Closed WebSocket connection."
                    activeWebSocketLeak = null
                    res
                }
                "retrofit_callback" -> {
                    val res = activeRetrofitLeak?.triggerFix() ?: "Canceled network callback."
                    activeRetrofitLeak = null
                    res
                }
                "event_bus_leak" -> {
                    val res = activeEventSubscriber?.let { EventBusLeak.triggerFix(it) } ?: "Unregistered subscriber."
                    activeEventSubscriber = null
                    res
                }
                "compose_remember" -> "Fixed! Use LocalContext.current in Compose instead of capturing Activity in remember."
                "compose_disposable_effect" -> "Fixed! Place unregistration/cleanup code inside onDispose { } block."
                "compose_launched_effect" -> "Fixed! Use rememberUpdatedState for changing values inside long-running LaunchedEffect blocks."
                else -> "Executed cleanup fix for '$id'."
            }
        } catch (e: Exception) {
            "Error triggering fix for '$id': ${e.localizedMessage}"
        }
    }
}
