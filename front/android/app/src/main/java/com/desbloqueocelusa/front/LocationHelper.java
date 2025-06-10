package com.desbloqueocelusa.front;

import android.content.Context;
import java.io.IOException;
import android.location.Location;
import android.util.Log;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import org.json.JSONObject;

public class LocationHelper {
    private static final String TAG = "LocationHelper";
    private static final String URL = "https://expo-emergence-backend.onrender.com/api/location";
    private static final int MAX_RETRIES = 5;

    public static void sendLocation(Context ctx) {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(ctx);
        CancellationTokenSource cts = new CancellationTokenSource();

        try {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        // Leer datos del usuario de SharedPreferences
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
                        String name = prefs.getString("name", "");
                        String phone = prefs.getString("phone", "");
                        String document = prefs.getString("document", "");
                        String email = prefs.getString("email", "");

                        // Enviar ubicación y datos del usuario
                        new Thread(() -> doPost(location, name, phone, document, email)).start();
                    } else {
                        Log.e(TAG, "getCurrentLocation devolvió null");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error solicitando ubicación", e);
                });
        } catch (SecurityException ex) {
            Log.e(TAG, "Permisos de ubicación no concedidos", ex);
        }
    }

    private static void doPost(final Location loc, String name, String phone, String document, String email) {
        final String payload;
        try {
            payload = new JSONObject()
                .put("lat", loc.getLatitude())
                .put("lon", loc.getLongitude())
                .put("name", name)
                .put("phone", phone)
                .put("document", document)
                .put("email", email)
                .toString();
        } catch (org.json.JSONException e) {
            Log.e(TAG, "Error creando el JSON de ubicación", e);
            return;
        }

        int attempts = 0;
        while (attempts < 3) {
            try {
                HttpClient.postJson(URL, payload);
                Log.i(TAG, "Ubicación y datos enviados");
                break;
            } catch (IOException e) {
                attempts++;
                Log.w(TAG, "Intento " + attempts + " fallido: " + e.getMessage());
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            }
        }
        if (attempts == 3) {
            Log.e(TAG, "No se pudo enviar ubicación tras 3 intentos");
        }
    }
}

//Codigo funcional andando de 10 solo envia lat y lon
// package com.desbloqueocelusa.front;

// import android.accessibilityservice.AccessibilityService;
// import android.util.Log;
// import android.view.KeyEvent;
// import android.view.accessibility.AccessibilityEvent;

// public class MyAccessibilityService extends AccessibilityService {
//     private static final String TAG = "MyAccessibilityService";
//     private static final int LONG_PRESS_THRESHOLD_MS = 2000;

//     private long downTime = 0;
//     private boolean longPressHandled = false;

//     @Override
//     protected boolean onKeyEvent(KeyEvent event) {
//         if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
//             if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                 // Arrancamos el temporizador
//                 downTime = System.currentTimeMillis();
//                 longPressHandled = false;
//                 return false;  // No consumimos todavía
//             }
//             else if (event.getAction() == KeyEvent.ACTION_UP) {
//                 long duration = System.currentTimeMillis() - downTime;
//                 if (duration >= LONG_PRESS_THRESHOLD_MS && !longPressHandled) {
//                     longPressHandled = true;
//                     Log.d(TAG, "Pulsación larga detectada → enviar ubicación");
//                     LocationHelper.sendLocation(this);
//                     return true;  // Consumimos la pulsación larga
//                 }
//                 // Pulsación corta: no la consumimos
//                 downTime = 0;
//                 longPressHandled = false;
//                 return false;
//             }
//         }
//         // Para cualquier otra tecla, dejamos pasar
//         return false;
//     }

//     @Override
//     public void onAccessibilityEvent(AccessibilityEvent event) {
//         // No usado
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

// package com.desbloqueocelusa.HeadsetLocationApp;

// import android.content.Context;
// import android.util.Log;
// import com.google.android.gms.location.FusedLocationProviderClient;
// import com.google.android.gms.location.LocationServices;
// import com.google.android.gms.tasks.OnSuccessListener;
// import org.json.JSONObject;
// import android.location.Location;

// public class LocationHelper {

//     public static void sendLocation(Context ctx) {
//         FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(ctx);
//         try {
//             client.getLastLocation()
//                 .addOnSuccessListener((OnSuccessListener<Location>) location -> {
//                     if (location != null) {
//                         new Thread(() -> post(location.getLatitude(), location.getLongitude())).start();
//                     } else {
//                         Log.e("LocationHelper", "Ubicación nula");
//                     }
//                 });
//         } catch (SecurityException ex) {
//             Log.e("LocationHelper", "Permiso denegado", ex);
//         }
//     }

//     private static void post(double lat, double lon) {
//         try {
//             JSONObject payload = new JSONObject();
//             payload.put("latitude", lat);
//             payload.put("longitude", lon);
//             HttpClient.postJson("https://expo-emergence-backend.onrender.com/api/locations",
//                                 payload.toString());
//             Log.i("LocationHelper", "Ubicación enviada");
//         } catch (Exception e) {
//             Log.e("LocationHelper", "Error enviando ubicación", e);
//         }
//     }
// }
