package iot.tdmu.edu.vn.smartteddy.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import iot.tdmu.edu.vn.smartteddy.app.R;

/**
 * Created by nguye on 3/10/2017.
 *
 */

public class ScanResultAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<ScanResult> mResults;

    public ScanResultAdapter(Context context, List<ScanResult> results){
        this.mContext = context;
        this.mResults = results;
    }
    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public Object getItem(int position) {
        return mResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScanResult result = mResults.get(position);
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.net_list_row,null);
        }
        TextView txtSSID = (TextView) convertView.findViewById(R.id.txtSSID);
        TextView txtCapabilities = (TextView) convertView.findViewById(R.id.txtCapabilities);
        TextView txtBSSID = (TextView) convertView.findViewById(R.id.txtBSSID);

        txtSSID.setText("SSID::" +result.SSID);
        txtBSSID.setText("BSSID::"+result.BSSID);
        txtCapabilities.setText("capabilities::"+ result.capabilities);

        return convertView;
    }
}
