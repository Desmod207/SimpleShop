package com.example.simpleshop;

import static android.os.Environment.DIRECTORY_PICTURES;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER_IP   = "192.168.31.127"; // Адрес сервера
    public static final int    SERVER_PORT = 4242;

    public static final String SECTION = "section";
    public static final String PRODUCT_ID = "productId";

    private int localVersion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Добавляем панель инструменотв
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            // Получаем версию локального xml файла productList
            String[] arrFiles = fileList();
            if (arrFiles.length != 0) {
                localVersion = Integer.parseInt(arrFiles[0].substring(0, arrFiles[0].lastIndexOf('.')));
            }

            Log.d("XML", "Local version: " + localVersion);

            // Запускаем прелоадер
            PreLoader preloader = new PreLoader();
            preloader.start();
        }
    }

    // Строим меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Обработка нажатий на пунктах меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_order:
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Класс прелоадер, выставляет экран заглушку и производит проверку соединения,
    // затем в случае успеха строит интерфейс.
    private class PreLoader implements Runnable {

        public PreLoader() {
            createInformationFragment(InformationFragment.LOADING_DATA);
        }

        public void start() {
            Thread thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            // Проверяем соединение
            int result = connectionTest();
            if (result == 0) {
                // Получаем актуальную версию xml файла productList с сервера отправляя запрос VERSION_MSG
                CSDialogue dialogue = new CSDialogue(SERVER_IP, SERVER_PORT, MainActivity.this);
                dialogue.sendMessage(CSDialogue.VERSION_MSG);

                Log.d("XML", "Version " + dialogue.getVersion());

                // Если версия локалного файла меньше актуального то отправляем запрос на скачивание
                if (localVersion < dialogue.getVersion()) {
                    dialogue.sendMessage(CSDialogue.UPLOAD_MSG);
                    if (localVersion != 0) {
                        deleteFile(localVersion + ".xml"); // Удаляем устаревший файл
                    }
                }
                // Подгружаем все не загруженные изображения товаров
                ProductParser parser = new ProductParser();
                if (parser.parse(getFilesDir().getAbsolutePath() + "//" +
                        dialogue.getVersion() + ".xml")) {
                    for (Product prod : parser.getProducts()) {
                        File file = new File(getExternalFilesDir(DIRECTORY_PICTURES), prod.getImageName());
                        try {
                            new FileInputStream(file); // Смотрим существует ли файл
                        } catch (FileNotFoundException e) {
                            Log.d("!!!", "Загружаем файл " + prod.getImageName());
                            dialogue.downloadImage(prod.getImageName()); // Если нет то загружаем его
                        }
                    }
                }
            }
            postExecute(result);
        }

        private void postExecute(int result) {
            if (result == 0) {
                Fragment fragment = new TopFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            } else {
                createInformationFragment(result);
            }
        }
    }

    // Создаём фрагмент с информационным текстом
    private void createInformationFragment(int infoId) {
        Fragment fragment = new InformationFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(InformationFragment.INFO_ID, infoId);
        fragment.setArguments(bundle);
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        if (infoId == InformationFragment.LOADING_DATA) {
            ft.add(R.id.content_frame, fragment);
        } else {
            ft.replace(R.id.content_frame, fragment);
        }
        ft.commit();
    }

    // Функция для проверки соединения
    private int connectionTest() {
        // Проверяем доступность интернет соединения
        if (!isOnline(this)) {
            Log.d("XML", "connection fail");
            return InformationFragment.INTERNET_CONNECTION_ERROR;
        }
        // Проверяем доступность сервера
        if (!isServerOnline(SERVER_IP, SERVER_PORT)) {
            Log.d("XML", "server fail");
            return InformationFragment.SERVER_CONNECTION_ERROR;
        }
        Log.d("XML", "Connection test OK");
        return 0;
    }

    // Функция для проверки доступности интернет соединения
    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // Функция для проверки доступности сервера
    private boolean isServerOnline(String serverIp, int serverPort) {
        try {
            Socket socket = new Socket();
            SocketAddress sockAddr = new InetSocketAddress(serverIp, serverPort);
            socket.connect(sockAddr, 2000);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}