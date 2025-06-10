package com.desbloqueocelusa.HeadsetLocationApp;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";
    private static final int LONG_PRESS_THRESHOLD_MS = 2000;

    private long downTime = 0;
    private boolean longPressHandled = false;

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                // Arrancamos el temporizador
                downTime = System.currentTimeMillis();
                longPressHandled = false;
                return false;  // No consumimos todavía
            }
            else if (event.getAction() == KeyEvent.ACTION_UP) {
                long duration = System.currentTimeMillis() - downTime;
                if (duration >= LONG_PRESS_THRESHOLD_MS && !longPressHandled) {
                    longPressHandled = true;
                    Log.d(TAG, "Pulsación larga detectada → enviar ubicación");
                    LocationHelper.sendLocation(this);
                    return true;  // Consumimos la pulsación larga
                }
                // Pulsación corta: no la consumimos
                downTime = 0;
                longPressHandled = false;
                return false;
            }
        }
        // Para cualquier otra tecla, dejamos pasar
        return false;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // No usado
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Servicio interrumpido");
    }

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "AccessibilityService conectado");
    }
}

// package com.desbloqueocelusa.HeadsetLocationApp;

// import android.accessibilityservice.AccessibilityService;
// import android.util.Log;
// import android.view.KeyEvent;
// import android.view.accessibility.AccessibilityEvent;

// public class MyAccessibilityService extends AccessibilityService {
//     private static final String TAG = "MyAccessibilityService";
//     private static final int LONG_PRESS_THRESHOLD_MS = 2000;
//     private long downTime = 0;

//     @Override
//     protected boolean onKeyEvent(KeyEvent event) {
//         if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
//             if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                 if (downTime == 0) {
//                     downTime = System.currentTimeMillis();
//                 }
//             } else if (event.getAction() == KeyEvent.ACTION_UP) {
//                 long duration = System.currentTimeMillis() - downTime;
//                 downTime = 0;
//                 if (duration >= LONG_PRESS_THRESHOLD_MS) {
//                     Log.d(TAG, "Pulsación larga detectada, enviando ubicación...");
//                     LocationHelper.sendLocation(this);
//                 }
//             }
//             return true; // Consumir evento
//         }
//         return super.onKeyEvent(event);
//     }

//     @Override
//     public void onAccessibilityEvent(AccessibilityEvent event) {
//         // No usamos otros eventos
//     }

//     @Override
//     public void onInterrupt() {
//         Log.d(TAG, "Servicio interrumpido");
//     }

//     @Override
//     protected void onServiceConnected() {
//         Log.d(TAG, "AccessibilityService conectado");
//     }
// }
