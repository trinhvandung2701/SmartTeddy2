package iot.tdmu.edu.vn.smartteddy.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import iot.tdmu.edu.vn.smartteddy.wifi.WifiHotspot;

public class RequestActivity extends AppCompatActivity {

    ListView lvListHotspot;
    WifiHotspot HU;
    Button btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HU = new WifiHotspot(RequestActivity.this);
                lvListHotspot = (ListView) findViewById(R.id.lvListHotspot);
                HU.showHotspotsList(lvListHotspot);
            }
        });

    }
}
