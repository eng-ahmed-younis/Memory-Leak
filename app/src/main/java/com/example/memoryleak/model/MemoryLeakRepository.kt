package com.example.memoryleak.model

object MemoryLeakRepository {
    val leaks: List<MemoryLeakInfo> = listOf(
        // CORE COMPONENTS
        MemoryLeakInfo(
            id = "static_activity",
            title = "1. Static Reference to Activity Context",
            category = LeakCategory.CORE_COMPONENTS,
            summary = "Storing an Activity context reference in a static companion object or top-level variable.",
            detailedExplanation = "Static variables persist for the lifetime of the Application process. Storing an Activity reference in a static field prevents Garbage Collection when the Activity is destroyed during configuration changes (like rotation) or when the user navigates away.",
            badCodeSnippet = """
                object StaticActivityLeak {
                    var leakedActivity: Activity? = null
                    fun trigger(activity: Activity) { leakedActivity = activity }
                }
            """.trimIndent(),
            fixedCodeSnippet = """
                object FixedActivityReference {
                    var appContext: Context? = null
                    fun trigger(context: Context) { appContext = context.applicationContext }
                }
            """.trimIndent(),
            solutionExplanation = "Never hold strong static references to Activity context. Use applicationContext if a Context is needed long-term, or use WeakReference<Activity>."
        ),
        MemoryLeakInfo(
            id = "unregistered_receiver",
            title = "2. Unregistered BroadcastReceiver",
            category = LeakCategory.CORE_COMPONENTS,
            summary = "Registering a BroadcastReceiver with Context without unregistering it in onDestroy/onStop.",
            detailedExplanation = "Android OS holds a reference to registered BroadcastReceivers via Context. If Context.registerReceiver() is called without a matching unregisterReceiver(), the Activity instance is held by the system framework.",
            badCodeSnippet = """
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    registerReceiver(myReceiver, IntentFilter("MY_ACTION"))
                }
            """.trimIndent(),
            fixedCodeSnippet = """
                override fun onStop() {
                    super.onStop()
                    unregisterReceiver(myReceiver)
                }
            """.trimIndent(),
            solutionExplanation = "Always pair registerReceiver() with unregisterReceiver() in matching lifecycle callbacks."
        ),
        MemoryLeakInfo(
            id = "content_observer",
            title = "3. Unregistered ContentObserver",
            category = LeakCategory.CORE_COMPONENTS,
            summary = "Registering ContentObserver on ContentResolver without unregistering on destroy.",
            detailedExplanation = "ContentResolver keeps a strong reference to registered ContentObserver instances. When an inner ContentObserver is created inside an Activity, it keeps an implicit reference to the outer Activity.",
            badCodeSnippet = """
                val observer = object : ContentObserver(handler) { ... }
                contentResolver.registerContentObserver(uri, true, observer)
            """.trimIndent(),
            fixedCodeSnippet = """
                override fun onDestroy() {
                    super.onDestroy()
                    contentResolver.unregisterContentObserver(observer)
                }
            """.trimIndent(),
            solutionExplanation = "Unregister ContentObserver inside onDestroy() or onStop()."
        ),
        MemoryLeakInfo(
            id = "service_connection",
            title = "4. Bound ServiceConnection Leak",
            category = LeakCategory.CORE_COMPONENTS,
            summary = "Invoking Context.bindService() without calling unbindService().",
            detailedExplanation = "The Android ActivityManager retains ServiceConnection references. If bindService() is called without matching unbindService(), the ServiceConnection and host Activity are retained indefinitely.",
            badCodeSnippet = """
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            """.trimIndent(),
            fixedCodeSnippet = """
                override fun onDestroy() {
                    if (isBound) {
                        unbindService(serviceConnection)
                        isBound = false
                    }
                    super.onDestroy()
                }
            """.trimIndent(),
            solutionExplanation = "Call unbindService() in onDestroy() or onStop() whenever you bind to a Service."
        ),
        MemoryLeakInfo(
            id = "fragment_view_binding",
            title = "5. Fragment ViewBinding / View Leak",
            category = LeakCategory.CORE_COMPONENTS,
            summary = "Retaining strong references to Fragment View or ViewBinding across onDestroyView().",
            detailedExplanation = "A Fragment instance outlives its View hierarchy when placed on the backstack. Storing a non-null ViewBinding or View field past onDestroyView() keeps the entire view hierarchy alive in memory.",
            badCodeSnippet = """
                class DemoFragment : Fragment() {
                    private lateinit var binding: FragmentDemoBinding
                    override fun onCreateView(...) {
                        binding = FragmentDemoBinding.inflate(inflater)
                        return binding.root
                    }
                }
            """.trimIndent(),
            fixedCodeSnippet = """
                class DemoFragment : Fragment() {
                    private var _binding: FragmentDemoBinding? = null
                    private val binding get() = _binding!!
                    override fun onDestroyView() {
                        super.onDestroyView()
                        _binding = null
                    }
                }
            """.trimIndent(),
            solutionExplanation = "Nullify ViewBinding and View references in Fragment's onDestroyView()."
        ),
        MemoryLeakInfo(
            id = "activity_registry",
            title = "6. Activity Registry Leak",
            category = LeakCategory.CORE_COMPONENTS,
            summary = "Adding Activity instances to a static list/registry without removal on destroy.",
            detailedExplanation = "Maintaining a custom activity tracker or static list without removing destroyed activity instances keeps all destroyed activities in RAM.",
            badCodeSnippet = """
                object ActivityTracker {
                    val activities = mutableListOf<Activity>()
                    fun add(a: Activity) { activities.add(a) }
                }
            """.trimIndent(),
            fixedCodeSnippet = """
                object ActivityTracker {
                    fun remove(a: Activity) { activities.remove(a) }
                }
            """.trimIndent(),
            solutionExplanation = "Remove Activity references from static lists in onDestroy() or use WeakReference."
        ),
        MemoryLeakInfo(
            id = "telephony_listener",
            title = "7. PhoneStateListener Telephony Leak",
            category = LeakCategory.CORE_COMPONENTS,
            summary = "Registering PhoneStateListener on TelephonyManager without LISTEN_NONE.",
            detailedExplanation = "TelephonyManager holds system-wide callbacks. Failing to unregister PhoneStateListener keeps the listener and Context retained in system services.",
            badCodeSnippet = """
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
            """.trimIndent(),
            fixedCodeSnippet = """
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE)
            """.trimIndent(),
            solutionExplanation = "Unregister PhoneStateListener by passing PhoneStateListener.LISTEN_NONE when component stops."
        ),
        MemoryLeakInfo(
            id = "clipboard_listener",
            title = "8. ClipboardManager Listener Leak",
            category = LeakCategory.CORE_COMPONENTS,
            summary = "Adding OnPrimaryClipChangedListener without removePrimaryClipChangedListener.",
            detailedExplanation = "ClipboardManager retains primary clip listeners globally. Adding a listener without removing it retains the outer Activity context.",
            badCodeSnippet = """
                clipboardManager.addPrimaryClipChangedListener(clipListener)
            """.trimIndent(),
            fixedCodeSnippet = """
                clipboardManager.removePrimaryClipChangedListener(clipListener)
            """.trimIndent(),
            solutionExplanation = "Remove clip listeners using clipboardManager.removePrimaryClipChangedListener()."
        ),
        MemoryLeakInfo(
            id = "window_manager_view",
            title = "9. WindowManager Floating View Leak",
            category = LeakCategory.CORE_COMPONENTS,
            summary = "Adding floating View via WindowManager without calling removeView().",
            detailedExplanation = "WindowManager keeps a direct window hierarchy reference to views added dynamically. If not removed, the view and activity context are leaked.",
            badCodeSnippet = """
                windowManager.addView(floatingView, layoutParams)
            """.trimIndent(),
            fixedCodeSnippet = """
                windowManager.removeViewImmediate(floatingView)
            """.trimIndent(),
            solutionExplanation = "Call windowManager.removeViewImmediate(view) in onDestroy() to detach window views."
        ),
        MemoryLeakInfo(
            id = "speech_recognizer",
            title = "10. SpeechRecognizer Resource Leak",
            category = LeakCategory.CORE_COMPONENTS,
            summary = "Creating SpeechRecognizer instance without calling destroy().",
            detailedExplanation = "SpeechRecognizer binds to speech recognition IPC services and native audio recorders. Without destroy(), system audio channels and context leak.",
            badCodeSnippet = """
                val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            """.trimIndent(),
            fixedCodeSnippet = """
                speechRecognizer?.destroy()
            """.trimIndent(),
            solutionExplanation = "Always invoke speechRecognizer.destroy() when speech recognition finishes."
        ),
        MemoryLeakInfo(
            id = "camera_hardware",
            title = "11. Camera Hardware Subsystem Leak",
            category = LeakCategory.CORE_COMPONENTS,
            summary = "Opening low-level Camera session without cameraDevice.close().",
            detailedExplanation = "Camera hardware sessions bind directly to hardware media drivers. Leaving session open drains battery and retains Activity instance.",
            badCodeSnippet = """
                cameraManager.openCamera(cameraId, stateCallback, handler)
            """.trimIndent(),
            fixedCodeSnippet = """
                cameraDevice?.close()
            """.trimIndent(),
            solutionExplanation = "Close Camera instances by calling cameraDevice.close() in onPause() or onDestroy()."
        ),

        // SYSTEM SERVICES
        MemoryLeakInfo(
            id = "unregistered_sensor",
            title = "12. Unregistered SensorEventListener",
            category = LeakCategory.SYSTEM_SERVICES,
            summary = "Registering SensorEventListener with SensorManager without unregistering on stop.",
            detailedExplanation = "System services like SensorManager are long-lived process-wide singletons. Registering a listener with SensorManager holds a reference to the listener and its outer Context indefinitely.",
            badCodeSnippet = """
                sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            """.trimIndent(),
            fixedCodeSnippet = """
                override fun onPause() {
                    super.onPause()
                    sensorManager.unregisterListener(sensorListener)
                }
            """.trimIndent(),
            solutionExplanation = "Always unregister SensorEventListeners in onPause() or onStop()."
        ),
        MemoryLeakInfo(
            id = "location_listener",
            title = "13. Unregistered LocationListener",
            category = LeakCategory.SYSTEM_SERVICES,
            summary = "Requesting location updates without calling LocationManager.removeUpdates().",
            detailedExplanation = "LocationManager stores LocationListener references in system location service. Without removeUpdates(), GPS updates keep invoking the callback.",
            badCodeSnippet = """
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, locationListener)
            """.trimIndent(),
            fixedCodeSnippet = """
                override fun onDestroy() {
                    super.onDestroy()
                    locationManager.removeUpdates(locationListener)
                }
            """.trimIndent(),
            solutionExplanation = "Remove location updates using locationManager.removeUpdates() in onPause() or onDestroy()."
        ),
        MemoryLeakInfo(
            id = "view_tree_observer",
            title = "14. ViewTreeObserver Listener Leak",
            category = LeakCategory.SYSTEM_SERVICES,
            summary = "Adding OnGlobalLayoutListener to ViewTreeObserver without calling removeOnGlobalLayoutListener.",
            detailedExplanation = "ViewTreeObserver holds listeners in an internal list. Failing to remove OnGlobalLayoutListener retains the listener closure and referenced outer objects.",
            badCodeSnippet = """
                view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener { ... })
            """.trimIndent(),
            fixedCodeSnippet = """
                view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
            """.trimIndent(),
            solutionExplanation = "Remove ViewTreeObserver listeners after first layout dispatch or inside onDetachedFromWindow()/onDestroy()."
        ),
        MemoryLeakInfo(
            id = "alarm_manager",
            title = "15. AlarmManager PendingIntent Leak",
            category = LeakCategory.SYSTEM_SERVICES,
            summary = "Setting repeating alarms with PendingIntent holding Context without cancel().",
            detailedExplanation = "AlarmManager schedules alarms in system server. An uncancelled PendingIntent retains implicit references to application components and context.",
            badCodeSnippet = """
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMs, intervalMs, pendingIntent)
            """.trimIndent(),
            fixedCodeSnippet = """
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            """.trimIndent(),
            solutionExplanation = "Cancel alarms via alarmManager.cancel(pendingIntent) when work is no longer required."
        ),

        // ASYNC & THREADING
        MemoryLeakInfo(
            id = "inner_class_handler",
            title = "16. Non-Static Handler / Runnable Leak",
            category = LeakCategory.ASYNC_THREADING,
            summary = "Posting delayed messages using non-static Handler inner class.",
            detailedExplanation = "In Java/Kotlin, non-static inner classes hold an implicit reference to their outer class. Delayed Messages posted to Looper queue hold the Handler -> outer Activity alive until message executes.",
            badCodeSnippet = """
                class MyActivity : Activity() {
                    private val handler = Handler(Looper.getMainLooper())
                    override fun onCreate(...) { handler.postDelayed({ updateUi() }, 600000L) }
                }
            """.trimIndent(),
            fixedCodeSnippet = """
                override fun onDestroy() {
                    super.onDestroy()
                    handler.removeCallbacksAndMessages(null)
                }
            """.trimIndent(),
            solutionExplanation = "Call handler.removeCallbacksAndMessages(null) in onDestroy() or use static Handler with WeakReference."
        ),
        MemoryLeakInfo(
            id = "thread_leak",
            title = "17. Unmanaged Thread Leak",
            category = LeakCategory.ASYNC_THREADING,
            summary = "Starting an anonymous Thread that runs continuously captured in Activity context.",
            detailedExplanation = "An active Java Thread is a Garbage Collection Root. Any non-static inner Thread holds an implicit reference to its outer Activity.",
            badCodeSnippet = """
                object : Thread() {
                    override fun run() { while (true) { Thread.sleep(5000) } }
                }.start()
            """.trimIndent(),
            fixedCodeSnippet = """
                lifecycleScope.launch { while (isActive) { delay(5000) } }
            """.trimIndent(),
            solutionExplanation = "Replace raw Thread with lifecycle-aware Kotlin Coroutines (lifecycleScope / viewModelScope)."
        ),
        MemoryLeakInfo(
            id = "coroutine_global_scope",
            title = "18. GlobalScope Coroutine Leak",
            category = LeakCategory.ASYNC_THREADING,
            summary = "Launching long-running jobs in GlobalScope capturing Activity context.",
            detailedExplanation = "GlobalScope coroutines operate on the Application lifetime and do not cancel when Activity finishes. Captured lambdas retain Activity fields indefinitely.",
            badCodeSnippet = """
                GlobalScope.launch {
                    val result = fetchNetworkData()
                    updateUi(result)
                }
            """.trimIndent(),
            fixedCodeSnippet = """
                lifecycleScope.launch {
                    val result = fetchNetworkData()
                    updateUi(result)
                }
            """.trimIndent(),
            solutionExplanation = "Avoid GlobalScope. Use lifecycleScope or viewModelScope which automatically cancel when scope is destroyed."
        ),
        MemoryLeakInfo(
            id = "flow_listener",
            title = "19. Hot Flow Uncancelled Collection",
            category = LeakCategory.ASYNC_THREADING,
            summary = "Collecting hot StateFlow/SharedFlow in non-lifecycle scope.",
            detailedExplanation = "Collecting hot flows continuously keeps subscribers active in memory. If launched in an unmanaged CoroutineScope, the collection loop never terminates.",
            badCodeSnippet = """
                val scope = CoroutineScope(Dispatchers.Main)
                scope.launch { sharedFlow.collect { value -> updateUi(value) } }
            """.trimIndent(),
            fixedCodeSnippet = """
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        sharedFlow.collect { value -> updateUi(value) }
                    }
                }
            """.trimIndent(),
            solutionExplanation = "Use repeatOnLifecycle(Lifecycle.State.STARTED) in UI components to collect flows safely."
        ),
        MemoryLeakInfo(
            id = "executor_service",
            title = "20. ExecutorService Thread Pool Leak",
            category = LeakCategory.ASYNC_THREADING,
            summary = "Instantiating ExecutorService without calling shutdown().",
            detailedExplanation = "Thread pools create worker threads that remain running in JVM memory. If executor.shutdown() is not invoked, background worker threads capture host references indefinitely.",
            badCodeSnippet = """
                val executor = Executors.newFixedThreadPool(4)
                executor.execute { doWork() }
            """.trimIndent(),
            fixedCodeSnippet = """
                executor.shutdownNow()
            """.trimIndent(),
            solutionExplanation = "Call executor.shutdown() or executor.shutdownNow() when thread pool is no longer required."
        ),
        MemoryLeakInfo(
            id = "live_data_observe_forever",
            title = "21. LiveData observeForever Leak",
            category = LeakCategory.ASYNC_THREADING,
            summary = "Calling LiveData.observeForever() without liveData.removeObserver().",
            detailedExplanation = "observeForever() bypasses LifecycleOwner observation. The observer is stored permanently in LiveData's internal listener set until removeObserver() is called.",
            badCodeSnippet = """
                liveData.observeForever(myObserver)
            """.trimIndent(),
            fixedCodeSnippet = """
                liveData.removeObserver(myObserver)
            """.trimIndent(),
            solutionExplanation = "Use liveData.observe(lifecycleOwner) instead of observeForever(), or call removeObserver()."
        ),
        MemoryLeakInfo(
            id = "rxjava_disposable",
            title = "22. RxJava / Reactive Stream Disposable Leak",
            category = LeakCategory.ASYNC_THREADING,
            summary = "Subscribing to RxJava stream without disposable.dispose().",
            detailedExplanation = "RxJava subscriptions keep active observable streams emitting events to subscriber closures. Failing to dispose active subscriptions leaks the subscriber Activity.",
            badCodeSnippet = """
                Observable.interval(1, TimeUnit.SECONDS).subscribe { updateUi() }
            """.trimIndent(),
            fixedCodeSnippet = """
                compositeDisposable.add(disposable)
                // In onDestroy():
                compositeDisposable.clear()
            """.trimIndent(),
            solutionExplanation = "Store Disposables in CompositeDisposable and invoke clear()/dispose() in onDestroy()."
        ),
        MemoryLeakInfo(
            id = "async_task_inner",
            title = "23. AsyncTask Non-Static Inner Class Leak",
            category = LeakCategory.ASYNC_THREADING,
            summary = "AsyncTask non-static inner class running doInBackground across rotation.",
            detailedExplanation = "Legacy AsyncTask inner classes hold implicit reference to outer Activity. Background tasks continuing past Activity destroy retain the dead Activity.",
            badCodeSnippet = """
                inner class MyTask : AsyncTask<Void, Void, Void>() { ... }
            """.trimIndent(),
            fixedCodeSnippet = """
                // Migrate to Coroutines lifecycleScope
                lifecycleScope.launch(Dispatchers.IO) { ... }
            """.trimIndent(),
            solutionExplanation = "Migrate legacy AsyncTask implementations to modern Kotlin Coroutines with lifecycleScope."
        ),

        // UI & VIEWS
        MemoryLeakInfo(
            id = "static_view",
            title = "24. Static View Reference Leak",
            category = LeakCategory.UI_VIEWS,
            summary = "Holding a View instance in a static variable or companion object.",
            detailedExplanation = "Every View object retains a reference to its host Context (Activity). Storing a View in a static field keeps the entire Activity and View hierarchy in memory.",
            badCodeSnippet = """
                companion object { var staticTextView: TextView? = null }
            """.trimIndent(),
            fixedCodeSnippet = """
                private var textView: TextView? = null
            """.trimIndent(),
            solutionExplanation = "Never store View instances in static variables or singletons."
        ),
        MemoryLeakInfo(
            id = "animator_leak",
            title = "25. Infinite ValueAnimator Leak",
            category = LeakCategory.UI_VIEWS,
            summary = "Starting infinite ValueAnimator without calling cancel() on destroy.",
            detailedExplanation = "ValueAnimator registers update listeners with native Choreographer. Infinite animators continue running callbacks even when the View or Activity is closed.",
            badCodeSnippet = """
                animator.repeatCount = ValueAnimator.INFINITE
                animator.start()
            """.trimIndent(),
            fixedCodeSnippet = """
                animator.removeAllUpdateListeners()
                animator.cancel()
            """.trimIndent(),
            solutionExplanation = "Call animator.removeAllUpdateListeners() and animator.cancel() in onDestroy()."
        ),
        MemoryLeakInfo(
            id = "text_watcher",
            title = "26. TextWatcher Listener Leak",
            category = LeakCategory.UI_VIEWS,
            summary = "Adding TextWatcher to shared/global EditText without removeTextChangedListener.",
            detailedExplanation = "EditText maintains a list of TextWatchers. Adding anonymous TextWatcher inner classes without removing them retains host references.",
            badCodeSnippet = """
                editText.addTextChangedListener(object : TextWatcher { ... })
            """.trimIndent(),
            fixedCodeSnippet = """
                editText.removeTextChangedListener(textWatcher)
            """.trimIndent(),
            solutionExplanation = "Remove TextWatcher listeners using removeTextChangedListener()."
        ),
        MemoryLeakInfo(
            id = "web_view_leak",
            title = "27. Android WebView Memory Leak",
            category = LeakCategory.UI_VIEWS,
            summary = "Instantiating WebView with Activity Context without explicit destroy().",
            detailedExplanation = "WebView contains native Chromium components that bind closely to Activity context. Without explicitly calling webView.destroy(), internal listeners retain the Context.",
            badCodeSnippet = """
                val webView = WebView(this)
                setContentView(webView)
            """.trimIndent(),
            fixedCodeSnippet = """
                (webView.parent as? ViewGroup)?.removeView(webView)
                webView.destroy()
            """.trimIndent(),
            solutionExplanation = "Remove WebView from parent layout, stop loading, and call webView.destroy() in onDestroy()."
        ),
        MemoryLeakInfo(
            id = "media_player_leak",
            title = "28. MediaPlayer Native Resource Leak",
            category = LeakCategory.UI_VIEWS,
            summary = "Creating MediaPlayer without calling player.release().",
            detailedExplanation = "MediaPlayer allocates native C++ hardware audio drivers. Leaving MediaPlayer instances active without release() drains native heap.",
            badCodeSnippet = """
                val mediaPlayer = MediaPlayer.create(context, R.raw.sound)
                mediaPlayer.start()
            """.trimIndent(),
            fixedCodeSnippet = """
                mediaPlayer.release()
            """.trimIndent(),
            solutionExplanation = "Always release MediaPlayer resources by calling release() in onStop() or onDestroy()."
        ),
        MemoryLeakInfo(
            id = "custom_view_listener",
            title = "29. Custom View Callback Leak",
            category = LeakCategory.UI_VIEWS,
            summary = "Custom View retaining strong reference to Activity callback.",
            detailedExplanation = "When custom views retain listener interfaces without nullifying them on view detach, references between view and activity persist.",
            badCodeSnippet = """
                class CustomView : View { var listener: OnEventListener? = null }
            """.trimIndent(),
            fixedCodeSnippet = """
                override fun onDetachedFromWindow() {
                    super.onDetachedFromWindow()
                    listener = null
                }
            """.trimIndent(),
            solutionExplanation = "Clear listener callbacks in View.onDetachedFromWindow()."
        ),
        MemoryLeakInfo(
            id = "view_pager_fragment",
            title = "30. ViewPager View / Fragment Cache Leak",
            category = LeakCategory.UI_VIEWS,
            summary = "Caching Fragment View instances in ViewPager adapter lists.",
            detailedExplanation = "Retaining instantiated view references inside static or custom ViewPager adapter list caches prevents view recycling.",
            badCodeSnippet = """
                val viewCache = mutableListOf<View>()
                fun add(view: View) { viewCache.add(view) }
            """.trimIndent(),
            fixedCodeSnippet = """
                viewCache.clear()
            """.trimIndent(),
            solutionExplanation = "Clear adapter view caches when ViewPager or Fragment host is destroyed."
        ),
        MemoryLeakInfo(
            id = "lottie_animation",
            title = "31. Continuous Lottie Vector Animation Leak",
            category = LeakCategory.UI_VIEWS,
            summary = "Running continuous Lottie/Vector animations without cancelAnimation().",
            detailedExplanation = "LottieAnimationView attaches animators to system frame callbacks. Continuous repeat loops keep animation listeners active after screen leaves.",
            badCodeSnippet = """
                lottieView.repeatCount = LottieDrawable.INFINITE
                lottieView.playAnimation()
            """.trimIndent(),
            fixedCodeSnippet = """
                lottieView.cancelAnimation()
                lottieView.clearAnimation()
            """.trimIndent(),
            solutionExplanation = "Call lottieView.cancelAnimation() in onStop() or onDestroy()."
        ),

        // SINGLETONS & CACHES
        MemoryLeakInfo(
            id = "singleton_context",
            title = "32. Singleton Activity Context Leak",
            category = LeakCategory.SINGLETONS_CACHES,
            summary = "Passing Activity Context to Singleton constructor or instance initializer.",
            detailedExplanation = "Singletons live for the entire process duration. Saving an Activity context inside a Singleton causes the Activity instance to remain in memory permanently.",
            badCodeSnippet = """
                class MySingleton private constructor(val context: Context) {
                    companion object { fun init(c: Context) { instance = MySingleton(c) } }
                }
            """.trimIndent(),
            fixedCodeSnippet = """
                class MySingleton private constructor(val context: Context) {
                    companion object { fun init(c: Context) { instance = MySingleton(c.applicationContext) } }
                }
            """.trimIndent(),
            solutionExplanation = "Always use context.applicationContext when passing Context to singletons."
        ),
        MemoryLeakInfo(
            id = "bitmap_cache",
            title = "33. Unbounded Static Bitmap Cache",
            category = LeakCategory.SINGLETONS_CACHES,
            summary = "Storing large Bitmaps in static collections without eviction policy or recycling.",
            detailedExplanation = "Bitmaps occupy large memory blocks in heap memory. Storing Bitmaps in a static map without LruCache or recycling causes OutOfMemoryError.",
            badCodeSnippet = """
                object ImageCache { val cache = mutableMapOf<String, Bitmap>() }
            """.trimIndent(),
            fixedCodeSnippet = """
                object ImageCache {
                    private val cache = LruCache<String, Bitmap>(10 * 1024 * 1024)
                    fun clear() { cache.evictAll() }
                }
            """.trimIndent(),
            solutionExplanation = "Use LruCache with a strict memory limit instead of raw static maps for Bitmaps."
        ),
        MemoryLeakInfo(
            id = "room_observer",
            title = "34. Room Database InvalidationTracker Leak",
            category = LeakCategory.SINGLETONS_CACHES,
            summary = "Registering Room InvalidationTracker.Observer without removeObserver().",
            detailedExplanation = "Room database singletons retain invalidation observers in database engine. Unremoved observers retain captured contexts.",
            badCodeSnippet = """
                database.invalidationTracker.addObserver(observer)
            """.trimIndent(),
            fixedCodeSnippet = """
                database.invalidationTracker.removeObserver(observer)
            """.trimIndent(),
            solutionExplanation = "Call database.invalidationTracker.removeObserver(observer) in onDestroy()."
        ),
        MemoryLeakInfo(
            id = "web_socket_listener",
            title = "35. Persistent WebSocket Connection Leak",
            category = LeakCategory.SINGLETONS_CACHES,
            summary = "Long-lived WebSocket client holding UI listener without webSocket.close().",
            detailedExplanation = "WebSocket connections run background threads in OkHttpClient. Leaving sockets active emits events to destroyed UI listeners.",
            badCodeSnippet = """
                val socket = client.newWebSocket(request, listener)
            """.trimIndent(),
            fixedCodeSnippet = """
                webSocket.close(1000, "Screen destroyed")
            """.trimIndent(),
            solutionExplanation = "Close active WebSockets via webSocket.close() when component finishes."
        ),

        // NETWORKING & APIS
        MemoryLeakInfo(
            id = "retrofit_callback",
            title = "36. Async Network Callback Leak",
            category = LeakCategory.NETWORKING_APIS,
            summary = "Asynchronous network request holding UI listener reference across Activity destroy.",
            detailedExplanation = "Async HTTP calls execute on background threads. Passing anonymous UI callbacks retains host Activity until network call finishes or times out.",
            badCodeSnippet = """
                apiService.getData().enqueue(object : Callback<Data> {
                    override fun onResponse(...) { updateUi(response.body()) }
                })
            """.trimIndent(),
            fixedCodeSnippet = """
                lifecycleScope.launch {
                    val data = apiService.getData()
                    updateUi(data)
                }
            """.trimIndent(),
            solutionExplanation = "Use Coroutine suspend functions with lifecycleScope or cancel network Call in onDestroy()."
        ),
        MemoryLeakInfo(
            id = "event_bus_leak",
            title = "37. EventBus Listener Leak",
            category = LeakCategory.NETWORKING_APIS,
            summary = "Subscribing to EventBus without unregistering on lifecycle end.",
            detailedExplanation = "EventBus singletons maintain strong reference collections of all subscribers. Subscribing without calling unregister keeps the subscriber object in memory forever.",
            badCodeSnippet = """
                EventBus.getDefault().register(this)
            """.trimIndent(),
            fixedCodeSnippet = """
                EventBus.getDefault().unregister(this)
            """.trimIndent(),
            solutionExplanation = "Always pair EventBus.register() with EventBus.unregister() in onStop() or onDestroy()."
        ),

        // JETPACK COMPOSE
        MemoryLeakInfo(
            id = "compose_remember",
            title = "38. Compose remember { } Context Capture",
            category = LeakCategory.JETPACK_COMPOSE,
            summary = "Capturing Activity context inside remember { } blocks or non-recomposable listeners.",
            detailedExplanation = "In Jetpack Compose, state stored via `remember { context }` can persist across recompositions and keep enclosing objects alive if leaked to long-lived state holders.",
            badCodeSnippet = """
                @Composable
                fun MyScreen(context: Context) { val activityContext = remember { context } }
            """.trimIndent(),
            fixedCodeSnippet = """
                @Composable
                fun MyScreen() { val context = LocalContext.current }
            """.trimIndent(),
            solutionExplanation = "Use LocalContext.current in Compose instead of capturing Activity parameter in remember { }."
        ),
        MemoryLeakInfo(
            id = "compose_disposable_effect",
            title = "39. DisposableEffect Missing onDispose Cleanup",
            category = LeakCategory.JETPACK_COMPOSE,
            summary = "Registering event listeners in DisposableEffect with empty or missing onDispose { }.",
            detailedExplanation = "DisposableEffect is designed for side effects that require teardown. Leaving onDispose { } empty fails to unregister listeners when the composable leaves the tree.",
            badCodeSnippet = """
                DisposableEffect(Unit) {
                    sensorManager.registerListener(listener, sensor, delay)
                    onDispose { }
                }
            """.trimIndent(),
            fixedCodeSnippet = """
                DisposableEffect(Unit) {
                    sensorManager.registerListener(listener, sensor, delay)
                    onDispose { sensorManager.unregisterListener(listener) }
                }
            """.trimIndent(),
            solutionExplanation = "Always execute listener teardown inside DisposableEffect's onDispose block."
        ),
        MemoryLeakInfo(
            id = "compose_launched_effect",
            title = "40. LaunchedEffect Key State Capture Leak",
            category = LeakCategory.JETPACK_COMPOSE,
            summary = "LaunchedEffect(Unit) running long coroutines capturing stale non-remembered state.",
            detailedExplanation = "Passing key(Unit) to LaunchedEffect causes coroutines to run continuously without restarting when parameters change, capturing stale closures.",
            badCodeSnippet = """
                LaunchedEffect(Unit) { while(true) { delay(10000); doWork(param) } }
            """.trimIndent(),
            fixedCodeSnippet = """
                val updatedParam by rememberUpdatedState(param)
                LaunchedEffect(Unit) { while(true) { delay(10000); doWork(updatedParam) } }
            """.trimIndent(),
            solutionExplanation = "Use rememberUpdatedState for changing values inside long-running LaunchedEffect blocks."
        )
    )
}
