package com.desbloqueocelusa.front
import expo.modules.splashscreen.SplashScreenManager

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.desbloqueocelusa.front.LocationService
import expo.modules.ReactActivityDelegateWrapper

class MainActivity : ReactActivity() {
  private val REQUEST_CODE = 100

  override fun onCreate(savedInstanceState: Bundle?) {
    // Set the theme to AppTheme BEFORE onCreate to support
    // coloring the background, status bar, and navigation bar.
    // This is required for expo-splash-screen.
    // setTheme(R.style.AppTheme);
    // @generated begin expo-splashscreen - expo prebuild (DO NOT MODIFY) sync-f3ff59a738c56c9a6119210cb55f0b613eb8b6af
    SplashScreenManager.registerOnActivity(this)
    // @generated end expo-splashscreen
    super.onCreate(null)

    // Solicita permisos si no están concedidos
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE_LOCATION
        )
        val notGranted = permissions.any {
            ActivityCompat.checkSelfPermission(this, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        if (notGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            // NO inicies el servicio aquí, espera a que el usuario acepte los permisos
            return
        }
    }

    // Ahora sí, inicia el servicio solo si tienes los permisos
    val svc = Intent(this, LocationService::class.java)
    startForegroundService(this, svc)

    // Verificar AccessibilityService
    if (!isAccessibilityEnabled()) {
      Toast.makeText(
        this,
        "Por favor activa el servicio de accesibilidad",
        Toast.LENGTH_LONG
      ).show()
      android.os.Handler(mainLooper).postDelayed({
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
      }, 500)
    }
  }

  private fun isAccessibilityEnabled(): Boolean {
    val expectedService = "$packageName/.MyAccessibilityService"
    val setting = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    return setting.split(':').any { it.equals(expectedService, ignoreCase = true) }
  }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "main"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate {
    return ReactActivityDelegateWrapper(
          this,
          BuildConfig.IS_NEW_ARCHITECTURE_ENABLED,
          object : DefaultReactActivityDelegate(
              this,
              mainComponentName,
              fabricEnabled
          ){})
  }

  /**
    * Align the back button behavior with Android S
    * where moving root activities to background instead of finishing activities.
    * @see <a href="https://developer.android.com/reference/android/app/Activity#onBackPressed()">onBackPressed</a>
    */
  override fun invokeDefaultOnBackPressed() {
      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
          if (!moveTaskToBack(false)) {
              // For non-root activities, use the default implementation to finish them.
              super.invokeDefaultOnBackPressed()
          }
          return
      }

      // Use the default back button implementation on Android S
      // because it's doing more than [Activity.moveTaskToBack] in fact.
      super.invokeDefaultOnBackPressed()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_CODE && grantResults.all { it == android.content.pm.PackageManager.PERMISSION_GRANTED }) {
        val svc = Intent(this, LocationService::class.java)
        startForegroundService(this, svc)
    }
}}
