package com.vote;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.prmja.http.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


class UserMessage {
    String name;
    String tel;
    String message;
}


class Vote {
    int value;
}


public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";
    final String ID = "1";
    int HIVE_INTERVAL = 60; //
    int COUNT_DOWN = 90;
    final String FINISH_CODE = "llrrllr";
    String CompareFinishCode = "";

    ArrayList<Vote> votes = new ArrayList<Vote>();
    ArrayList<UserMessage> messages = new ArrayList<UserMessage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vote_page);
        setFullScreen();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        // Создаем таймер
        Timer myTimer = new Timer();
        final Handler uiHandler = new Handler();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        time(ID);
                        Log.d(LOG_TAG,"task");
                    }
                });
            }
        }, 0L, HIVE_INTERVAL* 1L * 1000);
    }


    private void setFullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


//Save to file complaint and toggle layout
    public void onClickSend(View view) {

        hideKeyboard();

        EditText etName = (EditText) findViewById(R.id.editText6);
        EditText etPhone = (EditText) findViewById(R.id.editText7);
        EditText etMessage = (EditText) findViewById(R.id.editText8);

        //Fill text field if is empty
        if(etName.getText().length()==0) etName.setText("noname");
        if(etPhone.getText().length()==0) etPhone.setText("none");
        if(etMessage.getText().length()==0) etMessage.setText("message");

        UserMessage tempMessage = new UserMessage();
        tempMessage.name = etName.getText().toString();
        tempMessage.tel = etPhone.getText().toString();
        tempMessage.message = etMessage.getText().toString();
        messages.add(tempMessage);

        sendMesSetLayout();
    }


    public void hideKeyboard(){

        Log.d(LOG_TAG, "hideKeyboard()");

        View c_view = this.getCurrentFocus();
        if (c_view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(c_view.getWindowToken(), 0);
        }
    }



    public boolean sendToServer(String[] parameters) {

        String res = "";

        try {
            res = prmja_com.Post("http://vote.product.in.ua/r.php", parameters);
            Log.d(LOG_TAG, "RES = "+res);
            if(!res.equals("ok")) return false;
        } catch (ExecutionException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }

        return true;
    }

    public void sendMesSetLayout(){


        setContentView(R.layout.thanx);

        for (Vote vote: votes) {
            Log.d(LOG_TAG, "VOTE "+vote.value);
            String[] tempParams = new String[2];
            tempParams[0] = "Mark";
            tempParams[1] = String.valueOf(vote.value);
            if (sendToServer(tempParams)) {
                votes.remove(vote);
            }
            else {
                Log.d(LOG_TAG, "Fail send vote to server");
            }
        }

        for (UserMessage message: messages) {
            Log.d(LOG_TAG, "Message: "+message.name);
            String[] tempParams = new String[6];
            tempParams[0] = "Message";
            tempParams[1] = message.message;
            tempParams[2] = "Name";
            tempParams[3] = message.name;
            tempParams[4] = "Phone";
            tempParams[5] = message.tel;

            if (sendToServer(tempParams)) {
                messages.remove(message);
            }
            else {
                Log.d(LOG_TAG, "Fail send message to server");
            }
        }
        

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                setContentView(R.layout.vote_page);
            }
        }.start();
    }

//Save to file mark from "Vote"
    public void onClick_v1(View view) {


        Vote tempVote = new Vote();
        tempVote.value = 0;

        switch(view.getId()){
            case R.id.button:
                tempVote.value = 1;
                break;
            case R.id.button8:
                tempVote.value = 2;
                break;
            case R.id.button9:
                tempVote.value = 3;
                break;
            case R.id.button10:
                tempVote.value = 4;
                break;
            case R.id.button11:
                tempVote.value = 5;
                break;
        }

        votes.add(tempVote);

        if (tempVote.value == 1) {
            Log.d(LOG_TAG, "showForm()");
            showForm();
        }
        else {
            sendMesSetLayout();
        }


    }


    public void showForm(){
        setContentView(R.layout.activity_main);

        final TextView tvCountDown= (TextView) findViewById(R.id.tvCountDown);
        new CountDownTimer(COUNT_DOWN*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                String time = String.format("%02d:%02d", millisUntilFinished / 60000, (millisUntilFinished % 60000) / 1000);
                tvCountDown.setText(time);

            }
            @Override
            public void onFinish() {

                hideKeyboard();
                setContentView(R.layout.vote_page);
            }
        }.start();
    }

    public void onClickShowForm(View view) {
        showForm();
    }

    //Send ID and timeStamp
    public String time(String ID) {
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(date);
        String [] timeStamp={"ID", ID, "timestamp", dateString};
        //Log.d(LOG_TAG, dateString);
        String res = "";

        try {
             //HTTP Post Method
             res = prmja_com.Post("http://hive.product.in.ua/alive/", timeStamp);

            } catch (ExecutionException e) {
                e.printStackTrace();
                //Log.d(LOG_TAG, "1");
            } catch (InterruptedException e) {
                e.printStackTrace();
                //Log.d(LOG_TAG, "2");
        }
        return res;
    }

    public void onClickFinish(View view) {

        switch (view.getId()){
            case R.id.btnLeft:
                CompareFinishCode+='l';
                break;
            case R.id.btnCancel:
                CompareFinishCode="";
                break;
            case R.id.btnRight:
                CompareFinishCode+='r';
                break;
        }
        Log.d(LOG_TAG, CompareFinishCode);
        //if(FINISH_CODE.equals(CompareFinishCode)) this.finish();
        if(FINISH_CODE.equals(CompareFinishCode)) startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }

//refactor Back soft-button
    @Override
    public void onBackPressed() {
        // soft button Back - do nothing
    }

//Close all system dialogs
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            Intent closeDialog =
                    new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

//app became a block-screen
    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

}
