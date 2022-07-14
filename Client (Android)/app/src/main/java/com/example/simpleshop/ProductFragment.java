package com.example.simpleshop;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


// Фрагмен отображающий выбраный фрагмент
public class ProductFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        int productId = -1;
        if (bundle != null) {
            productId = bundle.getInt(MainActivity.PRODUCT_ID);
        }
        View view = getView();
        if (view != null) {
            TextView productNameText = view.findViewById(R.id.product_name);
            TextView productPriceText = view.findViewById(R.id.product_price);
            TextView productDescriptionText = view.findViewById(R.id.product_description);
            ImageView productImage = view.findViewById(R.id.product_image);
            ProductParser parser = new ProductParser();
            Product prod = parser.findById(productId);

            productNameText.setText(prod.getName());
            productPriceText.setText(Integer.toString(prod.getPrice()));
            productDescriptionText.setText(prod.getDescription());
            String filePath = getActivity().getExternalFilesDir(DIRECTORY_PICTURES) + "/" + prod.getImageName();
            productImage.setImageDrawable(Drawable.createFromPath(filePath));

            ImageButton imageButton = view.findViewById(R.id.cart_button);
            imageButton.setOnTouchListener((View v, MotionEvent motionEvent) -> {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageButton.setBackgroundResource(R.color.purple_200);
                        break;
                    case MotionEvent.ACTION_UP:
                        imageButton.setBackgroundResource(R.color.purple_500);
                        break;
                }
                return false;
            });
            ShoppingCart cart = new ShoppingCart();
            imageButton.setOnClickListener(v -> {
                Log.d("!!!", "OnClick");
                cart.addProduct(prod.getId());
                Toast toast = Toast.makeText(getActivity(), R.string.add_order_message, Toast.LENGTH_SHORT);
                toast.show();
            });
        }
    }
}