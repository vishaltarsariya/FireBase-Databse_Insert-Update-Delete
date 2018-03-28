package com.example.vishal.firebase_database_app;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView TXT_DISPLAY;
    private EditText EDT_NAME;
    private EditText EDT_EMAIL;
    private Button BTN_SAVE;

    public static String TAG = MainActivity.class.getSimpleName();
    private DatabaseReference FIRE_DB;
    private FirebaseDatabase FIRE_INSTANCE;
    private String USER_ID;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();


        TXT_DISPLAY = (TextView) findViewById(R.id.txt_user);
        EDT_NAME = (EditText) findViewById(R.id.name);
        EDT_EMAIL = (EditText) findViewById(R.id.email);
        BTN_SAVE = (Button) findViewById(R.id.btn_save);

        FIRE_INSTANCE = FirebaseDatabase.getInstance();
        FIRE_DB = FIRE_INSTANCE.getReference("user");
        FIRE_INSTANCE.getReference("app_title").setValue("Realtime Database");

        FIRE_INSTANCE.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(getApplicationContext(), "App title Updateed", Toast.LENGTH_SHORT).show();
                String titile = dataSnapshot.getValue(String.class);
                ab.setTitle(titile);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed : " + databaseError.toException(), Toast.LENGTH_SHORT).show();
            }
        });

        BTN_SAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(USER_ID)){
                    Create_User(EDT_NAME.getText().toString().trim(), EDT_EMAIL.getText().toString().trim());
                }else{
                    Update_User(EDT_NAME.getText().toString().trim(), EDT_EMAIL.getText().toString().trim());
                }
            }
        });

    }

    private void Create_User(String name, String email) {
        if (TextUtils.isEmpty(USER_ID)) {
            USER_ID = FIRE_DB.push().getKey();
        }

        User user = new User(name, email);

        FIRE_DB.child(USER_ID).setValue(user);
        Add_UserChangeListner();
    }

    private void Add_UserChangeListner(){
        FIRE_DB.child(USER_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if(user==null){
                    Toast.makeText(getApplicationContext(),"User Data is Null",Toast.LENGTH_SHORT).show();
                }
                TXT_DISPLAY.setText(user.getName()+", "+user.getEmail());
                EDT_NAME.setText("");
                EDT_EMAIL.setText("");
                toggleButton();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    int i =1;
                    User user1 = dataSnapshot.getValue(User.class);
                    Log.i("--DISPLAY DATA : "+i, user1.getName()+" : "+user1.getEmail());
                    i++;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Update_User(String name, String email) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            FIRE_DB.child(USER_ID).child("name").setValue(name);

        if (!TextUtils.isEmpty(email))
            FIRE_DB.child(USER_ID).child("email").setValue(email);
    }

    private void toggleButton() {
        if (TextUtils.isEmpty(USER_ID)) {
            BTN_SAVE.setText("Save");
        } else {
            BTN_SAVE.setText("Update");
        }
    }

}
