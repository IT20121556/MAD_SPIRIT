package com.example.shppingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shppingapp.MainActivity;
import com.example.shppingapp.OrderActivity;
import com.example.shppingapp.R;
import com.example.shppingapp.eventBus.MyUpdateCartEvent;
import com.example.shppingapp.listner.IcartListner;
import com.example.shppingapp.listner.IrecycleClickListner;
import com.example.shppingapp.modal.Cart;
import com.example.shppingapp.modal.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyItemAdapter  extends RecyclerView.Adapter<MyItemAdapter.MyItemHolder> {

    private Context context;
    private List<Item> items ;
    private IcartListner icartListner;

    public MyItemAdapter(Context context, List<Item> items, IcartListner icartListner) {
        this.context = context;
        this.items = items;
        this.icartListner = icartListner;
    }

    @NonNull
    @Override
    public MyItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyItemHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyItemHolder holder, int position) {
        Glide.with(context).load(items.get(position).getImage()).into(holder.imageView);
        holder.txtPrice.setText(new StringBuffer("RS. ").append(items.get(position).getPrice()));
        holder.txtName.setText(new StringBuffer().append(items.get(position).getName()));

        holder.setListner((view, adaptorPosition) -> {
            Intent intent = new Intent(context,OrderActivity.class);
            intent.putExtra("itemKey",items.get(position).getKey());
            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.textPrice)
        TextView txtPrice;

        IrecycleClickListner irecycleClickListner;

        public void setListner(IrecycleClickListner listner){
            this.irecycleClickListner = listner;
        }

        private Unbinder unbinder;

        public MyItemHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            irecycleClickListner.onRecycleClick(v,getAdapterPosition());
        }
    }
}
