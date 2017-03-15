package iot.tdmu.edu.vn.smartteddy.app;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

public class ConnectTeddyActivity extends AppCompatActivity {

    Button btnConect;
    TextView txtSSID,txtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_teddy);

        btnConect = (Button) findViewById(R.id.btnConnect);
        txtSSID = (TextView) findViewById(R.id.txtNameWifi);
        txtPass = (TextView) findViewById(R.id.txtPasWifi);
        final Intent intent = getIntent();

        String ssid = intent.getStringExtra("SSID");
        txtSSID.setText(ssid);

        btnConect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ma = 1;
                Intent intent1 = new Intent(ConnectTeddyActivity.this,Send_Data_viaBTActivity.class);
                intent1.putExtra("MA",ma);
                intent1.putExtra("SSID",txtSSID.getText().toString());
                intent1.putExtra("PASS",txtPass.getText().toString());
                startActivity(intent1);

            }
        });
    }
}
