package com.zubair.jarvis.diagnostics

import android.app.ActivityManager
import android.app.Application
import android.app.ApplicationExitInfo
import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Lightweight, local-only startup diagnostics.
 * Fatal native signals cannot be caught here; historical exit information is
 * inspected on the next launch through ApplicationExitInfo.
 */
class JarvisApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        StartupDiagnostics.install(this)
        StartupDiagnostics.mark("APPLICATION_ON_CREATE")
        StartupDiagnostics.recordPreviousExit(this)
    }
}

object StartupDiagnostics {
    private const val TAG = "JarvisStartup"
    private const val FILE_NAME = "startup-diagnostics.log"
    private const val MAX_BYTES = 256 * 1024
    private val lock = Any()
    @Volatile private var installed = false
    private lateinit var logFile: File

    fun install(application: Application) {
        synchronized(lock) {
            if (installed) return
            logFile = File(application.noBackupFilesDir, FILE_NAME)
            val previous = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, error ->
                record("UNCAUGHT_EXCEPTION thread=${thread.name} type=${error.javaClass.name}")
                recordStack(error)
                previous?.uncaughtException(thread, error)
            }
            installed = true
        }
    }

    fun mark(stage: String) {
        record("STAGE $stage")
    }

    fun recordPreviousExit(application: Application) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return
        try {
            val manager = application.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                ?: return
            val exits = manager.getHistoricalProcessExitReasons(application.packageName, 0, 8)
            exits.forEach { info ->
                val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
                    .format(Date(info.timestamp))
                record(
                    "PREVIOUS_EXIT timestamp=$timestamp reason=${info.reason} " +
                        "status=${info.status} importance=${info.importance}"
                )

                // traceInputStream was added in API 31; API 30 devices must not call it.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    (info.reason == ApplicationExitInfo.REASON_CRASH ||
                        info.reason == ApplicationExitInfo.REASON_CRASH_NATIVE)
                ) {
                    try {
                        info.traceInputStream?.bufferedReader()?.use { reader ->
                            record("EXIT_TRACE_BEGIN")
                            reader.readLines().take(160).forEach { record(it.take(500)) }
                            record("EXIT_TRACE_END")
                        }
                    } catch (ignored: Exception) {
                        record("EXIT_TRACE_UNAVAILABLE type=${ignored.javaClass.simpleName}")
                    }
                }
            }
        } catch (error: Exception) {
            record("EXIT_INFO_ERROR type=${error.javaClass.name}")
        }
    }

    fun exportFile(): File? = if (::logFile.isInitialized && logFile.exists()) logFile else null

    private fun recordStack(error: Throwable) {
        val writer = StringWriter()
        error.printStackTrace(PrintWriter(writer))
        writer.toString().lineSequence().take(120).forEach { record(it.take(500)) }
    }

    private fun record(message: String) {
        val line = "${System.currentTimeMillis()} $message"
        Log.i(TAG, message)
        synchronized(lock) {
            if (!::logFile.isInitialized) return
            try {
                if (logFile.exists() && logFile.length() > MAX_BYTES) {
                    val backup = File(logFile.parentFile, "$FILE_NAME.1")
                    if (backup.exists()) backup.delete()
                    logFile.renameTo(backup)
                }
                logFile.appendText(line + "\n")
            } catch (ignored: Exception) {
                Log.w(TAG, "Could not persist startup diagnostics", ignored)
            }
        }
    }
}
