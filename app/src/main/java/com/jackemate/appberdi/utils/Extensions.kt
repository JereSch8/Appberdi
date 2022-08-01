package com.jackemate.appberdi.utils

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
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

fun View.addCircleRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    setBackgroundResource(resourceId)
}

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

fun Context?.isOnline(): Boolean {
    return (this?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnected
        ?: false
}

fun Context.colorOf(id: Int) = ContextCompat.getColor(this, id)

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
    " \nAction=$action\nExtras=[${extras?.keySet()?.joinToString { "$it=${extras!!.get(it)}" }}]"

fun Intent.toPendingIntent(context: Context): PendingIntent {
    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    return PendingIntent.getActivity(
        context,
        System.currentTimeMillis().toInt(),
        this,
        0
    )
}

// https://stackoverflow.com/a/9753178
inline fun <reified T : Enum<T>> Intent.putExtra(victim: T): Intent =
    putExtra(T::class.java.name, victim.ordinal)

inline fun <reified T: Enum<T>> Intent.getEnumExtra(): T =
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
    liveData.observe(this, { body.invoke(it) })
}

fun Activity.transparentStatusBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    } else {
        window.setDecorFitsSystemWindows(false)
    }
}

fun Activity.hideKeyboard() {
    this.currentFocus?.let {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun FragmentActivity.showDialogFragment(
    fragment: DialogFragment,
    tag: String,
    vararg pairs: Pair<String, Any?>
) {
    if (supportFragmentManager.findFragmentByTag(tag) != null) {
        return
    }
    fragment.arguments = bundleOf(*pairs)
    fragment.show(supportFragmentManager, tag)
}

fun Activity.share(subject: String, text: String) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(shareIntent, "¿A quién se lo compartís?"))
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}

fun Fragment.share(subject: String, text: String) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(shareIntent, "¿A quién se lo compartís?"))
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}

fun SearchView.setQueryListener(listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            listener(newText)
            return true
        }
    })
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

fun Boolean.toInt(): Int = if (this) 1 else -1


fun Double.toRoundString(): String {
    return "%.2f".format(this)
}

fun Float.toRoundString(): String {
    return "%.2f".format(this)
}

fun String.toDate(fmt: String): Date? {
    return try {
        SimpleDateFormat(fmt, Locale.US).parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun String.upper(): String = this.toUpperCase(Locale("es"))

fun LocalDateTime.eeeeHmm(): String {
    return this.format(DateTimeFormatter.ofPattern("eeee 'a las' H:mm", Locale("es")))
}

fun LocalDateTime.Hmm(): String {
    return this.format(DateTimeFormatter.ofPattern("H:mm", Locale("es")))
}

fun Date.ddMM(): String = SimpleDateFormat("dd/MM", Locale.getDefault()).format(this)
fun Date.ddMMHHmm(): String = SimpleDateFormat("dd/MM HH:mm", Locale.US).format(this)
fun Date.ddMMyyyy(): String = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(this)
fun Date.ddMMyyyyHHmm(): String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(this)
fun Date.ddMMyyyyHHmmh(): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm'h'", Locale.US).format(this)

fun Date.yyyyMMdd(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(this)
fun Date.yyyyMMddHHmmss(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(this)

fun Date.kkmm(): String = SimpleDateFormat("kk:mm", Locale.US).format(this)
fun Date.kkmmh(): String = SimpleDateFormat("kk:mm'h'", Locale.US).format(this)
fun Date.ddMMkkmm(): String = SimpleDateFormat("dd/MM kk:mm", Locale.US).format(this)
fun Date.ddMMkkmmh(): String = SimpleDateFormat("dd/MM kk:mm'h'", Locale.US).format(this)

fun Context.needPermission(p: String) =
    ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED

fun Context.needLocationPermission() =
    needPermission(Manifest.permission.ACCESS_FINE_LOCATION) && needPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

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

fun View.showSnackbar(msgId: Int, length: Int) {
    showSnackbar(context.getString(msgId), length)
}

fun View.showSnackbar(msg: String, length: Int) {
    showSnackbar(msg, length, null, {})
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

fun LatLng.fromGeo(geoPoint: GeoPoint): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun Int.toTimeString() = String.format(
    "%02d:%02d",
    TimeUnit.MILLISECONDS.toMinutes(toLong()),
    TimeUnit.MILLISECONDS.toSeconds(toLong()) % 60
)
