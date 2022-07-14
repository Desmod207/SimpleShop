package com.example.simpleshop;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


// Этот фрагмент используется для вывода информации для оповещения пользователя
public class InformationFragment extends Fragment {

    public static final String INFO_ID = "infoId";

    public static final int SERVER_CONNECTION_ERROR     = R.string.error_server_connection; // Ошибка возникающая при недоступности сервера
    public static final int INTERNET_CONNECTION_ERROR   = R.string.error_internet_connection; // Ошибка возникающая при проблемах с соединением с сетью

    public static final int LOADING_DATA                = R.string.loading_data; // Заглушка выводящаяся во время соединения с сервером

    private static int infoId;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            infoId = bundle.getInt(INFO_ID);
        }
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            TextView infoText = view.findViewById(R.id.information_text);
            infoText.setText(infoId);
        }
    }
}