package com.desbloqueocelusa.front;

import android.content.Context;
import java.io.IOException;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import org.json.JSONObject;

public class LocationHelper {
    private static final String TAG = "LocationHelper";

    public static void sendLocation(Context ctx) {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(ctx);
        CancellationTokenSource cts = new CancellationTokenSource();

        try {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        // **Envolvemos la llamada POST en un hilo aparte**
                        new Thread(() -> doPost(location)).start();
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

    // private static void doPost(Location location) {
    //     double lat = location.getLatitude();
    //     double lon = location.getLongitude();
    //     try {
    //         JSONObject payload = new JSONObject();
    //         payload.put("latitude", lat);
    //         payload.put("longitude", lon);

    //         HttpClient.postJson(
    //             "https://expo-emergence-backend.onrender.com/api/locations",
    //             payload.toString()
    //         );
    //         Log.i(TAG, "Ubicación enviada: " + lat + ", " + lon);
    //     } catch (Exception e) {
    //         Log.e(TAG, "Error enviando ubicación", e);
    //     }
    // }
    private static void doPost(final Location loc) {
    final String payload;
    try {
        payload = new JSONObject()
            .put("lat", loc.getLatitude())
            .put("lon", loc.getLongitude())
            .toString();
    } catch (org.json.JSONException e) {
        Log.e(TAG, "Error creando el JSON de ubicación", e);
        return;
    }

    new Thread(() -> {
        int attempts = 0;
        while (attempts < 3) {
            try {
                HttpClient.postJson(URL, payload);
                Log.i(TAG, "Ubicación enviada");
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
    }).start();
}
    private static final String URL = "https://expo-emergence-backend.onrender.com/api/location";
    private static final int MAX_RETRIES = 5;
}


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
