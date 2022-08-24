package edu.neu.madcourse.testmusic;
import edu.neu.madcourse.testmusic.databinding.FragmentMainBinding;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import java.util.List;


public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private int stateValue;
    private final String  SOME_VALUE_KEY = "someValueToSave";
    // greetings & motivation
    private TextView greeting_tv, motivation_tv;
    private String greetingMsg;
    private ScrollView badge_sv;

    // greetings & motivation
    TextView totalSteps_tv;
    LottieAnimationView badge1, badge2, badge3, badge4;



    // weatherv
    Button getWeather_btn;
    TextView location;
    TextView temperature_tv, humidity_tv, description_tv;

    // music info
    TextView musicBPM, musicTitle, musicArtist;

    // music player
//    JcPlayerView jcPlayerView;
    List<Song> dbList;

    //Animation
    LottieAnimationView lottieFavorite, lottieRunning, lottieHeartbeat, lottieLocation;
    Button getBPM;

    // BPM fields
    TextView myBPM_tv;
    int startCounter = 0;
    int endCounter = 0;

    //main activity
    MainActivity mainActivity;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (savedInstanceState != null) {
            stateValue = savedInstanceState.getInt(SOME_VALUE_KEY);
        }

        mainActivity = (MainActivity) getActivity();
        dbList = mainActivity.dbsongList;
        motivation_tv = root.findViewById(R.id.motivation_tv);
        greeting_tv = root.findViewById(R.id.greeting_tv);
        badge1 = root.findViewById(R.id.badge1);
        badge2 = root.findViewById(R.id.badge2);
        badge3 = root.findViewById(R.id.badge3);
        badge4 = root.findViewById(R.id.badge4);

        //music
        musicTitle = root.findViewById(R.id.musicTitle_tv);

        //Weather
        location = root.findViewById(R.id.weatherLocation);
        getWeather_btn = root.findViewById(R.id.getWeather_btn);
        temperature_tv = root.findViewById(R.id.temperature_tv);
        description_tv = root.findViewById(R.id.description_tv);
        humidity_tv = root.findViewById(R.id.humidity_tv);
        getWeather_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.getWeatherDetails();

                //need some time to get the weather data
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    temperature_tv.setText(mainActivity.df.format(mainActivity.temperature) + " °C");
                    humidity_tv.setText(mainActivity.humidity+ "%");
                    description_tv.setText(mainActivity.description);
                    }
                }, 1000);

            }
        });


        //Animation
        lottieFavorite = root.findViewById(R.id.likeSongButton);
        lottieFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainActivity.isInMyFavorite){
                    lottieFavorite.setMinAndMaxProgress(0.5f,1.0f);
                    lottieFavorite.playAnimation();
                    //mainActivity.clickedFavorite = false;
                    String songName = musicTitle.getText().toString();
                    mainActivity.removeFromFavorite(songName);
                }
                else{
                    String songName = musicTitle.getText().toString();
                    if(songName != null && !songName.equals("No Song Is Playing")) {
                        lottieFavorite.setMinAndMaxProgress(0.0f, 0.5f);
                        lottieFavorite.playAnimation();
                        //mainActivity.clickedFavorite = true;
                        mainActivity.addToFavorite(songName);
                    }
                }
            }
        });

        badge1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                badge1.setMinAndMaxProgress(0.0f,1.0f);
                badge1.playAnimation();
                Toast.makeText(mainActivity.getApplicationContext(), "Weekly Active Runner",Toast.LENGTH_SHORT).show();
            }
        });

        badge2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                badge2.setMinAndMaxProgress(0.0f,1.0f);
                badge2.playAnimation();
                Toast.makeText(mainActivity.getApplicationContext(), "Global Leaderboard Champion",Toast.LENGTH_SHORT).show();
            }
        });

        badge3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                badge3.setMinAndMaxProgress(0.0f,1.0f);
                badge3.playAnimation();
                Toast.makeText(mainActivity.getApplicationContext(), "Daily Login Badge!",Toast.LENGTH_SHORT).show();
            }
        });

        badge4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                badge4.setMinAndMaxProgress(0.0f,1.0f);
                badge4.playAnimation();
                Toast.makeText(mainActivity.getApplicationContext(), "Local Leaderboard Champion",Toast.LENGTH_SHORT).show();
            }
        });

        lottieLocation = root.findViewById(R.id.lottie_island);
        lottieLocation.setRepeatCount(LottieDrawable.INFINITE);
        lottieLocation.playAnimation();

        lottieRunning = root.findViewById(R.id.lottie_running);
        lottieRunning.setRepeatCount(LottieDrawable.INFINITE);
        lottieRunning.playAnimation();

        ObjectAnimator animator = ObjectAnimator.ofFloat(lottieRunning, "translationX", -650.0f, 650.0f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(4000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();

        lottieHeartbeat = root.findViewById(R.id.lottie_heartbeat);
        lottieHeartbeat.setRepeatCount(LottieDrawable.INFINITE);
        lottieHeartbeat.playAnimation();

        // User BPM & music player
        myBPM_tv = root.findViewById(R.id.myBPM_tv);
        getBPM = root.findViewById(R.id.getBPM_btn);
        musicTitle = root.findViewById(R.id.musicTitle_tv);
        musicTitle.setSelected(true);
        musicArtist = root.findViewById(R.id.musicArtist_tv);
        musicBPM = root.findViewById(R.id.musicBPM);
        getBPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calculate BPM
                startCounter = mainActivity.stepCount;
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        endCounter = mainActivity.stepCount;
                        mainActivity.calculatedBPM = (endCounter-startCounter)*12;
                        myBPM_tv.setText(String.valueOf(mainActivity.calculatedBPM));
                        //filter music from data base and save original songs in dbList
                        mainActivity.filterMusicByBpm(mainActivity.calculatedBPM);
                    }
                }, 5000);
            }
        });

        // update music information thread: frequency 1s
        new Thread(() -> {
            while (true) {
                try {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // update greeting ui
                            greeting_tv.setText(mainActivity.greetingMsg);
                            location.setText(mainActivity.userCity);
                            // update music info
                            updateMusicInfo();
                            motivation_tv.setText("You have run " + mainActivity.df.format(mainActivity.miles) + " miles, \ntotal steps of " + String.valueOf(mainActivity.stepCount) + "!");
                            //  check the current song is in the favorite list or not
                            String songName = musicTitle.getText().toString();
                            mainActivity.checkIsFavorite(songName);
                            if(mainActivity.isInMyFavorite){
                                lottieFavorite.setFrame(30);
                            }else{
                                //lottieFavorite.setFrame(0);
                            }
                        }
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        //initialize fragment view
        myBPM_tv.setText(String.valueOf(mainActivity.calculatedBPM));
        temperature_tv.setText(mainActivity.df.format(mainActivity.temperature) + " °C");
        humidity_tv.setText(mainActivity.humidity+ "%");
        description_tv.setText(mainActivity.description);

        if(mainActivity.clickedFavorite){
            lottieFavorite.setFrame(30);
        }
        else{
            lottieFavorite.setFrame(0);
        }
        badge1.setFrame(50);
        badge2.setFrame(50);
        badge3.setFrame(50);
        badge4.setFrame(50);

//        new Thread(() -> {
//                    mainActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            getWeather_btn.performClick();
//                        }
//                    });
//        }).start();


//        new Thread(() -> {
//                try {
//                    mainActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            getWeather_btn.performClick();
//                        }
//                    });
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//        }).start();

        return root;

    }



    public void updateMusicInfo(){
        if(mainActivity.jcPlayerView.isPlaying()){
            String name = mainActivity.jcPlayerView.getCurrentAudio().getTitle();
            for(Song s: dbList){
                if(s.getName().equals(name)){
                    musicBPM.setText("BPM: " + s.getBpm());
                    musicTitle.setText(s.getName());
                    musicArtist.setText(s.getArtist());
                }
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

