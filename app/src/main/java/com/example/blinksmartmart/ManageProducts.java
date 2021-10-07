package com.example.blinksmartmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ManageProducts extends AppCompatActivity {

    // creating variables for our edit text, firebase database,
    // database reference, course rv modal,progress bar.
    private TextInputEditText courseNameEdt, courseDescEdt, coursePriceEdt, bestSuitedEdt, courseImgEdt, courseLinkEdt;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ItemsRVModal itemsRVModal;
    private ProgressBar loadingPB;
    // creating a string for our course id.
    private String courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_products);
        // initializing all our variables on below line.
        Button addCourseBtn = findViewById(R.id.idBtnAddCourse);
        courseNameEdt = findViewById(R.id.idEdtCourseName);
        courseDescEdt = findViewById(R.id.idEdtCourseDescription);
        coursePriceEdt = findViewById(R.id.idEdtCoursePrice);
        bestSuitedEdt = findViewById(R.id.idEdtSuitedFor);
        courseImgEdt = findViewById(R.id.idEdtCourseImageLink);
        courseLinkEdt = findViewById(R.id.idEdtCourseLink);
        loadingPB = findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // on below line we are getting our modal class on which we have passed.
        itemsRVModal = getIntent().getParcelableExtra("course");
        Button deleteCourseBtn = findViewById(R.id.idBtnDeleteCourse);

        if (itemsRVModal != null) {
            // on below line we are setting data to our edit text from our modal class.
            courseNameEdt.setText(itemsRVModal.getCourseName());
            coursePriceEdt.setText(itemsRVModal.getCoursePrice());
            bestSuitedEdt.setText(itemsRVModal.getBestSuitedFor());
            courseImgEdt.setText(itemsRVModal.getCourseImg());
            courseLinkEdt.setText(itemsRVModal.getCourseLink());
            courseDescEdt.setText(itemsRVModal.getCourseDescription());
            courseID = itemsRVModal.getCourseId();
        }

        // on below line we are initialing our database reference and we are adding a child as our course id.
        databaseReference = firebaseDatabase.getReference("Courses").child(courseID);
        // on below line we are adding click listener for our add course button.
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are making our progress bar as visible.
                loadingPB.setVisibility(View.VISIBLE);
                // on below line we are getting data from our edit text.
                String courseName = courseNameEdt.getText().toString();
                String courseDesc = courseDescEdt.getText().toString();
                String coursePrice = coursePriceEdt.getText().toString();
                String bestSuited = bestSuitedEdt.getText().toString();
                String courseImg = courseImgEdt.getText().toString();
                String courseLink = courseLinkEdt.getText().toString();
                // on below line we are creating a map for
                // passing a data using key and value pair.
                Map<String, Object> map = new HashMap<>();
                map.put("courseName", courseName);
                map.put("courseDescription", courseDesc);
                map.put("coursePrice", coursePrice);
                map.put("bestSuitedFor", bestSuited);
                map.put("courseImg", courseImg);
                map.put("courseLink", courseLink);
                map.put("courseId", courseID);

                // on below line we are calling a database reference on
                // add value event listener and on data change method
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        // making progress bar visibility as gone.
//                        loadingPB.setVisibility(View.GONE);
//                        // adding a map to our database.
//                        databaseReference.updateChildren(map);
//                        // on below line we are displaying a toast message.
//                        Toast.makeText(ManageProducts.this, "Course Updated..", Toast.LENGTH_SHORT).show();
//                        // opening a new activity after updating our coarse.
//                        startActivity(new Intent(ManageProducts.this, MainActivity.class));
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // displaying a failure message on toast.
//                        Toast.makeText(ManageProducts.this, "Fail to update course..", Toast.LENGTH_SHORT).show();
//                    }
//                });

                databaseReference.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ManageProducts.this, "Product Updated..", Toast.LENGTH_SHORT).show();
                        loadingPB.setVisibility(View.GONE);
                        startActivity(new Intent(ManageProducts.this, MainActivity.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManageProducts.this, "Fail to update product..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // adding a click listener for our delete course button.
        deleteCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling a method to delete a course.
                deleteCourse();
            }
        });

    }

    private void deleteCourse() {
        // on below line calling a method to delete the course.
        databaseReference.removeValue();
        // displaying a toast message on below line.
        Toast.makeText(this, "Product Deleted..", Toast.LENGTH_SHORT).show();
        // opening a main activity on below line.
        startActivity(new Intent(ManageProducts.this, MainActivity.class));
    }
}
