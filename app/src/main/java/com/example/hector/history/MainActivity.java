package com.example.hector.history;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    final RabbitController mensaje = new RabbitController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(true){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        setContentView(R.layout.activity_main);
        Button enviar = (Button) findViewById(R.id.button);
        final EditText text = (EditText) findViewById(R.id.editText);
        final ListView lista = (ListView) findViewById(R.id.lista);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list);
        lista.setAdapter(adapter);
        mensaje.publishToAMQP();

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = msg.getData().getString("msg");
                adapter.add(message);
                lista.setSelection(adapter.getCount() - 1);
            }
        };
        mensaje.subscribe(incomingMessageHandler);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m = text.getText().toString();
                if (!m.equals("")){
                    mensaje.publishMessage(m);
                    text.setText("");
                    lista.setSelection(adapter.getCount() - 1);

                }
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mensaje.publishThread.interrupt();
        mensaje.subscribeThread.interrupt();
    }
}
