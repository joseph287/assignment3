package com.logan20.draganddropgame;
/*created by jsj*/

/*hard asssignment prof lol */

/*most codes generated from android studio and web */



import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void play(View view) {
        startActivity(new Intent(MainActivity.this, PlayActivity.class));
    }

    public void exit(View view) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no,null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show(); /*option to enter game or exit game screen with instructions */
    }

    public void instructions(View view) {
        startActivity(new Intent(MainActivity.this,InstructionActivity.class));
    }
}
