package com.example.mqtt_test;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public static final String BROADCAST_ACTION = "com.example.corn";
    private BroadcastReceiver mBroadcastReceiver;

    //private List<item> itemList1 = new ArrayList<item>();
    //private item item = new item("");
    private String string = "1";
    private String string1 = "1";
    private double[] temp = new double[12];
    private double[] temp1 = new double[12];
    private double[] temp_x = new double[12];
    private int length = 0;

    private Myapplication myapplication;

    private EditText edit_topic_publish;
    private EditText edit_msg_publish;
    private EditText edit_topic_sub;
    private EditText info1;
    private EditText info2;
    private MQTTManager mqttManager = MQTTManager.getInstance(this);
    private MqttCallback callback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String str1 = new String(message.getPayload());
            String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();

            myapplication = (Myapplication) getApplication();

            //string = str1;
            if(topic.equals("Hum") ){
                //将Hum,Tem放入全局变量
                myapplication.setHum(Double.parseDouble(string));
                string = str1;
            }else if(topic.equals("Tem")){
                myapplication.setTem(Double.parseDouble(string1));
                string1 = str1;
            }
            Log.d("messageArrived:" , str1);
            Log.i(TAG, str2);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.d("发布消息成功的回调", "ok");
        }

        @Override
        public void connectionLost(Throwable arg0) {
            // 失去连接，重连
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        mqttManager.setMessageCallBack(callback);
        mqttManager.connect();

        Button publish = (Button) findViewById(R.id.publish);
        Button subscribe = (Button) findViewById(R.id.subscribe);
        Button update = (Button) findViewById(R.id.update);
        ImageButton chart = (ImageButton) findViewById(R.id.chart);
        edit_topic_publish = (EditText) findViewById(R.id.topic);
        edit_msg_publish = (EditText) findViewById(R.id.message);
        edit_topic_sub = (EditText) findViewById(R.id.topic_sub);
        info1 = (EditText) findViewById(R.id.info);
        info2 = (EditText) findViewById(R.id.info2);

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_topic = edit_topic_publish.getText().toString();
                String str_msg = edit_msg_publish.getText().toString();
                if(str_msg!="" && str_topic!=""){
                    mqttManager.publish(str_topic,str_msg, true, 1);
                }else{
                    Toast.makeText(MainActivity.this, "信息不完整", Toast.LENGTH_SHORT).show();
                }

            }
        });

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_topic_sub = edit_topic_sub.getText().toString();
                mqttManager.subscribeMsg(str_topic_sub, 1);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(length == 12){
                    length = 0;
                    for(int i=0 ; i<12 ; i++){
                        temp[i] = 0;
                        temp1[i] = 0;
                        temp_x[i] = 0;
                    }
                }


                info1.setText("Humdity(%):" + string);
                //temp[length] = Double.parseDouble(string);
                info2.setText("Temperature(oC):" + string1);
                //temp1[length] = Double.parseDouble(string1);
                //temp_x[length] = length;
                //length++;
            }
        });

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, SuscribeActivity.class);
                //intent.putExtra("value", temp);
                //intent.putExtra("value1", temp1);
                //intent.putExtra("value_x", temp_x);
                startActivity(intent);
            }
        });



        //Adapter adapter = new Adapter(MainActivity.this, R.layout.item, itemList1);
        //ListView listView1 = (ListView) findViewById(R.id.list_view);
        //listView1.setAdapter(adapter);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
