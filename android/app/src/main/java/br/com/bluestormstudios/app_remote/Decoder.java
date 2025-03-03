package br.com.bluestormstudios.app_remote;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Decoder {
    private static final String TAG = "Decoder";
    private Surface surface;
    private String serverAdr;
    private MediaCodec codec;
    private Socket socket;

    public Decoder(Surface surface, String serverAdr) {
        this.surface = surface;
        this.serverAdr = serverAdr;
    }

    public void start() {
        try {
            // Conectar ao servidor Scrcpy no dispositivo remoto
            socket = new Socket(serverAdr, 5000);
            Log.d(TAG, "Conectado ao servidor Scrcpy em " + serverAdr);

            // Configurar o codec de vídeo
            codec = MediaCodec.createDecoderByType("video/avc");
            MediaFormat format = MediaFormat.createVideoFormat("video/avc", 1920, 1080); // Ajuste conforme necessário
            codec.configure(format, surface, null, 0);
            codec.start();

            // Ler dados do socket e decodificar
            new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024 * 1024];
                    while (true) {
                        int bytesRead = socket.getInputStream().read(buffer);
                        if (bytesRead > 0) {
                            int inputBufferId = codec.dequeueInputBuffer(10000);
                            if (inputBufferId >= 0) {
                                ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferId);
                                inputBuffer.clear();
                                inputBuffer.put(buffer, 0, bytesRead);
                                codec.queueInputBuffer(inputBufferId, 0, bytesRead, 0, 0);
                            }

                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                            int outputBufferId = codec.dequeueOutputBuffer(bufferInfo, 10000);
                            if (outputBufferId >= 0) {
                                codec.releaseOutputBuffer(outputBufferId, true);
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Erro ao decodificar vídeo: " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "Erro ao iniciar decoder: " + e.getMessage());
        }
    }
}