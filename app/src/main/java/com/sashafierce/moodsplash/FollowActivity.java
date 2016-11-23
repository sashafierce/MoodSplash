package com.sashafierce.moodsplash;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class FollowActivity extends Activity {
    private EditText ETemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);


    }
    public void importList(){
        String email = ETemail.getText().toString();

    }
}
