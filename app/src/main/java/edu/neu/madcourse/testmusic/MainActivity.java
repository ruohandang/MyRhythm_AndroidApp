package edu.neu.madcourse.testmusic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import edu.neu.madcourse.testmusic.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    // for fragment binding
    ActivityMainBinding binding;
    public int currentMenuItemId = R.id.navigation_home;

    // greetings & motivation
    String greetingMsg;
    TextView greeting, motivation;

    // weather
    TextView location_tv;
    double temperature = 0;
    int humidity = 0;
    String description;
    final String url = "https://api.openweathermap.org/data/2.5/weather";
    final String appid = "d580011676f75c02dffa25e237648543";
    DecimalFormat df = new DecimalFormat("#.##");

    // music info
    TextView musicBPM, musicTitle, musicArtist;

    // music player
    List<Song> dbsongList = new ArrayList<>();
    FirebaseFirestore db;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    JcPlayerView jcPlayerView;


    //Sensor
    SensorManager shakeSensorManager;
    Sensor stepCounter;
    boolean isCounterSensorPresent;
    int stepCount = 0;
    double miles = 0;
    int calculatedBPM = 0;
    boolean clickedFavorite = false;
    User userProfile;

    // store leaderboard data
     ArrayList<User> usersList = new ArrayList<>();
     ArrayList<User> localusersList = new ArrayList<>();

    // fetch user info from database
    FirebaseUser user;
    DatabaseReference reference;
    String userID;

    // user info

    String userFullName;
    String userState;
    String userCity;
    int userStep;
    String userEmail;

    // liked songs
    boolean isInMyFavorite = false;
    private ArrayList<Song> favList;
    ArrayList<JcAudio> newList;

    DatabaseReference reference01;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // fetch songs from firestore database
       db = FirebaseFirestore.getInstance();
       db.collection("songData").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               if(!queryDocumentSnapshots.isEmpty()){
                   List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                   for(DocumentSnapshot d: list){
                       Song song = d.toObject(Song.class);
                       dbsongList.add(song);
                   }
               }
           }
       });

        //ask for acc sensor permission
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){ //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }


        //replaceFragment(new MainFragment());
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_leaderboard, R.id.navigation_aboutme)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerLayout);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);


        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.navigation_home:
                    currentMenuItemId = R.id.navigation_home;
                    navController.navigate(R.id.navigation_home);
                    return true;
                case R.id.navigation_leaderboard:
                    currentMenuItemId = R.id.navigation_leaderboard;
                    navController.navigate(R.id.navigation_leaderboard);
                    return true;
                case R.id.navigation_aboutme:
                    currentMenuItemId = R.id.navigation_aboutme;
                    navController.navigate(R.id.navigation_aboutme);
                    return true;
            }
            return true;
        });

        // fetch user
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProfile = snapshot.getValue(User.class);

                if(userProfile!=null){
                    userFullName = userProfile.userName;
                    userState = userProfile.state;
                    userCity = userProfile.city;
                    userStep = userProfile.step;
                    userEmail = userProfile.email;


                    updateLeaderBoard(userEmail, userStep, userCity);
                    getGreetingMsg();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });


        // greeting & motivation
        greeting = findViewById(R.id.greeting_tv);
        motivation = findViewById(R.id.motivation_tv);

        // weather
        location_tv = findViewById(R.id.weatherLocation);


        // user info
        //myBPM = findViewById(R.id.myBPM_tv);

        //music info
        musicBPM = findViewById(R.id.musicBPM);
        musicTitle = findViewById(R.id.musicTitle_tv);
        musicArtist = findViewById(R.id.musicArtist_tv);

        //liked songs

        jcPlayerView = findViewById(R.id.jcplayer);



        //initialization sensor
        shakeSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounter = shakeSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(shakeSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER ) != null){
            stepCounter = shakeSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        }
        else{
            isCounterSensorPresent = false;
        }

        getAllMusic();
    }


    /*
    ================================================================
    ========================= music player =========================
    ================================================================
     */
    public void filterMusicByBpm(int bpm){
        Song closest = dbsongList.get(0);
        Song init = dbsongList.get(0);
        newList = new ArrayList<>();
        for (Song s: dbsongList){
            int currBPM = s.getBpm();
            if(Math.abs(currBPM - bpm) < Math.abs(closest.getBpm() - bpm)){
                closest = s;
            }
            if( currBPM>= bpm-10 && currBPM <= bpm+10){
                newList.add(JcAudio.createFromURL(s.getName(), s.getUrl()));
            }
        }
        if(newList.size() == 0){
            newList.add(JcAudio.createFromURL(init.getName(), closest.getUrl()));
            newList.add(JcAudio.createFromURL(closest.getName(), closest.getUrl()));
        }
        jcPlayerView.initPlaylist(newList, null);
    }

    public void getAllMusic(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Song s: dbsongList){
                    jcAudios.add(JcAudio.createFromURL(s.getName(), s.getUrl()));
                }
                jcPlayerView.initPlaylist(jcAudios,null);
            }
        }, 1000);
    }


