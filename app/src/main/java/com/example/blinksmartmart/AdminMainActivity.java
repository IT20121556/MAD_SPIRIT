package com.example.blinksmartmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AdminMainActivity extends AppCompatActivity {

    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        cardView = (CardView) findViewById(R.id.viewproducts);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this,MainActivity.class);
                startActivity(intent);

                Toast.makeText(AdminMainActivity.this, "Showing Your Products", Toast.LENGTH_SHORT).show();
            }
        });

    }

}