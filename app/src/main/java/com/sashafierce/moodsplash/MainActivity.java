package com.sashafierce.moodsplash;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //called when settings button clicked
    public void setting(View view) {
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }
    //called when edit mood button clicked
    public void editMood(View view) {
        Intent intent = new Intent(this, MoodListActivity.class);

        startActivity(intent);


    }

}
