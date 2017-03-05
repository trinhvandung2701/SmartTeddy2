package iot.tdmu.edu.vn.smartteddy.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConnectTeddyActivity extends AppCompatActivity {

    Button btnConect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_teddy);

        btnConect = (Button) findViewById(R.id.btnConnect);
        btnConect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectTeddyActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
