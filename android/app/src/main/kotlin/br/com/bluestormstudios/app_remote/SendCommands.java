package br.com.bluestormstudios.app_remote;

import android.content.Context;
import android.util.Log;

public class SendCommands {
    private static final String TAG = "SendCommands";

    public int SendAdbCommands(Context context, byte[] fileBase64, String serverAdr, String localIp, int videoBitrate, int maxHeight) {
        try {
            String adbPath = "adb";
            Process process;

            // Criar diretório
            process = Runtime.getRuntime().exec(new String[]{adbPath, "-s", serverAdr, "shell", "mkdir", "-p", "/data/local/tmp/scrcpy"});
            process.waitFor();

            // Enviar o JAR
            process = Runtime.getRuntime().exec(new String[]{adbPath, "-s", serverAdr, "push", "/data/local/tmp/scrcpy/scrcpy-server.jar"});
            process.waitFor();

            // Configurar encaminhamento de porta
            process = Runtime.getRuntime().exec(new String[]{adbPath, "-s", serverAdr, "forward", "tcp:5000", "tcp:5000"});
            process.waitFor();

            // Iniciar o servidor Scrcpy
            String cmd = "CLASSPATH=/data/local/tmp/scrcpy/scrcpy-server.jar app_process / com.genymobile.scrcpy.Server 1.18 0 " + maxHeight + " " + videoBitrate + " 5000 false false";
            process = Runtime.getRuntime().exec(new String[]{adbPath, "-s", serverAdr, "shell", cmd});
            process.waitFor();

            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao executar comandos ADB: " + e.getMessage());
            return -1;
        }
    }
}