package com.sashafierce.moodsplash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.google.android.gms.internal.zzs.TAG;
import static java.security.AccessController.getContext;

public class FollowActivity extends Activity implements View.OnClickListener {
    private EditText ETemail;
    private DBManager dbManager;
    private DatabaseHelper databaseHelper;
    private List<String> list;
    private Button followBtn;
    String username;
    String email;
    boolean flag;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private DatabaseReference childref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        followBtn = (Button) findViewById(R.id.follow_button);
        followBtn.setOnClickListener(this);
        ETemail = (EditText) findViewById(R.id.email);
        ref = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        list = new ArrayList<String>();
        dbManager = new DBManager(this);
        databaseHelper = new DatabaseHelper(this);
        dbManager.open();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.follow_button:
                email = ETemail.getText().toString();
                username = "";
                for (int i = 0; i < email.length(); i++) {
                    char c = email.charAt(i);

                    if (c == '@') break;
                    if (c == '.' || c == '#' || c == '$' || c == '[' || c == ']') ;
                    else username += Character.toString(c);

                }
                flag = checkDatabase(username);


              this.returnHome();
                break;
        }
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }
    public boolean checkDatabase(final String username) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(username)) {
                    Toast.makeText(getApplicationContext(), "member doesnot exists or list is empty", Toast.LENGTH_LONG).show();
                    flag = false;
                }
                else {
                    ref = FirebaseDatabase.getInstance().getReference(username);
                    childref = ref.child(username);

                    final GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                    };
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            long numChildren = dataSnapshot.getChildrenCount();
                            list = dataSnapshot.getValue(t);
                            for (int i = 0; i < list.size(); i++) {
                                dbManager.insert(list.get(i));
                            }
                            Log.d(TAG, "no of children " + numChildren);
                            Log.d(TAG, "list  :  " + list);
                            Toast.makeText(getApplicationContext(), "List synced with " + username + " !", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.e(TAG, "Failed to read app title value.", error.toException());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        return flag;
    }

}
