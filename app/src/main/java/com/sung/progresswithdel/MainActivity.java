package com.sung.progresswithdel;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button start,del;
    private boolean READY_TO_DELETE = true;
    private boolean RECORDING_PAUSE = true;
    private MediaObject mediaObject;
    private ProgressView mProgressView;
    private TextView logText;

    private static int RECORD_TIME_MAX = 30 * 1000;
    private static int RECORD_TIME_MIN = (int) (1.5f * 1000);

    MediaObject.MediaPart part;
    long mStartTime;
    long mStopTime;
    private final int HANDLER_UPDATE_DURATION = 0;

    private Handler updateDuration = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_UPDATE_DURATION:
                    mStopTime = System.currentTimeMillis();
                    part.duration = mStopTime - mStartTime;
                    logText.append("updating...duration = "+part.duration+"\n");
                    sendEmptyMessageDelayed(0,500);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaObject != null)
            return;

        mediaObject = new MediaObject();
        mProgressView.setData(mediaObject);

        if (logText == null)
            return;

        logText.append("RECORD_TIME_MAX:"+RECORD_TIME_MAX+"(long)\nRECORD_TIME_MIN:"+RECORD_TIME_MIN+"(long)\n");
        logText();
    }

    private void logText(){
        if (mediaObject != null && mediaObject.getMediaParts().size()!=0){
            logText.append("exsit part...\n");
            for (int i = 0; i < mediaObject.getMediaParts().size(); i++) {
                logText.append("position:"+i+"\tpartduration:"+mediaObject.getMediaParts().get(i).duration+"(long)\n");
            }
        }else {
            logText.append("empty part lsit!");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mProgressView.stop();
        updateDuration.removeCallbacksAndMessages(null);
    }

    private void initView(){
        mProgressView = (ProgressView) findViewById(R.id.pv_progress);
        logText = (TextView) findViewById(R.id.tv_log);
        del = (Button) findViewById(R.id.btn_del);
        start = (Button) findViewById(R.id.btn_start);
        del.setOnClickListener(this);
        start.setOnClickListener(this);

        logText.setText("log here...\n");
        mProgressView.setMaxDuration(RECORD_TIME_MAX);
        mProgressView.setMinTime(RECORD_TIME_MIN);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                cancelDelete();
                start.setText(start.getText().toString().equals("pause")?"start":"pause");

                if (RECORDING_PAUSE)
                    start();
                else
                    pause();

                RECORDING_PAUSE = !RECORDING_PAUSE;
                break;
            case R.id.btn_del:
                if (!RECORDING_PAUSE)
                    pause();

                if (READY_TO_DELETE)
                    prepareDelete();
                else
                    cancelDelete();
                break;
            default:
                break;
        }
    }

    private void alert(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("删除？").setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logText.append("no execute del!");
            }
        }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delete();
            }
        }).create();
        alertDialog.show();
    }

    private void prepareDelete(){
        if (mediaObject.getMediaParts().size()==0) {
            logText.append("MediParts size = 0\n");
            return;
        }

        alert();
        del.setText("cancel");
        READY_TO_DELETE = false;
        MediaObject.MediaPart part = mediaObject.getMediaParts().get(mediaObject.getMediaParts().size() - 1);
        part.remove = true;

        logText.append("prepareDelete last part (duaration:"+mediaObject.getMediaParts().get(mediaObject.getMediaParts().size()-1).duration+")\n");
    }

    private void delete(){
        if (mediaObject.getMediaParts().size()==0) {
            logText.append("MediParts size = 0\n");
            return;
        }

        READY_TO_DELETE = true;
        logText.append("delete begain...");
        mediaObject.getMediaParts().remove(mediaObject.getMediaParts().get(mediaObject.getMediaParts().size() - 1));
        logText();
    }

    private void cancelDelete(){
        del.setText("delete");
        READY_TO_DELETE = true;
        logText.append("cancel delete\n");
        if (mediaObject.getMediaParts().size()==0) {
            logText.append("mediaparts size = 0\n");
            return;
        }

        MediaObject.MediaPart part = mediaObject.getMediaParts().get(mediaObject.getMediaParts().size() - 1);
        part.remove = false;
    }

    private void start(){
        part = new MediaObject.MediaPart();
        mStartTime = System.currentTimeMillis();
        updateDuration.sendEmptyMessageDelayed(HANDLER_UPDATE_DURATION,500);
        part.remove = false;
        mediaObject.partList.add(part);
        logText.append("add new part to partlist...\n");

        mProgressView.start();
        logText.append("progressview start update progress!\n");
    }

    private void pause(){
        updateDuration.removeMessages(HANDLER_UPDATE_DURATION);

        part = null;
        mProgressView.stop();
        logText.append("pause progress,save duration to part\n");
    }
}
