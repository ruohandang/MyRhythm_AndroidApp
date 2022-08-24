package edu.neu.madcourse.testmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoriteSongs extends AppCompatActivity {
    ListView likedSongsList;
    TextView noSongText;
    ArrayList<String> al = new ArrayList<>();

    FirebaseUser user;
    DatabaseReference reference;
    String userID;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_songs);

        likedSongsList = (ListView) findViewById(R.id.likedSongList);
        noSongText = (TextView) findViewById(R.id.noSong);

        getSongNames();

    }


    public void getSongNames(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        reference.child(userID).child("Favorites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    String val = snap.getKey();
                    al.add(val);
                    count++;
                }
                updateUI();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void updateUI(){
        if(count<1){
            noSongText.setVisibility(View.VISIBLE);
            likedSongsList.setVisibility(View.GONE);
        }else{
            noSongText.setVisibility(View.GONE);
            likedSongsList.setVisibility(View.VISIBLE);
            likedSongsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
        }
    }


    // make back button back to setting fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}