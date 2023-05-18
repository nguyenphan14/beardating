package com.phannguyen.datingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phannguyen.datingapp.Cards.arrayAdapter;
import com.phannguyen.datingapp.Cards.Cards;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Cards cards_data[];
    private com.phannguyen.datingapp.Cards.arrayAdapter arrayAdapter;
    private int i;
    private FirebaseAuth mAuth;
    private ImageView userImageView;
    private  String currentUId;
    private DatabaseReference usersDb,mUserDatabase;
    List<Cards> rowItems;
    private  String userSex;
    private String oppositeUserSex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersDb = FirebaseDatabase.getInstance("https://beardating-d48a5-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        currentUId=mAuth.getCurrentUser().getUid();
        userImageView=(ImageView) findViewById(R.id.userImage);
        getUserInfo();
        checkUserSex();
        rowItems = new ArrayList<Cards>();

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems );

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                //delete an object from the Adapter (/AdapterView)
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("unlike").child(currentUId).setValue(true);

                Toast.makeText(MainActivity.this,"Không Thích",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("like").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this,"Thích",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                arrayAdapter.notifyDataSetChanged();
//                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                //System.out.println("scrolling....");
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getUserInfo() {
        mUserDatabase=usersDb.child(currentUId);
        //event to handle user change profile image
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Map<String,Object> map = (Map<String, Object>)  snapshot.getValue();
                    if(map.get("profileImageUrl")!=null){
                        String profileImageUrl = map.get("profileImageUrl").toString();
                        switch (profileImageUrl){
                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(userImageView);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(userImageView);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("like").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(MainActivity.this,"Bạn và người ấy đã kết nối",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }



    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());


        //Event to handle user sex and set oppositeUserSex
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("sex").getValue().toString()!=null){
                        userSex=snapshot.child("sex").getValue().toString();
                        switch (userSex){
                            case "Male":
                                oppositeUserSex = "Female";
                                break;
                            case "Female":
                                oppositeUserSex = "Male";
                                break;
                        }
                        getOppositeSexUser();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getOppositeSexUser(){
        //Get all opposite sex users
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("sex").getValue()!=null){
                    //remove unlike users
                    if(snapshot.exists()
                            && !snapshot.child("connections").child("unlike").hasChild(currentUId)
                            && !snapshot.child("connections").child("like").hasChild(currentUId)
                            && snapshot.child("sex").getValue().toString().equals(oppositeUserSex))
                    {
                        String profileImageUrl = "default";
                        if(!snapshot.child("profileImageUrl").getValue().equals("default")){
                            profileImageUrl=snapshot.child("profileImageUrl").getValue().toString();
                        }
                        else{
                            profileImageUrl="https://firebasestorage.googleapis.com/v0/b/beardating-d48a5.appspot.com/o/ic_default.jpg?alt=media&token=36c81a84-4748-4c77-8556-8f256a77742e";
                        }
                        Cards item = new Cards(snapshot.getKey(),snapshot.child("name").getValue().toString(),profileImageUrl);
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getUserInfo();
    }
}