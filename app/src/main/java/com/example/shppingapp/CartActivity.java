package com.example.shppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shppingapp.adapter.MyCartAdapter;
import com.example.shppingapp.eventBus.MyUpdateCartEvent;
import com.example.shppingapp.listner.IcartListner;
import com.example.shppingapp.modal.Cart;
import com.example.shppingapp.util.LoadingDialog;
import com.example.shppingapp.util.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements IcartListner {
    @BindView(R.id.recycle_cart)
    RecyclerView recycle_cart;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.btnBack)
    ImageView btnback;
    @BindView(R.id.txtTotal)
    TextView txtTotal;

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
        loadCartFromFirebase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        init();
        loadCartFromFirebase();
        loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();
    }

    private void loadCartFromFirebase() {
        List<Cart> cartList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Cart")
                .child("001").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Cart cartModel = dataSnapshot.getValue(Cart.class);
                        cartModel.setKey(dataSnapshot.getKey());
                        cartList.add(cartModel);
                    }
                    icartListner.onCartLoadSuccess(cartList);
                    loadingDialog.dismissDialog();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Cart empty add items!",
                            Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                icartListner.onCartLoadFail(error.getMessage());
            }
        });
    }

    private void init(){
        ButterKnife.bind(this);

        icartListner =this;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycle_cart.setLayoutManager(linearLayoutManager);
        recycle_cart.addItemDecoration(new DividerItemDecoration(this,linearLayoutManager.getOrientation()));

        btnback.setOnClickListener(v->finish());
    }

    @Override
    public void onCartLoadSuccess(List<Cart> cart) {
        double sum =0;
        for(Cart cartModal : cart){
            sum += cartModal.getToatalPrice();
        }
        txtTotal .setText(new StringBuffer("Rs. ").append(sum));
        MyCartAdapter adapter = new MyCartAdapter(this,cart,this);
        recycle_cart.setAdapter(adapter);

    }

    @Override
    public void onCartLoadFail(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }
}