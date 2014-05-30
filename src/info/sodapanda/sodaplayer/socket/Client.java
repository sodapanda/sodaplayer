package info.sodapanda.sodaplayer.socket;

import info.sodapanda.sodaplayer.Status;
import info.sodapanda.sodaplayer.events.DisConnEvent;
import info.sodapanda.sodaplayer.pojo.LogedUser;
import info.sodapanda.sodaplayer.socket.out.DDMessage;
import info.sodapanda.sodaplayer.socket.out.LoginMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;

public class Client {
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    Handler handler;
    Bus bus;
    MessageParser mp;
    private boolean stopped;
    ServerInfo serverInfo;
    private ChatHandler mhandler;

    public Client(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        handler = new Handler(Looper.getMainLooper());
        bus = BusProvider.getBus();
        mp = new MessageParser();
    }

    /**
     * 连接和初始化操作
     */
    public void connect(final ChatHandler mhandler) {
        this.mhandler = mhandler;
        stopped =false;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Log.i("dudu", "正在连接");
                    socket = new Socket();
                    SocketAddress serverAddr = new InetSocketAddress(serverInfo.getUrl(), serverInfo.getPort());
                    socket.connect(serverAddr, 3000);
                    out = socket.getOutputStream();
                    in = socket.getInputStream();
                    mhandler.handleOnMain(true);
                    listen();

                    login(Status.getRoomInfo().getArchivesId() + "", serverInfo.getUrl(), LogedUser.getUser_id() + "");
                } catch (UnknownHostException e) {
                    Log.i("dudu", "未知地址");
                    mhandler.handleOnMain(false);
                } catch (IOException e) {
                    Log.i("dudu", "连接错误");
                    mhandler.handleOnMain(false);
                }
            }
        }).start();
    }

    /**
     * 连接和初始化操作
     */
    public void connectSilent() {
        stopped =false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("qicheng", "正在静默重试连接...");
                    socket = new Socket();
                    SocketAddress serverAddr = new InetSocketAddress(serverInfo.getUrl(), serverInfo.getPort());
                    socket.connect(serverAddr, 3000);
                    out = socket.getOutputStream();
                    in = socket.getInputStream();
                    listen();

                    login(Status.getRoomInfo().getArchivesId() + "", serverInfo.getUrl(), LogedUser.getUser_id() + "");
                } catch (UnknownHostException e) {
                    Log.i("dudu", "未知地址");
                } catch (IOException e) {
                    handleError();
                    Log.i("dudu", "连接错误");
                }
            }
        }).start();
    }

    /**
     * 登陆操作
     *
     * @param archives_id
     * @param domain
     * @param uid
     * @return
     */
    public boolean login(String archives_id, String domain, String uid) {
        Log.i("test", "调用登录 " + archives_id + " " + uid);
        DDMessage loginMessage = new LoginMessage(archives_id, domain, uid);

        if (send(loginMessage)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 断开连接
     */
    public void disconnect() {
        if (socket != null) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException e) {
                Log.i("dudu", "断开连接错误");
            }
        }
        selfStop();
    }

    /**
     * 发送消息
     *
     * @param msg 被发送的消息
     * @return 发送是否成功
     */
    public boolean send(DDMessage msg) {
        if (out == null) {
            return false;
        }
        try {
            out.write(msg.getMessageByte());
            return true;
        } catch (IOException e) {
            Log.i("dudu", "网络错误");
            return false;
        }
    }

    /**
     * 监听服务器发送过来的消息
     */
    private void listen() {
        new Thread() {

            @Override
            public void run() {

                byte[] buff = new byte[65535];
                int len;
                ByteBuffer byte_buf;
                //链接成功后 重置重连次数.
                ClientFactory.getInstance().resetRetryCount();

                while (!stopped) {
                    if (!isConnectAlive()) {
                        Log.d("qicheng","启动重连");
                        ClientFactory.startNewClient();
                    }
                    try {
                        len = in.read(buff);// 阻塞读取数据填充缓冲
                        Log.i("dudu", "有新内容读取到长度" + len);

                        if (len > 0) {// 读取到有效数据
                            byte_buf = ByteBuffer.wrap(buff, 0, len);
                            mp.parseMessage(byte_buf, len);
                        } else {// 读到末尾即socket断开情况
                            Log.i("dudu", "网络正常断开");
                            break;
                        }
                    } catch (IOException e) {
                        Log.i("dudu", "接收网络数据失败");
                        handleError();
                        break;
                    }
                }
                Log.d("qicheng","listening end");
            }
        }.start();
    }

    public void selfStop() {
        if (!stopped) {
            stopped = true;
        }
    }

    private boolean isConnectAlive() {
        if (!socket.isConnected())
            return false;
        if (socket.isOutputShutdown())
            return false;
        return true;
    }

    private void handleError() {
        ClientFactory.startNewClient();
        handler.post(new Runnable() {

            @Override
            public void run() {
                bus.post(new DisConnEvent());
            }
        });
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void changeServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }
}
