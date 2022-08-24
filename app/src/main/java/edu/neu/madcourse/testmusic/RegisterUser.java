package edu.neu.madcourse.testmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private TextView banner;
    private EditText editTextFullName, editTextCity, editEmail, editTextPassword;
    private ProgressBar progressBar;

    // for spinner
    private String selectedState;
    private TextView tvStateSpinner;
    private Spinner stateSpinner;
    private ArrayAdapter<CharSequence> stateAdapter;
    private Button registerBtn;
    DatabaseReference reference01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        mAuth = FirebaseAuth.getInstance();

        // for banner
        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerBtn = (Button) findViewById(R.id.registerUserBtn);
        registerBtn.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullNameEt);
        editTextCity = (EditText) findViewById(R.id.cityEt);
        editEmail = (EditText) findViewById(R.id.emailEt);
        editTextPassword = (EditText) findViewById(R.id.passwordEt);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        tvStateSpinner =(TextView) findViewById(R.id.stateTv);

        // state spinner Initial
        stateSpinner =(Spinner)findViewById(R.id.stateSpin);

        // populate array adapter sing string array and a spinner layout that we will define
        stateAdapter = ArrayAdapter.createFromResource(this, R.array.array_usa_states, R.layout.spinner_layout);

        //specify the layout to use when the list of choices appear
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // set the adapter to the spinner to populate the state spinner
        stateSpinner.setAdapter(stateAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState = stateSpinner.getSelectedItem().toString();
                tvStateSpinner.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.banner:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.registerUserBtn:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String userName = editTextFullName.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String state = selectedState;

        List<Song> playlist = new ArrayList<>();
        int step = 0;

        if(userName.isEmpty()){
            editTextFullName.setError("Full name is required!");
            editTextFullName.requestFocus();
            return;
        }

        if(selectedState.equals("Select Your State")){
            Toast.makeText(RegisterUser.this, "Please select your state", Toast.LENGTH_LONG).show();
            tvStateSpinner.setError("State is required"); // set error when submit is clicked without any state being selected
            tvStateSpinner.requestFocus();
            return;
        }

        if (city.isEmpty()){
            editTextCity.setError("City is required!");
            editTextCity.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editEmail.setError("Email is required!");
            editEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Please provide valid email!");
            editEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length()<6){
            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        String myId = createLeaderBoard(email,userName,state,city,step);

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(userName, state, city, email, step, myId);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                          if(task.isSuccessful()){
                                              Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                              progressBar.setVisibility(View.GONE);
                                              // redirect to login layout
                                              startActivity(new Intent(RegisterUser.this, Login.class));

                                          }else{
                                              Toast.makeText(RegisterUser.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                              progressBar.setVisibility(View.GONE);
                                          }
                                        }
                                    });
                        }else{
                            Toast.makeText(RegisterUser.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

    public String createLeaderBoard(String email, String name, String state, String city, int step){
        reference01 = FirebaseDatabase.getInstance().getReference();
        String id = reference01.push().getKey();
        User user = new User(name, state, city, email, step);
        reference01.child("NewLeaderBoard").child(id).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
        return id;
    }
}