package com.example.simpleshop;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

// Строим меню товаров из выбраного раздела
public class SectionFragment extends Fragment {

    private String section;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView pizzaRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_section, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            section = bundle.getString(MainActivity.SECTION);
        }

        ProductParser parser = new ProductParser();
        ArrayList<Product> products = parser.getProducts();

        ArrayList<String> captions = new ArrayList<>();
        ArrayList<String> imagesNames = new ArrayList<>();
        ArrayList<Integer> productsId = new ArrayList<>();
        for (Product prod : products) {
            if (prod.getSection().equalsIgnoreCase(section)) {
                captions.add(prod.getName() + " " + prod.getPrice());
                productsId.add(prod.getId());
                imagesNames.add(prod.getImageName());
            }
        }

        CaptionedImagesAdapter adapter = new CaptionedImagesAdapter(captions.toArray(new String[0]), imagesNames.toArray(new String[0]));
        pizzaRecycler.setAdapter(adapter);
        // Чтобы карточки отображались в виде таблицы издвух столбцов, мы используем GridLayoutManager
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        pizzaRecycler.setLayoutManager(layoutManager);

        adapter.setListener(position -> {
            Fragment fragment = new ProductFragment();
            Bundle b = new Bundle();
            b.putInt(MainActivity.PRODUCT_ID, productsId.get(position));
            fragment.setArguments(b); // Передаём версию файла ProductsList фрагменту
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); // Фрагмент должен сопроваждаться эффектами проявления и растворения
            ft.addToBackStack(null); // Транзакция добавлена в стек возврата
            ft.commit();
        });

        return pizzaRecycler;
    }
}