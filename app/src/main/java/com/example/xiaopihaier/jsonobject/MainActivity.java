package com.example.xiaopihaier.jsonobject;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static final int SHOW_PESPONSE = 0;
    Button sendRequest;
    TextView responseText;
    String BookName,Author;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_PESPONSE:
                    String response = (String) msg.obj;
                    parseJSONWithJSONObjiect(response);
            }
        }
    };

    private void parseJSONWithJSONObjiect(String jsonData) {
        try {
            //JSONArray jsonArray = new JSONArray(jsonData);
            //for (int i = 0; i < jsonArray.length(); i++) {
                //JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject jsonObject=new JSONObject(jsonData);
            BookName = jsonObject.getString("BookName");
            Author = jsonObject.getString("Author");
                responseText.setText("书名:"+BookName+","+"作者:"+Author);
            } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendRequest = (Button) findViewById(R.id.send_request);
        responseText = (TextView) findViewById(R.id.response_text);
        sendRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_request) {
            sendRequestWithHttpURLConnection();
        }
    }


    //网路请求
    private void sendRequestWithHttpURLConnection() {
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://10.0.2.2/1.json");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(1000);
                    connection.setReadTimeout(1000);
                    InputStream inputStream = connection.getInputStream();
                    //下面对获取到的输入流读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message = new Message();
                    message.what = SHOW_PESPONSE;
                    //将服务器返回的结果存放到message中
                    message.obj = response.toString();
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
