package com.jackemate.appberdi.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.R
import java.util.concurrent.TimeUnit

fun View.dp(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

// https://stackoverflow.com/a/51768312/11385657
fun View.addRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

// https://stackoverflow.com/a/5921190
fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

fun Intent.pretty() =
    " \nAction=$action\nExtras=[${extras?.keySet()?.joinToString {
        "$it=${extras!!.get(it)}"
    }
    }]"

fun Intent.toPendingIntent(context: Context): PendingIntent {
    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    return PendingIntent.getActivity(
        context,
        System.currentTimeMillis().toInt(),
        this,
        PendingIntent.FLAG_IMMUTABLE
    )
}

// https://stackoverflow.com/a/9753178
inline fun <reified T : Enum<T>> Intent.putExtra(victim: T): Intent =
    putExtra(T::class.java.name, victim.ordinal)

inline fun <reified T : Enum<T>> Intent.getEnumExtra(): T =
    getIntExtra(T::class.java.name, -1)
        .takeUnless { it == -1 }
        ?.let { T::class.java.enumConstants!![it] }!!
// https://stackoverflow.com/a/9753178


fun View.visible(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}

fun View.invisible(isInvisible: Boolean) {
    visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}

inline fun <T> LifecycleOwner.observe(liveData: LiveData<T>, crossinline body: (T) -> Unit) {
    liveData.observe(this) { body.invoke(it) }
}

fun Activity.transparentStatusBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    } else {
        window.setDecorFitsSystemWindows(false)
    }
}

fun Activity.finishWithToast() {
    Toast.makeText(this, R.string.error_happend, Toast.LENGTH_LONG).show()
    finish()
}

fun Context.share(subject: String, text: String) {
    try {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SHARE) {
            param(FirebaseAnalytics.Param.CONTENT_TYPE, subject)
            param(FirebaseAnalytics.Param.ITEM_ID, text)
        }
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(shareIntent, "¿A quién se lo compartís?"))
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}

fun Context.open(url: String?) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: Exception) {
        e.printStackTrace()
        FirebaseCrashlytics.getInstance().recordException(e)
    }
}

fun EditText.onTextChanged(onTextChanged: (CharSequence) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
            onTextChanged.invoke(s)
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

fun SeekBar.onSeekBarChanged(onSeekbarChanged: (progress: Int, fromUser: Boolean) -> Unit) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onStopTrackingTouch(seekBar: SeekBar) {}

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            onSeekbarChanged(progress, fromUser)
        }
    })
}


fun Context.needPermission(p: String) =
    ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED

// Comprueba si ya tiene el permiso otorgado
fun AppCompatActivity.hasPermission(permission: String): Boolean =
    ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.hasPermission(vararg permissions: String): Boolean =
    permissions.all {
        ContextCompat.checkSelfPermission(
            this,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

fun Context.hasAnyPermission(vararg permissions: String): Boolean =
    permissions.any {
        ContextCompat.checkSelfPermission(
            this,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String): Boolean =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(
    permissionsArray: Array<String>,
    requestCode: Int
) {
    ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}

fun View.showSnackbar(
    msgId: Int,
    length: Int,
    actionMessageId: Int,
    action: (View) -> Unit
) {
    showSnackbar(context.getString(msgId), length, context.getString(actionMessageId), action)
}

fun View.showSnackbar(
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(this, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }
    }
    snackbar.show()
}

fun Int.toTimeString() = String.format(
    "%02d:%02d",
    TimeUnit.MILLISECONDS.toMinutes(toLong()),
    TimeUnit.MILLISECONDS.toSeconds(toLong()) % 60
)

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("label", text)
    clipboard?.setPrimaryClip(clip)
}

fun String.parseNewLines() = replace("\\n", "\n")
fun String.removeNewLines() = replace("\\n", "")