//    @Override
//    public void onBackPressed() {
//        final Fragment currentFragment = MainFragment.getChildFragmentManager().getFragments().get(0);
//        final NavController controller = Navigation.findNavController(this, R.id.fragmentContainerLayout);
//        if (currentFragment instanceof OnBackPressedListener)
//            ((OnBackPressedListener) currentFragment).onBackPressed();
//        else if (!controller.popBackStack())
//            finish();
//
//    }


//
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//    }
//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//    }


    public void getGreetingMsg() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour>= 12 && hour < 17){
            greetingMsg = "Good Afternoon, " + userFullName + "!";
        } else if(hour >= 17 && hour < 21){
            greetingMsg = "Good Evening, " + userFullName + "!";
        } else if(hour >= 21 && hour < 24){
            greetingMsg = "Good Night, " + userFullName + "!";
        } else {
            greetingMsg = "Good Morning, " + userFullName + "!";
        }
    }



    /**
     * Getting weather
     */
    void getWeatherDetails(){
        String tempUrl = "";
        String city = userCity;
        String state = userState;
        String country = "United States";
        if(city.equals("")){
            Toast.makeText(this, "Choose your city first!",Toast.LENGTH_LONG).show();
        }else {
            tempUrl = url + "?q=" + city + "," + state + "," + country + "&appid=" + appid;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        description = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        temperature = jsonObjectMain.getDouble("temp") - 273.15;
                        humidity = jsonObjectMain.getInt("humidity");
//                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
//                        String wind = jsonObjectWind.getString("speed");
//                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
//                        String clouds = jsonObjectClouds.getString("all");
//                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
//                                + "\n Feels Like: " + df.format(feelsLike) + " °C"

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

    /**
    update steps for firebase and userStep
     */

    public void updateStep(int step){
        HashMap User = new HashMap();
        userStep = userStep + step;
        User.put("step", userStep);
        reference.child(userID).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
            }
        });
    }

    //========================================
    // ==========favorites songs part=========
    //========================================

    /**
     * check one song is in user's fav list or not
     * @param songName
     */
    public void checkIsFavorite(String songName){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // delete from database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites").child(songName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyFavorite = snapshot.exists();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    /**
     * add current song to the fav list
     * @param songName
     */

    public void addToFavorite(String songName){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        HashMap<String, Object> map = new HashMap<>();
        map.put("songName", songName);

        // save to database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites").child(songName)
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    /**
     * remove the song from the current list
     * @param songName
     */
    public void removeFromFavorite(String songName){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // delete from database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites").child(songName)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    /**
     * change user city in database and local
     * @param newCity
     */
    public void changeCity(String newCity){
        HashMap User = new HashMap();
        userCity = newCity;
        User.put("city", newCity);
        reference.child(userID).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
            }
        });
    }


    /**
     * leaderboard update
     * @param email
     * @param step
     */
    // update leaderboard
    public void updateLeaderBoard(String email, int step, String city){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NewLeaderBoard");
        HashMap newStep = new HashMap();
        newStep.put("step", step);
        newStep.put("city", city);
        ref.child(userProfile.getId()).updateChildren(newStep);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(!usersList.contains(user)){
                        usersList.add(user);
                        if(Objects.equals(user.getCity(), userCity)) localusersList.add(user);
                    }
                }
                Collections.sort(usersList, new UsersStepCompare());
                Collections.sort(localusersList, new UsersStepCompare());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == stepCounter){
            stepCount = (int) event.values[0];
            miles = (double)stepCount/1609;
            updateStep(stepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shakeSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            shakeSensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);
        }
        // update database step
        updateStep(stepCount);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(shakeSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            shakeSensorManager.unregisterListener(this, stepCounter);
        }
        // update database step
        updateStep(stepCount);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateStep(stepCount);
        // moveTaskToBack(true);
    }


    @Override
    protected void onStop() {
        super.onStop();
        updateStep(stepCount);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateStep(stepCount);
    }
}

