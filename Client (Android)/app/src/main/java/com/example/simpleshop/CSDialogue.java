package com.example.simpleshop;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.util.Log;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


// Класс для диалога клинта с сервером
public class CSDialogue implements Runnable {

    public static final int VERSION_MSG = 1;
    public static final int UPLOAD_MSG  = 2;

    private static final int ORDER_MSG   = 8;
    private static final int IMAGE_MSG  = 9;


    private final String serverIp;
    private final int serverPort;
    private final Context context;

    private int message;
    private String orderString;
    private int version = 0;
    private String imageName = "";

    public CSDialogue(String serverIp, int serverPort, Context context) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(serverIp, serverPort);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            outputStream.write(message);
            switch (message) {
                case VERSION_MSG: // Получаем актуальную версию xml файла productList с сервера
                    version = inputStream.read();
                    if (version < 0) {
                        sendMessage(VERSION_MSG);
                    }
                    Log.d("XML", "Server product version: " + version);
                    break;
                case UPLOAD_MSG: // Скачиваем актуальную версию xml файла productList с сервера
                    downloadFile(inputStream, version + ".xml", false);
                    break;
                case ORDER_MSG: // Предаём данные заказа серверу
                    Log.d("order", orderString);
                    outputStream.write(orderString.getBytes());
                    break;
                case IMAGE_MSG: // Передаём запрос на скачивание изображения товара
                    outputStream.write(imageName.getBytes());
                    downloadFile(inputStream, imageName, true);
                    break;
            }
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(InputStream inputStream, String fileName, boolean externalFilesDir) throws IOException {
        FileOutputStream fileOutput;
        if (externalFilesDir) {
            File file = new File(context.getExternalFilesDir(DIRECTORY_PICTURES), fileName);
            fileOutput = new FileOutputStream(file);
        } else {
            fileOutput = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        }
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            fileOutput.write(buffer, 0, length);
        }
        fileOutput.close();
    }

    public int getVersion() {
        return version;
    }

    public void downloadImage(String imageName) {
        this.imageName = imageName;
        sendMessage(IMAGE_MSG);
    }

    public void makeOrder (String orderString) {
        this.orderString = orderString;
        sendMessage(ORDER_MSG);
    }

    public void sendMessage(int message) {
        this.message = message;
        Thread thread = new Thread(this);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
