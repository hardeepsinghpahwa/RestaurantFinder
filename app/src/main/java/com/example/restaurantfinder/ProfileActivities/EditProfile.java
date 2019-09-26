package com.example.restaurantfinder.ProfileActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.restaurantfinder.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    TextInputEditText name, phone, address;
    ImageView back, propic;
    Uri img;
    ConstraintLayout constraintLayout;
    StorageReference storageReference;
    MaterialButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        name = findViewById(R.id.editname);
        phone = findViewById(R.id.editphone);
        address = findViewById(R.id.editaddress);
        back = findViewById(R.id.editprofileback);
        propic = findViewById(R.id.editpropic);
        save = findViewById(R.id.save);
        constraintLayout=findViewById(R.id.conslayout2);
        phone.clearFocus();
        name.clearFocus();
        address.clearFocus();

        storageReference= FirebaseStorage.getInstance().getReference();
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfile.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                phone.clearFocus();
                name.clearFocus();
                address.clearFocus();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("About").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name.setText(dataSnapshot.child("name").getValue(String.class));

                Glide.with(getApplicationContext()).load(dataSnapshot.child("image").getValue(String.class)).into(propic);

                if (dataSnapshot.child("phone").getValue(String.class) != null) {
                    phone.setText(dataSnapshot.child("phone").getValue(String.class));
                }

                if (dataSnapshot.child("address").getValue(String.class) != null) {
                    address.setText(dataSnapshot.child("address").getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        propic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 121);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().equals("")) {
                    Toast.makeText(EditProfile.this, "Name cant be empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (img != null) {
                        storageReference = storageReference.child("Profile/" + FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/" + UUID.randomUUID().toString());
                        UploadTask uploadTask = storageReference.putFile(img);

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return storageReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();

                                    String im=downloadUri.toString();
                                    String n=name.getText().toString(),add=address.getText().toString(),phn=phone.getText().toString();


                                    Map<String,Object> values=new HashMap<>();
                                    values.put("image",im);
                                    values.put("name",n);
                                    values.put("address",add);
                                    values.put("phone",phn);

                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("About").updateChildren(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Alerter.create(EditProfile.this)
                                                    .setBackgroundColorInt(Color.parseColor("#FFA000"))
                                                    .setText("Profile Updated")
                                                    .setDuration(500)
                                                    .show();
                                            phone.clearFocus();
                                            name.clearFocus();
                                            address.clearFocus();
                                            EditProfile.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                        }
                                    });
                                }
                            }
                        });

                    }
                    else {
                        String n=name.getText().toString(),add=address.getText().toString(),phn=phone.getText().toString();


                        Map<String,Object> values=new HashMap<>();
                        values.put("name",n);
                        values.put("address",add);
                        values.put("phone",phn);

                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("About").updateChildren(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Alerter.create(EditProfile.this)
                                        .setBackgroundColorInt(Color.parseColor("#FFA000"))
                                        .setText("Profile Updated")
                                        .setDuration(500)
                                        .show();
                                phone.clearFocus();
                                name.clearFocus();
                                address.clearFocus();
                                EditProfile.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                            }
                        });

                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.alerter_slide_in_from_left,R.anim.alerter_slide_out_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 121 && data!=null) {
            img = data.getData();

            propic.setImageURI(img);
        }
    }
}
