package com.example.fess.firebasenotificationapp;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends android.support.v4.app.Fragment {

    Button logout;
    FirebaseAuth auth;
    TextView name;
    CircleImageView image;
    DatabaseReference databaseReference;
    ProgressBar progressBar;


    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        logout = view.findViewById(R.id.logout);

        name = view.findViewById(R.id.name);
        image = view.findViewById(R.id.image_profile);
        progressBar = view.findViewById(R.id.progress);

        String user_id = auth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String UserName = (String) dataSnapshot.child("name").getValue();
//                String image_url = "https://firebasestorage.googleapis.com/v0/b/fir-notificationapp-84ba7.appspot.com/o/images%2F2UrhWbxb6uRkMkTT9Tg9bz00jlv1.jpg?alt=media&token=98909b94-7e24-4fef-8246-cbee8c89ae52";
                String image_url = dataSnapshot.child("user_image").getValue().toString();
                Log.d("Profile_Images", "image_url-" + image_url);

                name.setText(UserName);


                Glide.with(container.getContext()).load(image_url).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).into(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference removeData = databaseReference.child("token_id");
                removeData.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        auth.signOut();
                        startActivity(new Intent(container.getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });


            }
        });
        return view;
    }

}