package com.threshold.prettylogger

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.threshold.logger.*
import com.threshold.logger.adapter.DiskLogAdapter
import com.threshold.logger.strategy.CsvFormatStrategy
import com.threshold.logger.util.Utils
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class MainActivity : AppCompatActivity(), PrettyLogger, EasyPermissions.PermissionCallbacks {

    companion object {
        const val RC_PERMISSION_WRITE_EXTERNAL_STORAGE = 100
    }

    private val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Log it", Snackbar.LENGTH_LONG)
                    .setAction("Action", {
                        debug("Clicked the fab button.")
                    }).show()
        }
        debug { "MainActivity onCreate succeed" }
        addDiskLogAdapter()
    }


    @AfterPermissionGranted(RC_PERMISSION_WRITE_EXTERNAL_STORAGE)
    private fun addDiskLogAdapter() = if (EasyPermissions.hasPermissions(this, *PERMISSIONS)) {
        val myLogFolder = Environment.getExternalStorageDirectory().absolutePath + File.separatorChar + "MyPrettyLogger"
        val csvFormatStrategy = CsvFormatStrategy.build {
            tag = "Threshold"       //((Optional) Global tag for every log. Default PRETTY_LOGGER
            logFolder = myLogFolder //(Optional) Default log folder is here: Environment.getExternalStorageDirectory().absolutePath + File.separatorChar + "PrettyLogger"
            methodCount = 3         //(Optional) How many method line to show. Default 2
//            methodOffset = 0      //(Optional) Hides internal method calls up to offset. Default 0
//            dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS", Locale.CHINESE) //(Optional) DateFormat for every log. Default yyyy-MM-dd HH:mm:ss:SSS
        }

        addAdapter(object : DiskLogAdapter(csvFormatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                //This is for demo purpose. Generally, we only record log into file at release build.
                return BuildConfig.DEBUG && super.isLoggable(priority, tag)
//                return !BuildConfig.DEBUG && priority >= Log.WARN
            }
        })
        info("Got external storage permission, you can find log file at \"$myLogFolder\"")
    } else {
        info { "No external storage permission, now we request it" }
        EasyPermissions.requestPermissions(this, "We need write external storage permission to record log to file.",
                RC_PERMISSION_WRITE_EXTERNAL_STORAGE, *PERMISSIONS)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.debugXml.
        return when (item.itemId) {
            R.id.action_settings -> {
                debug("Click the settings menu")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        tag(loggerTag).info { "onPaused" }
    }

    override fun onResume() {
        super.onResume()
        tag(loggerTag).info { "onResumed" }
    }

    override fun onDestroy() {
        super.onDestroy()
        info { "MainActivity onDestroyed" }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        info("Permission granted: $requestCode\n${Utils.toString(perms)}")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        error("Permission denied: $requestCode\n${Utils.toString(perms)}")
        perms?.let {
            // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
            // This will display a dialog directing them to enable the permission in app settings.
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                warn("You permanently denied some permissions,which will cause we can't record log, you can change it at app settings.")
                AppSettingsDialog.Builder(this).build().show()
            }
        }
    }

}
