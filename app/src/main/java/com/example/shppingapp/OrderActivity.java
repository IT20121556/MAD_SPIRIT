package com.example.shppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shppingapp.eventBus.MyUpdateCartEvent;
import com.example.shppingapp.listner.IcartListner;
import com.example.shppingapp.modal.Cart;
import com.example.shppingapp.modal.Item;
import com.example.shppingapp.util.LoadingDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends AppCompatActivity implements  IcartListner{

    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    Button btnOrder;
    @BindView(R.id.txt_itemName)
    TextView txt_itemName;
    @BindView(R.id.txtPricelable)
    TextView txtPricelable;
    @BindView(R.id.img_itemImage)
    ImageView img_itemImage;
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btnCart)
    FrameLayout btnCart;
    @BindView(R.id.navHomeBtn)
    Button navHomeBtn;
    @BindView(R.id.navAccountBtn)
    Button navAccountBtn;
    @BindView(R.id.navItemBtn)
    Button navItemBtn;
    @BindView(R.id.navShippingBtn)
    Button navShippingBtn;

    LoadingDialog loadingDialog;
    IcartListner icartListner;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event){
        countCartItems();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        btnOrder = findViewById(R.id.btnOrder);
        ButterKnife.bind(this);
        icartListner =this;

        loadingDialog = new LoadingDialog(this);

        navigater();
        Intent intent = getIntent();
        String key = intent.getStringExtra("itemKey");
        getItemFromFireBase(key);
        btnBack.setOnClickListener(v->finish());
        btnCart.setOnClickListener(v-> startActivity(new Intent(this,CartActivity.class)));
        btnOrder.setOnClickListener(v -> {
            orderItem(key);
        });

        loadingDialog.startLoadingDialog();
    }

    private void navigater(){
        navHomeBtn.setOnClickListener(v -> startActivity(new Intent(this,HomeActivity.class)));
        navItemBtn.setOnClickListener(v -> startActivity(new Intent(this,MainActivity.class)));

    }
    private void orderItem(String key) {
        loadingDialog.startLoadingDialog();
        FirebaseDatabase.getInstance().getReference("Item").child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Item item = snapshot.getValue(Item.class);
                            item.setKey(snapshot.getKey());
                            addToCart(item);
                            loadingDialog.dismissDialog();
                        }else{
                            icartListner.onCartLoadFail("Item load fail");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void  getItemFromFireBase(String key) {
        FirebaseDatabase.getInstance().getReference("Item").child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Item item = snapshot.getValue(Item.class);
                            Glide.with(OrderActivity.this).load(item.getImage()).into(img_itemImage);
                            txtPricelable.setText(new StringBuilder("Price : RS. ").append(snapshot.child("price").getValue().toString()));
                            txt_itemName.setText(new StringBuilder().append(snapshot.child("name").getValue().toString()));
                            loadingDialog.dismissDialog();
                        }else{
                            icartListner.onCartLoadFail("Item load fail");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void addToCart(Item item) {

        DatabaseReference cart = FirebaseDatabase.getInstance().getReference("Cart").child("001");

        cart.child(item.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    AlertDialog dialog = new AlertDialog.Builder(OrderActivity.this)
                            .setTitle("Item already in cart")
                            .setMessage("Do you want to add another ! " )
                            .setNegativeButton("NO", (dialog1, which) -> dialog1.dismiss())
                            .setPositiveButton("YES", (dialog12, which) -> {


                                Cart cartmodel = snapshot.getValue(Cart.class);
                                Map<String,Object> updateData = new HashMap<>();
                                updateData.put("qty",cartmodel.getQty() + 1);
                                updateData.put("toatalPrice",(cartmodel.getQty() + 1) *Float.parseFloat(cartmodel.getPrice()));

                                cart.child(item.getKey()).updateChildren(updateData)
                                        .addOnSuccessListener(aVoid -> {
                                            Snackbar.make(mainLayout,"Item added to cart  ",Snackbar.LENGTH_LONG).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Snackbar.make(mainLayout,"Item added fail  ",Snackbar.LENGTH_LONG).show();
                                        });



                                dialog12.dismiss();
                            }).create();
                    dialog.show();


                }else{
                    Cart cartmodel = new Cart();
                    cartmodel.setKey(item.getKey());
                    cartmodel.setPrice(item.getPrice());
                    cartmodel.setName(item.getName());
                    cartmodel.setImage(item.getImage());
                    cartmodel.setQty(1);
                    cartmodel.setToatalPrice(Float.parseFloat(item.getPrice()));

                    cart.child(item.getKey()).setValue(cartmodel)
                            .addOnSuccessListener(aVoid -> {
                                Snackbar.make(mainLayout,"Item added to cart  ",Snackbar.LENGTH_LONG).show();

                            })
                            .addOnFailureListener(e -> {
                                Snackbar.make(mainLayout,"Item added to cart fail  ",Snackbar.LENGTH_LONG).show();
                            });
                }

                EventBus.getDefault().postSticky(new MyUpdateCartEvent());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                icartListner.onCartLoadFail(error.getMessage());
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        countCartItems();
    }
    private void countCartItems() {
        List<Cart> cartModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Cart").child("001")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot cartSnapshot : snapshot.getChildren()){
                            Cart cartModel = cartSnapshot.getValue(Cart.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        icartListner.onCartLoadSuccess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        icartListner.onCartLoadFail(error.getMessage());
                    }
                });
    }

    @Override
    public void onCartLoadSuccess(List<Cart> cart) {
        int cartSum = 0;
        for(Cart cartModal : cart)
            cartSum += cartModal.getQty();

        badge.setNumber(cartSum);
    }

    @Override
    public void onCartLoadFail(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }
}