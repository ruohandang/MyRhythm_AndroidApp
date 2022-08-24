package edu.neu.madcourse.testmusic;
import edu.neu.madcourse.testmusic.databinding.FragmentLeaderboardBinding;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LeaderboardFragment extends Fragment {
    private FragmentLeaderboardBinding binding;

    private RecyclerView leaderboard_recyclerView;
    private RelativeLayout relativeLayout;
    private LeaderBoardAdapter leaderBoardAdapter;
    private LinearLayoutManager linearLayoutManager;
    ArrayList<User> usersList;
    ArrayList<User> locallist;
    LinearLayout localLayout, globalLayout;
    Button local_btn, global_btn;
    MainActivity mainActivity;


    public LeaderboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // getMainActivity, if need any data from main activity, could write a function in main activity to return the needed data
        mainActivity = (MainActivity) getActivity();

        //myarraylist get the whole list from mainactivity in the beginning, set it up with this list when on create by pass 0 to setuprv()
        //but, on tab click, mainactivity will pass 1 or 0 to decide whether local or global

        usersList = mainActivity.usersList;
        locallist = mainActivity.localusersList;


        localLayout = binding.localLayout;
        globalLayout = binding.globalLayout;
        local_btn = binding.localBtn;
        global_btn = binding.globalBtn;

        if (usersList != null) {
            initLocalRecyclerView();
        }

        local_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mainActivity.getApplicationContext(), "I just clicked my local!",Toast.LENGTH_LONG).show();
                initLocalRecyclerView();
            }
        });

        global_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setUpRecyclerView(0);
                initGlobalRecyclerView();
                //Toast.makeText(mainActivity.getApplicationContext(), "I just clicked my global!",Toast.LENGTH_LONG).show();
            }
        });


        return root;
    }

    private void initGlobalRecyclerView() {
        leaderboard_recyclerView = binding.leaderboardRecycler;
        linearLayoutManager  = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        leaderboard_recyclerView.setLayoutManager(linearLayoutManager);
        leaderBoardAdapter = new LeaderBoardAdapter(getContext(), usersList);
        leaderboard_recyclerView.setAdapter(leaderBoardAdapter);
        leaderBoardAdapter.notifyDataSetChanged();
    }

    private void initLocalRecyclerView() {
        leaderboard_recyclerView = binding.leaderboardRecycler;
        linearLayoutManager  = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        leaderboard_recyclerView.setLayoutManager(linearLayoutManager);
        leaderBoardAdapter = new LeaderBoardAdapter(getContext(), locallist);
        leaderboard_recyclerView.setAdapter(leaderBoardAdapter);
        leaderBoardAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}