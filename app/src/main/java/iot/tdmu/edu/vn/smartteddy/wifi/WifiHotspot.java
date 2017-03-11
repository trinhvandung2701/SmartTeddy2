package iot.tdmu.edu.vn.smartteddy.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by nguye on 3/10/2017.
 *
 */

public class WifiHotspot {
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    Context context;
    List<ScanResult> results;
    ListView netWorksList;
    ScanResultAdapter scanResultAdapter;
    WifiReceiver receiver;
    public static boolean isConnectToHotSpotRunning=false;

    public WifiHotspot(Context c){
        context = c;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
    }

    public boolean connectToHotspot(String netSSID, String netPass) {

        isConnectToHotSpotRunning= true;
        WifiConfiguration wifiConf = new WifiConfiguration();
        List<ScanResult> scanResultList=wifiManager.getScanResults();

        if(wifiManager.isWifiEnabled()){

            for (ScanResult result : scanResultList) {

                if (result.SSID.equals(netSSID)) {

                    removeWifiNetwork(result.SSID);
                    String mode = getSecurityMode(result);

                    if (mode.equalsIgnoreCase("OPEN")) {

                        wifiConf.SSID = "\"" + netSSID + "\"";
                        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        int res = wifiManager.addNetwork(wifiConf);
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(res, true);
                        wifiManager.reconnect();
                        wifiManager.setWifiEnabled(true);
                        isConnectToHotSpotRunning=false;
                        return true;

                    } else if (mode.equalsIgnoreCase("WEP")) {

                        wifiConf.SSID = "\"" + netSSID + "\"";
                        wifiConf.wepKeys[0] = "\"" + netPass + "\"";
                        wifiConf.wepTxKeyIndex = 0;
                        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                        int res = wifiManager.addNetwork(wifiConf);
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(res, true);
                        wifiManager.reconnect();
                        wifiManager.setWifiEnabled(true);
                        isConnectToHotSpotRunning=false;
                        return true;

                    }else{

                        wifiConf.SSID = "\"" + netSSID + "\"";
                        wifiConf.preSharedKey = "\"" + netPass + "\"";
                        wifiConf.hiddenSSID = true;
                        wifiConf.status = WifiConfiguration.Status.ENABLED;
                        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        int res = wifiManager.addNetwork(wifiConf);
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(res, true);
                        wifiManager.reconnect();
                        wifiManager.saveConfiguration();
                        wifiManager.setWifiEnabled(true);
                        isConnectToHotSpotRunning=false;
                        return true;

                    }
                }
            }
        }
        isConnectToHotSpotRunning=false;
        return false;
    }

    public boolean isConnectedToAP(){
        ConnectivityManager connectivity = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public ScanResult getHotspotMaxLevel(){
        List<ScanResult> hotspotList=wifiManager.getScanResults();
        if (hotspotList != null) {
            final int size = hotspotList.size();
            if (size == 0){
                return null;
            } else {
                ScanResult maxLevel = hotspotList.get(0);
                for (ScanResult result : hotspotList) {
                    if (WifiManager.compareSignalLevel(maxLevel.level,
                            result.level) < 0) {
                        maxLevel = result;
                    }
                }
                return maxLevel;
            }
        }else{
            return null;
        }
    }

    public List<ScanResult> getHotspotsList(){

        if(wifiManager.isWifiEnabled()) {

            if(wifiManager.startScan()){
                return wifiManager.getScanResults();
            }

        }
        return null;
    }

    public void showHotspotsList(ListView List){
        if(wifiManager.isWifiEnabled()) {
            receiver = new WifiReceiver();
            scanNetworks();
            netWorksList = netWorksList==null ? List : netWorksList;
            if(results!=null){
                scanResultAdapter = new ScanResultAdapter(context,results);
                netWorksList.setAdapter(scanResultAdapter);
            }
        }
        else Toast.makeText(context,"wifi is not enabled", Toast.LENGTH_LONG).show();
    }

    private void scanNetworks() {
        boolean scan = wifiManager.startScan();

        if(scan) {
            results = wifiManager.getScanResults();

        } else
            switch(wifiManager.getWifiState()) {
                case WifiManager.WIFI_STATE_DISABLING:
                    Toast.makeText(context,"wifi disabling", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    Toast.makeText(context, "wifi disabled", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Toast.makeText(context, "wifi enabling", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Toast.makeText(context, "wifi enabled", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    Toast.makeText(context,"wifi unknown state", Toast.LENGTH_LONG).show();
                    break;
            }

    }


    private String getSecurityMode(ScanResult result) {
        final String cap = result.capabilities;
        final String[] modes = {"WPA", "EAP","WEP" };
        for (int i = modes.length - 1; i >= 0; i--) {
            if (cap.contains(modes[i])) {
                return modes[i];
            }
        }
        return "OPEN";
    }

    private void removeWifiNetwork(String ssid) {
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                if (config.SSID.contains(ssid)) {
                    wifiManager.disableNetwork(config.networkId);
                    wifiManager.removeNetwork(config.networkId);
                }
            }
        }
        wifiManager.saveConfiguration();
    }

    class WifiReceiver extends BroadcastReceiver{

        public List<ScanResult> getResults() {
            return results;
        }

        public WifiManager getManager() {
            return wifiManager;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            scanResultAdapter = new ScanResultAdapter(context,results);
            netWorksList.setAdapter(scanResultAdapter);
            scanResultAdapter.notifyDataSetChanged();
        }
    }

}
