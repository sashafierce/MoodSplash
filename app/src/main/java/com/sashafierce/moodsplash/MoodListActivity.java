package com.sashafierce.moodsplash;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.internal.zzs.TAG;

public class MoodListActivity extends AppCompatActivity {

    final String[] from = new String[] { DatabaseHelper._ID,
            DatabaseHelper.SUBJECT };
    final int[] to = new int[] { R.id.id, R.id.title };
    private DBManager dbManager;
    private ListView listView;
    private SimpleCursorAdapter adapter;
    private List<String> list ;
    private DatabaseHelper databaseHelper;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    String username , email;
    private DatabaseReference childref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_empty_list);
        Intent intent = getIntent();

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();


        ref =  FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        list = new ArrayList<String>();
        databaseHelper = new DatabaseHelper(this);
          listView = (ListView) findViewById(R.id.list_view);
        listView.setEmptyView(findViewById(R.id.empty));
       /* username = "";
        for (int i = 0; i < email.length(); i++) {
            char c = email.charAt(i);

            if (c == '@') break;
            if (c == '.' || c == '#' || c == '$' || c == '[' || c == ']') ;
            else username += Character.toString(c);

        }
        ref = FirebaseDatabase.getInstance().getReference(username);
        //childref = ref.child(username);

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
                //Toast.makeText(getApplicationContext(), "List synced with " + username + " !", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });*/
         adapter = new SimpleCursorAdapter(this, R.layout.activity_view_mood, cursor, from, to, 0);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);



        // OnCLickListiner For List Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
                TextView idTextView = (TextView) view.findViewById(R.id.id);
                TextView titleTextView = (TextView) view.findViewById(R.id.title);

                String id = idTextView.getText().toString();
                String title = titleTextView.getText().toString();

                Intent modify_intent = new Intent(getApplicationContext(), ModifyMoodActivity.class);
                modify_intent.putExtra("title", title);
                modify_intent.putExtra("id", id);
                startActivity(modify_intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add_record) {

            Intent add_mem = new Intent(this, AddMoodActivity.class);
            startActivity(add_mem);

        }
        else if(id == R.id.clear_list){           //

            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            db.execSQL("delete from MOODS");
            //clear firebase data
            FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
            String email = user.getEmail();

            String username = "";
            for (int i=0; i<email.length(); i++) {
                char c = email.charAt(i);

                if(c == '@') break;
                if(c == '.' || c == '#' || c == '$' || c == '[' || c == ']' ) ;
                else username += Character.toString(c);

            }
            ref.child(username).setValue(list);
          //  listView.setEmptyView(findViewById(R.id.empty));
           Cursor cursor = dbManager.fetch();
            adapter = new SimpleCursorAdapter(this, R.layout.activity_view_mood, cursor, from, to, 0);

            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);

        }
        return super.onOptionsItemSelected(item);
    }

}