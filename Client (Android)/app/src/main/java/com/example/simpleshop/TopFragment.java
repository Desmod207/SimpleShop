package com.example.simpleshop;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class TopFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top, container, false);
    }

    // Строим меню разделов
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            ListView listView = view.findViewById(R.id.sections_list);

            ProductParser parser = new ProductParser();
            ArrayList<String> sections = parser.getSections();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, sections);
            listView.setAdapter(adapter);

            AdapterView.OnItemClickListener itemClickListener =
                    (listView1, itemView, position, id) -> {
                        // Тарнзакция фрагмента используется для отображения экземпляра SectionFragment
                        Fragment fragment = new SectionFragment();
                        Bundle bundle = new Bundle();
                        Log.d("XML", listView1.getItemAtPosition(position).toString());
                        bundle.putString(MainActivity.SECTION, listView1.getItemAtPosition(position).toString());
                        fragment.setArguments(bundle); // Передаём версию файла ProductsList фрагменту
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); // Фрагмент должен сопроваждаться эффектами проявления и растворения
                        ft.addToBackStack(null); // Транзакция добавлена в стек возврата
                        ft.commit();
                    };

            listView.setOnItemClickListener(itemClickListener);
        }
    }
}