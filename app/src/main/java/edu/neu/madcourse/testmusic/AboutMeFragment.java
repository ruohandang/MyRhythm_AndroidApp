package edu.neu.madcourse.testmusic;
import edu.neu.madcourse.testmusic.databinding.FragmentAboutMeBinding;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class AboutMeFragment extends Fragment {
    private FragmentAboutMeBinding binding;
    Activity context;
    Button likedSongs, settings, privacy, logout;
    MainActivity mainActivity;



    public AboutMeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentAboutMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        likedSongs = root.findViewById(R.id.likedSongs_btn);
        settings = root.findViewById(R.id.setting_btn);
        privacy = root.findViewById(R.id.privacy_btn);
        logout = root.findViewById(R.id.signOut_btn);
        context = getActivity();
        mainActivity = (MainActivity) getActivity();


        likedSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mainActivity, FavoriteSongs.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDialog();

            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { privacyPopupDialog(); }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mainActivity, Login.class));
                FirebaseAuth.getInstance().signOut();
                //mainActivity.finish();
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showPopupDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mainActivity);
        alertDialogBuilder.setTitle("Change your city");
        alertDialogBuilder.setCancelable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(mainActivity);
        final View changeCityView = layoutInflater.inflate(R.layout.change_city, null);
        alertDialogBuilder.setView(changeCityView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button saveCityButton = changeCityView.findViewById(R.id.button_save_city);
        saveCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextName = changeCityView.findViewById(R.id.edit_city);
                String cityName = editTextName.getText().toString().trim();

                if (TextUtils.isEmpty(cityName)) {
                    // If name or URL is empty then popup a Snackbar.
                    Snackbar.make(v, "Please fill with new city.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    mainActivity.changeCity(cityName);
                    alertDialog.hide();
                }
            }
        });
        Button cancelWebButton = changeCityView.findViewById(R.id.button_cancel_city);
        cancelWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    private void privacyPopupDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mainActivity);
        alertDialogBuilder.setTitle("Privacy");
        alertDialogBuilder.setCancelable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(mainActivity);
        final View showPrivacy = layoutInflater.inflate(R.layout.privacy, null);
        alertDialogBuilder.setView(showPrivacy);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button cancelWebButton = showPrivacy.findViewById(R.id.closeprivacy_btn);
        cancelWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

//    public void onStart() {
//        super.onStart();
//        Button likedSongs  = (Button) context.findViewById(R.id.likedSongs_btn);
//        likedSongs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, LikedSongsActivity.class);
//                startActivity(intent);
//            }
//        });
//    }

}