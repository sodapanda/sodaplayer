package info.sodapanda.sodaplayer.socket;

import info.sodapanda.sodaplayer.Sodaplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class ClientFactory {
    public static ClientFactory instance;
    public int retryCount = 0;
    // 所有的Client
    private Map<Integer, Client> clients = new HashMap<Integer, Client>();
    private Client current;
    private List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();

    private ClientFactory() {

    }

    public static void startNewClient() {
        if (isOpenNetwork(Sodaplayer.getAppCxt())) {
            if (instance.clients.size() == 1) {
                Client current = instance.getCurrent();
                if (instance.retryCount <= 20) {
                    Log.i("qicheng","retry to connect im server"+instance.retryCount);
                    current.disconnect();
                    current.connectSilent();
                    instance.retryCount++;
                } else {
                    Log.e("qicheng", "connect to server error");
                    instance.current.disconnect();
                }
            } else if (instance.clients.size() > 1) {
                if (instance.retryCount <= 20) {
                    Log.i("qicheng","retry to connect im server"+instance.retryCount);
                    instance.getNext();
                    instance.current.connectSilent();
                    instance.retryCount++;
                } else {
                    Log.e("qicheng", "circle connect to server error");
                    instance.current.disconnect();
                }
            }
        } else {
            Log.d("qicheng","network is not working");
        }
    }

    public void resetRetryCount(){
        instance.retryCount = 0;
    }

    private void getNext() {
        for (int i = 0; i < serverInfos.size(); i++) {
            if (instance.getCurrent().getServerInfo().equals(serverInfos.get(i))) {
                if (i == serverInfos.size() - 1) {
                    instance.current.disconnect();
                    instance.current = null;
                    instance.current = instance.clients.get(serverInfos.get(0).hashCode());
                    break;
                } else {
                    instance.current.disconnect();
                    instance.current = null;

                    instance.current = instance.clients.get(serverInfos.get(i + 1).hashCode());
                }
            }
        }
    }

    public Client getCurrent() {
        return current;
    }

    public Client getClient(ServerInfo serverInfo) {
        clients.clear();
        retryCount = 0;
        if (serverInfo != null) {
            instance.serverInfos.clear();
            instance.serverInfos.add(serverInfo);

            current = new Client(serverInfo);
            clients.put(serverInfo.hashCode(), current);
            return clients.get(serverInfo.hashCode());
        }
        return null;
    }

    public Client getClient(List<ServerInfo> serverInfos) {
        clients.clear();
        retryCount = 0;
        instance.serverInfos.clear();
        instance.serverInfos.addAll(serverInfos);

        if (serverInfos.size() == 0) {
            return null;
        }
        for (ServerInfo serverInfo : serverInfos) {
            clients.put(serverInfo.hashCode(), new Client(serverInfo));
        }
        return clients.get(serverInfos.get(0).hashCode());
    }

    //服务器地址 "192.168.1.1:1111,192.168.1.2.2222,127.0.0.1:2010"
    public static ClientFactory getInstance() {
        if (instance == null) {
            instance = new ClientFactory();
        }
        return instance;
    }

    public static boolean isOpenNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
}
