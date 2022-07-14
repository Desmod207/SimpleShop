package com.example.simpleshop;

import static android.os.Environment.DIRECTORY_PICTURES;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class CartActivity extends AppCompatActivity {

    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart() {
        super.onStart();
        ShoppingCart cart = new ShoppingCart();
        ProductParser parser = new ProductParser();
        Set<Integer> ids = cart.getOrderList().keySet();
        ViewGroup cart_layout = findViewById(R.id.cart_layout);
        // Строим интерфейс корзины
        for(Integer id : ids) {
            LinearLayout linLayout = new LinearLayout(this);
            linLayout.setOrientation(LinearLayout.HORIZONTAL);

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    250, 250);
            imageView.setPadding(10,0,0,0);
            imageView.setLayoutParams(lp);
            String filePath = getExternalFilesDir(DIRECTORY_PICTURES) + "/" +
                    parser.getProducts().get(id).getImageName();
            imageView.setImageDrawable(Drawable.createFromPath(filePath));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            linLayout.addView(imageView);

            TextView productNameView = new TextView(this);
            productNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            productNameView.setText(parser.getProducts().get(id).getName());
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            productNameView.setPadding(50,0,0,0);
            productNameView.setLayoutParams(lp);
            linLayout.addView(productNameView);

            TextView productPriceView = new TextView(this);
            productPriceView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            productPriceView.setText(Integer.toString(parser.getProducts().get(id).getPrice()));
            productPriceView.setPadding(100,0,0,0);
            productPriceView.setLayoutParams(lp);
            linLayout.addView(productPriceView);
            totalPrice += parser.getProducts().get(id).getPrice() * cart.getOrderList().get(id);

            TextView quantity = new TextView(this);
            quantity.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            String str = "x " + cart.getOrderList().get(id);
            quantity.setText(str);
            quantity.setPadding(100,0,50,0);
            quantity.setLayoutParams(lp);
            linLayout.addView(quantity);

            cart_layout.addView(linLayout);
        }

        TextView totalView = findViewById(R.id.total_view);
        String str = getString(R.string.total_price) + ": " +  totalPrice;
        totalView.setText(str);

        EditText phoneEdit = findViewById(R.id.phone_edit);

        Button orderButton = findViewById(R.id.order_button);

        orderButton.setOnTouchListener((View v, MotionEvent motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    orderButton.setBackgroundResource(R.color.purple_200);
                    CSDialogue dialogue = new CSDialogue(MainActivity.SERVER_IP,
                            MainActivity.SERVER_PORT, this);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Phone number: ");
                    sb.append(phoneEdit.getText());
                    sb.append("\n");
                    for(Integer id : ids) {
                        sb.append(parser.getProducts().get(id).getName()).
                                append(" x ").
                                append(cart.getOrderList().get(id)).
                                append('\n');
                    }
                    sb.append("total price - ").append(totalPrice);
                    dialogue.makeOrder(sb.toString());
                    Toast toast = Toast.makeText(this, R.string.order_complete_message, Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case MotionEvent.ACTION_UP:
                    orderButton.setBackgroundResource(R.color.purple_500);
                    break;
            }
            return false;
        });
    }
}