package com.logan20.draganddropgame;


/*This is the activity where the user will be able to play the game and upon being successful
he/she can move to the next level
 */

/*created by jsj */


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PlayActivity extends AppCompatActivity {

    private final int MARGINSIZE = 3;
    private int currLevel;
    private JSONArray data;
    private GridLayout gl;
    private LinearLayout ll;
    private int w,h;
    private int currY;
    private int currX;
    private int destY;
    private int destX;
    private SoundPool sp;
    private int[]sounds;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        currLevel = -1;
        gl = (GridLayout)findViewById(R.id.mainPanel);
        ll =((LinearLayout)findViewById(R.id.viewPanel));
        mp = MediaPlayer.create(this,R.raw.bkg);
        mp.setLooping(true);
        mp.start();
        loadData();
        loadSounds();
        loadNextLevel();
        setDragAndDrop((com.logan20.draganddropgame.Item)findViewById(R.id.arrow_left));
        setDragAndDrop((com.logan20.draganddropgame.Item)findViewById(R.id.arrow_right));
        setDragAndDrop((com.logan20.draganddropgame.Item)findViewById(R.id.arrow_up));
        setDragAndDrop((com.logan20.draganddropgame.Item)findViewById(R.id.arrow_down));
        setDragAccept(findViewById(R.id.hsv_panel));

        /*all data loaded to process per instruction given*/
    }

    @Override
    protected void onDestroy() {
        sp.release();
        mp.stop();
        super.onDestroy();
    }

    private void loadSounds() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            sp = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(attrs)
                    .build();
        }
        else{
            sp = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        }

        sounds = new int[]{
                sp.load(this,R.raw.action_fail,1),
                sp.load(this,R.raw.action_1,1),
                sp.load(this,R.raw.action_2,1),
                sp.load(this,R.raw.action_success,1)
        };
    }
