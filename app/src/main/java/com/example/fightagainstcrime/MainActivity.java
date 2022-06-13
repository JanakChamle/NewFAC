package com.example.fightagainstcrime;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    ViewPager2 recview;
    ImageView mprof;
    //videoadapter adapter;
    FloatingActionButton addvideo;
    private ImageView sound_disk;
    DatabaseReference likereference;
    Boolean testclick=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sound_disk = (ImageView)findViewById(R.id.imageView3);
        //Glide.with(this).load(R.drawable.disc1).into( sound_disk);

        likereference=FirebaseDatabase.getInstance().getReference("likes");
        addvideo=(FloatingActionButton)findViewById(R.id.addvideo);
        mprof=(ImageView)findViewById(R.id.mprof);

        mprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),update_profile.class));
            }
        });

        addvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),addvideo.class));
            }
        });



        recview=(ViewPager2)findViewById(R.id.recview);
        //recview.setLayoutManager(new ConstraintLayout(this));
        FirebaseRecyclerOptions<videomodel> options =
                new FirebaseRecyclerOptions.Builder<videomodel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("videos"), videomodel.class)
                        .build();


        FirebaseRecyclerAdapter<videomodel,myviewholder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<videomodel, myviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull videomodel model) {
                holder.setdata(model);
                FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                final String userid=firebaseUser.getUid();
                final String postkey=getRef(position).getKey();


                holder.getlikebuttonstatus(postkey,userid);

                holder.like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        testclick=true;

                        likereference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(testclick==true)
                                {
                                    if(snapshot.child(postkey).hasChild(userid))
                                    {
                                        likereference.child(postkey).child(userid).removeValue();
                                        testclick=false;
                                    }
                                    else
                                    {
                                        likereference.child(postkey).child(userid).setValue(true);
                                        testclick=false;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });

                holder.comment_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getApplicationContext(),commentpanel.class);
                        intent.putExtra("postkey",postkey);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_row,parent,false);
                return new myviewholder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recview.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.appmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout: FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,OtpSendActivity.class));
                finish();
                break;

            case R.id.manage_profile:startActivity(new Intent(MainActivity.this,update_profile.class));
                break;




        }
        return super.onOptionsItemSelected(item);
    }

//        adapter=new videoadapter(options);
//        viewPager2.setAdapter(adapter);
//    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater=getMenuInflater();
//        menuInflater.inflate(R.menu.appmenu,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId())
//        {
//            case R.id.logout: FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(MainActivity.this,OtpSendActivity.class));
//                finish();
//                break;
//
//            case R.id.manage_profile:startActivity(new Intent(MainActivity.this,update_profile.class));
//                break;
//
//
//
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
}