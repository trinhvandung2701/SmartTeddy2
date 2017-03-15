package iot.tdmu.edu.vn.smartteddy.app;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import iot.tdmu.edu.vn.smartteddy.wifi.WifiHotspot;

public class RequestActivity extends AppCompatActivity {

    ListView lvListHotspot;
    WifiHotspot HU;
    Button btnScan,btnQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        btnScan = (Button) findViewById(R.id.btnScan);
        lvListHotspot = (ListView) findViewById(R.id.lvListHotspot);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HU = new WifiHotspot(RequestActivity.this);

                HU.showHotspotsList(lvListHotspot);
            }
        });
        HU = new WifiHotspot(RequestActivity.this);
        final List<ScanResult> list = HU.getHotspotsList();

        lvListHotspot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String a = list.get(position).SSID;

                Intent intent = new Intent(RequestActivity.this,ConnectTeddyActivity.class);
                intent.putExtra("SSID",a);
                startActivity(intent);
            }
        });

        btnQR = (Button) findViewById(R.id.btnQR);
        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestActivity.this,ConectTeddybyQR_Activity.class);
                startActivity(intent);
            }
        });
    }
}
