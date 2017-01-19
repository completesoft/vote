package com.vote;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prmja.http.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    final String FILENAME = "file";
    final String LOG_TAG = "myLogs";
    final String ID = "1";
    int HIVE_INTERVAL = 60; //
    int COUNT_DOWN = 60;
    final String FINISH_CODE = "llrrllr";
    String CompareFinishCode = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vote_page);
        setFullScreen();

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

    //Write File (MODE_APPEND)
    void writeFile(String line) {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_APPEND)));
            // пишем данные
            bw.write(line);
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан_1");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        String s = "Name"+"#"+etName.getText().toString()+"#"+"Phone"+"#"+etPhone.getText().toString()+"#"+"Message"+"#"+etMessage.getText().toString()+'\n';
        writeFile(s);




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


    public void sendMesSetLayout(){


        setContentView(R.layout.thanx);

        String str = "";
        String res = "";

            try {
                // открываем поток для чтения
                BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(FILENAME)));

                // читаем содержимое
                while ((str = br.readLine() ) != null) {
                    //Log.d(LOG_TAG, str);
                    String[] params = str.split("#");
                    try {
                        res = prmja_com.Post("http://vote.product.in.ua/r.php", params);
                        Log.d(LOG_TAG, "Три раза");
                        if(!res.equals("ok")) break;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }
                //Deleting if file send
                Log.d(LOG_TAG, "перед удалением "+res);
                if(res.equals("ok")) {
                    Log.d(LOG_TAG, "file delete");
                    File file = new File(getFilesDir(),FILENAME);
                    file.delete();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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

    String mark="0";

        switch(view.getId()){
            case R.id.button:
                mark="1";
                break;
            case R.id.button8:
                mark="2";
                break;
            case R.id.button9:
                mark="3";
                break;
            case R.id.button10:
                mark="4";
                break;
            case R.id.button11:
                mark="5";
                break;
        }
        String s= "Mark"+"#"+mark+'\n';
        writeFile(s);

        sendMesSetLayout();
    }

    public void onClickShowForm(View view) {
        setContentView(R.layout.activity_main);
        //int cnd=59;

        final TextView tvCountDown= (TextView) findViewById(R.id.tvCountDown);
        new CountDownTimer(COUNT_DOWN*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                if (millisUntilFinished <10000) {
                    tvCountDown.setText("00:0"+millisUntilFinished / 1000);
                }
                            else {
                    tvCountDown.setText("00:"+millisUntilFinished / 1000);
                }

            }
            @Override
            public void onFinish() {

                hideKeyboard();
                setContentView(R.layout.vote_page);
            }
        }.start();

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
        if(FINISH_CODE.equals(CompareFinishCode)) this.finish();

    }
}