/*Drag and drop codes from various exmaples*/


    private void setDragAndDrop(ImageView iv) {
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String direction = view.getTag().toString();
                ClipData.Item item = new ClipData.Item(direction);
                ClipData data = new ClipData(direction,new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},item);
                View.DragShadowBuilder shadow = new DragShadow((ImageView)view);
                w=view.getWidth();
                h= view.getHeight();

                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                    view.startDragAndDrop(data,shadow,null,0);
                }
                else{
                    view.startDrag(data,shadow,null,0);

                    /*not neccessary */
                }
                sp.play(sounds[1],1.0f,1.0f,1,0,1.0f);
                return true;

            }
        });
    }

    private void loadNextLevel() {
        gl.removeAllViews();
        ll.removeAllViews();
        currLevel++;
        if (currLevel<data.length()){
            try {
                JSONObject obj = data.getJSONObject(currLevel);
                currY = obj.getInt("startRow");
                currX = obj.getInt("startCol");
                destX = obj.getInt("endCol");
                destY = obj.getInt("endRow");
                final int rowCount = obj.getInt("numRow");
                final int colCount = obj.getInt("numCol");
                final String levelLayout = obj.getString("data");
                gl.setRowCount(rowCount);
                gl.setColumnCount(colCount);

                gl.post(new Runnable() {
                    @Override
                    public void run() {
                        int glWidth = gl.getWidth();
                        int glHeight = gl.getHeight();
                        for (int a=0;a<rowCount;a++){
                            for(int b=0;b<colCount;b++){
                                ImageView v = new ImageView(PlayActivity.this);
                                v.setBackgroundColor(Color.TRANSPARENT);
                                if (levelLayout.charAt(a*colCount+b)=='1'){
                                    v.setBackgroundColor(Color.WHITE);
                                }
                                if (a*colCount+b ==currY*colCount+currX){
                                    v.setBackgroundColor(Color.RED);
                                }
                                if (a*colCount+b ==destY*colCount+destX){
                                    v.setBackgroundColor(Color.GREEN);
                                }
                                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                                params.width = glWidth/colCount - 2*MARGINSIZE;
                                params.height = glHeight / rowCount - 2*MARGINSIZE;
                                params.setMargins(MARGINSIZE,MARGINSIZE,MARGINSIZE,MARGINSIZE);

                                gl.addView(v,params);
                            }
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this, "Game completed! No more levels", Toast.LENGTH_SHORT).show();
            finish(); /*last level reached user will exit the game */
        }
    }

    private void loadData() {
        try {
            BufferedReader in= new BufferedReader(new InputStreamReader(getAssets().open("levels.json")));
            StringBuilder sb = new StringBuilder();
            String line = in.readLine();
            while(line!=null){
                sb.append(line);
                line = in.readLine();
            }
            in.close();
            data =new JSONArray(sb.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(PlayActivity.this)
            .setTitle("Exit?")
            .setMessage("Are you sure you want to exit?")
            .setNegativeButton(android.R.string.no,null)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    PlayActivity.super.onBackPressed();
                }
            })
            .show();
    }

    public void setDragAccept(final View dragAccept) {
        dragAccept.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        if (dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
                            view.setBackgroundColor(Color.parseColor("#88ffffff"));
                            view.invalidate();
                            return true;
                        }
                        return false;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        view.setBackgroundColor(Color.parseColor("#55ffffff"));
                        view.invalidate();
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        view.setBackgroundColor(Color.parseColor("#88ffffff"));
                        view.invalidate();
                        return true;
                    case DragEvent.ACTION_DROP:
                        ClipData.Item item = dragEvent.getClipData().getItemAt(0);
                        Item i = new Item(PlayActivity.this);
                        i.setTag(item.getText().toString());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,h);
                        params.setMargins(5,5,5,5);
                        switch(i.getTag().toString()){
                            case "up":
                                i.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.arrow_up));
                                break;
                            case "down":
                                i.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.arrow_down));
                                break;
                            case "left":
                                i.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.arrow_left));
                                break;
                            case "right":
                                i.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.arrow_right));
                                break;
                        }
                        ll.addView(i,params);
                        view.invalidate();
                        sp.play(sounds[2],1.0f,1.0f,1,0,1.0f);
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        view.setBackgroundColor(Color.parseColor("#88ffffff"));
                        view.invalidate();
                        return true;
                    default:
                        break;

                }
                return false;
            }
        });
    }

    public void checkSol(View view) {
        final ImageView iv = (ImageView) gl.getChildAt(currY*gl.getColumnCount() + currX);
        if (iv==null){
            failure();
            return;
        }
        iv.post(new Runnable() {
            @Override
            public void run() {
                iv.setBackgroundColor(Color.BLUE);
            }
        });
        if (currY*gl.getColumnCount() + currX == destY*gl.getColumnCount()+destX){
            success();
            return;
        }

        Item item = (Item)ll.getChildAt(0);
        if (item==null){
            failure();
            return;
        }
        String direction = item.getTag().toString();
        int nX=currX,nY=currY;
        switch (direction){
            case "up":
                nY--;
                break;
            case "down":
                nY++;
                break;
            case "left":
                nX--;
                break;
            case "right":
                nX++;
                break;
        }
        ImageView next = (ImageView) gl.getChildAt(nY*gl.getColumnCount() + nX);
        int nextColor = ((ColorDrawable)next.getBackground()).getColor();
        if (nextColor ==Color.WHITE || nextColor==Color.GREEN){
            currX=nX;
            currY=nY;
        }
        else{
            ll.post(new Runnable() {
                @Override
                public void run() {
                    ll.removeViewAt(0);
                }
            });
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkSol(null);
            }
        }).start();

    }

    private void success() {
        sp.play(sounds[3],1f,1f,1,0,1f); /*raise or lower the sound */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(PlayActivity.this)
                        .setTitle("Success")
                        .setMessage("Level complete")
                        .setPositiveButton("On to the next level!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loadNextLevel();
                            }
                        })
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    private void failure() {
        sp.play(sounds[0],1.0f,1.0f,1,0,1.0f);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(PlayActivity.this)
                        .setTitle("Fail")
                        .setMessage("Level failed")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                currLevel--;
                                loadNextLevel();
                            }
                        })
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }
}
