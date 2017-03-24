package com.logan20.draganddropgame;

/*created by jsj */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class InstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
    }

    public void back(View view) {
        super.onBackPressed();
    }
}
