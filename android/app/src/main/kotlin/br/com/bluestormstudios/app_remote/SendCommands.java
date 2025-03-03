package br.com.bluestormstudios.app_remote;

import android.content.Context;
import android.util.Log;
import com.genymobile.scrcpy.wrappers.ServiceManager;

public class SendCommands {
    private static final String TAG = "SendCommands";
    private ServiceManager serviceManager;

    public SendCommands() {
        serviceManager = new ServiceManager();
    }

    public int SendAdbCommands(Context context, byte[] fileBase64, String serverAdr, String local_ip, int videoBitrate, int MaxHeight) {
        try {
            String[] Command_push = {"shell", "mkdir", "-p", "/data/local/tmp/scrcpy"};
            String[] Command_push2 = {"push", fileBase64 == null ? "scrcpy-server.jar" : new String(fileBase64), "/data/local/tmp/scrcpy/scrcpy-server.jar"};
            String[] Command_forward = {"forward", "tcp:5000", "tcp:5000"};
            String[] Command_start_server = {"shell", "CLASSPATH=/data/local/tmp/scrcpy/scrcpy-server.jar", "app_process", "/", "com.genymobile.scrcpy.Server", "1.18", String.valueOf(Log.getLogLevel()), String.valueOf(MaxHeight), String.valueOf(videoBitrate), "5000", "false", "false"};

            serviceManager.getAdb().executeShellCommand(Command_push);
            Thread.sleep(500);
            if (fileBase64 == null) {
                serviceManager.getAdb().executeShellCommand(Command_push2);
            } else {
                serviceManager.getAdb().pushFile(context, Command_push2[1], Command_push2[2]);
            }
            Thread.sleep(500);
            serviceManager.getAdb().executeShellCommand(Command_forward);
            Thread.sleep(500);
            Runtime.getRuntime().exec("adb -s " + serverAdr + " shell am force-stop com.genymobile.scrcpy");
            Thread.sleep(500);
            serviceManager.getAdb().executeShellCommandAsync(Command_start_server);

            return 0;
        } catch (Exception e) {
            Log.e(TAG, "ADB command failed: " + e.getMessage());
            return -1;
        }
    }
}