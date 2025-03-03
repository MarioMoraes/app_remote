package br.com.bluestormstudios.app_remote;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;

public class Scrcpy extends Service {
    private final IBinder binder = new MyServiceBinder();
    private SendCommands sendCommands;
    private byte[] fileBase64;
    private boolean isRunning = false;

    public class MyServiceBinder extends Binder {
        Scrcpy getService() {
            return Scrcpy.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            loadServerFile();
            String serverAdr = intent.getStringExtra("serverAdr");
            int videoBitrate = intent.getIntExtra("videoBitrate", 8000000);
            int maxHeight = intent.getIntExtra("maxHeight", 1920);
            String localIp = wifiIpAddress();
            sendCommands = new SendCommands();

            if (serverAdr != null && !serverAdr.isEmpty()) {
                int result = sendCommands.SendAdbCommands(this, fileBase64, serverAdr, localIp, videoBitrate, maxHeight);
                if (result == 0) {
                    Log.d("Scrcpy", "Scrcpy iniciado com sucesso");
                    isRunning = true;
                } else {
                    Log.e("Scrcpy", "Falha na conexão ADB ou rede");
                }
            } else {
                Log.e("Scrcpy", "Endereço do servidor vazio");
            }
        }
        return START_STICKY;
    }

    private void loadServerFile() {
        try {
            InputStream inputStream = getAssets().open("scrcpy-server.jar");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            fileBase64 = Base64.encode(buffer, Base64.NO_WRAP);
            inputStream.close();
        } catch (IOException e) {
            Log.e("Scrcpy", "Erro ao carregar scrcpy-server.jar: " + e.getMessage());
        }
    }

    private String wifiIpAddress() {
        try {
            java.net.InetAddress ipv4 = null;
            java.net.InetAddress ipv6 = null;
            for (java.util.Enumeration<java.net.NetworkInterface> en = java.net.NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                java.net.NetworkInterface intf = en.nextElement();
                for (java.util.Enumeration<java.net.InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    java.net.InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof java.net.Inet6Address) {
                        ipv6 = inetAddress;
                        continue;
                    }
                    if (inetAddress.isLoopbackAddress() && inetAddress instanceof java.net.Inet4Address) {
                        ipv4 = inetAddress;
                        continue;
                    }
                    return inetAddress.getHostAddress();
                }
            }
            if (ipv6 != null) return ipv6.getHostAddress();
            if (ipv4 != null) return ipv4.getHostAddress();
            return null;
        } catch (java.net.SocketException ex) {
            Log.e("Scrcpy", "Erro ao obter IP: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }
}