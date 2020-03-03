package com.jack.websocket;

import com.jack.websocket.util.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * 负责 WebSocket 重连
 * <p>
 * Created by jack on 2020/02/25
 */
public class DefaultReconnectManager implements ReconnectManager {

    private static final String TAG = "WSDefaultRM";

    /**
     * 重连锁
     */
    private final Object BLOCK = new Object();

    private WebSocketManager mWebSocketManager;
    private OnConnectListener mOnDisconnectListener;
    private WebSocketSetting mSetting;

    /**
     * 是否正在重连
     */
    private volatile boolean reconnecting;
    /**
     * 被销毁
     */
    private volatile boolean destroyed;
    /**
     * 是否需要停止重连
     */
    private volatile boolean needStopReconnect = false;
    /**
     * 是否已连接
     */
    private volatile boolean connected = false;

    /**
     * 重连机制睡眠时间 呈指数增长
     */
    private int mSleepTime = 0;

    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    public DefaultReconnectManager(WebSocketManager webSocketManager,
                                   WebSocketSetting mSetting,
                                   OnConnectListener onDisconnectListener) {
        this.mSetting = mSetting;
        this.mWebSocketManager = webSocketManager;
        this.mOnDisconnectListener = onDisconnectListener;
        reconnecting = false;
        destroyed = false;
    }

    @Override
    public boolean reconnecting() {
        return reconnecting;
    }

    @Override
    public void startReconnect() {
        if (reconnecting) {
            LogUtil.i(TAG, "Reconnecting, do not call again.");
            return;
        }
        if (destroyed) {
            LogUtil.e(TAG, "ReconnectManager is destroyed!!!");
            return;
        }
        needStopReconnect = false;
        reconnecting = true;
        try {
            reconnectCount = 0;
            singleThreadPool.execute(getReconnectRunnable());
        } catch (RejectedExecutionException e) {
            LogUtil.e(TAG, "线程队列已满，无法执行此次任务。", e);
            reconnecting = false;
        }
    }

    private int reconnectCount = 0;
    private int finishCount = 1;

    private Runnable getReconnectRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (destroyed || needStopReconnect) {
                    reconnecting = false;
                    return;
                }
                //LogUtil.d(TAG, "开始重连:" + reconnectCount);
                reconnectCount++;
                reconnecting = true;
                connected = false;
                try {
                    int count = mWebSocketManager.getSetting().getReconnectFrequency();
                    //LogUtil.e("----222222--", count + "");
                    for (int i = 0; i < count; i++) {
                        if (mWebSocketManager.isConnect()) {
                            break;
                        }

                        //LogUtil.e("------", i + "");
                        mSleepTime = mSleepTime * i * 2;
                        if (i != 0) {
                            if (mSleepTime <= mSetting.getConnectionRecoveryMinInterval()) {
                                mSleepTime = mSetting.getConnectionRecoveryMinInterval();
                            }
                            if (mSleepTime >= mSetting.getConnectionRecoveryMaxInterval()) {
                                mSleepTime = mSetting.getConnectionRecoveryMaxInterval();
                            }
                        }

                        try {
                            //LogUtil.e("Thread---", + mSleepTime + "-----" + System.currentTimeMillis());
                            Thread.sleep(mSleepTime * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //LogUtil.e("Thread---2222", + mSleepTime + "-----" + System.currentTimeMillis());
                        //LogUtil.i(TAG, String.format("第%s次重连---", i + 1));
                        mWebSocketManager.reconnectOnce();
                        synchronized (BLOCK) {
                            try {
                                BLOCK.wait(mWebSocketManager.getSetting().getConnectTimeout());
                                if (connected) {
                                    //LogUtil.i(TAG, "reconnectOnce success!");
                                    LogUtil.e("jack", reconnectCount + "---------");
                                    mOnDisconnectListener.onDisconnectCount(reconnectCount);
                                    mOnDisconnectListener.onConnected();
                                    return;
                                }
                                if (needStopReconnect) {
                                    break;
                                }
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                    //重连失败
                    LogUtil.i(TAG, "reconnectOnce failed!");
                    LogUtil.e("jack --failed", reconnectCount + "---------");
                    mOnDisconnectListener.onDisconnect();
                    mOnDisconnectListener.onDisconnectCount(reconnectCount);
                } finally {
                    LogUtil.d(TAG, "重连结束:" + finishCount);
                    finishCount++;
                    reconnecting = false;
                    LogUtil.i(TAG, "reconnecting = false");
                }
            }
        };
    }

    @Override
    public void stopReconnect() {
        needStopReconnect = true;
        if (singleThreadPool != null) {
            singleThreadPool.shutdownNow();
        }
    }

    @Override
    public void onConnected() {
        connected = true;
        synchronized (BLOCK) {
            LogUtil.i(TAG, "onConnected()->BLOCK.notifyAll()");
            BLOCK.notifyAll();
        }
    }

    @Override
    public void onConnectError(Throwable th) {
        connected = false;
        synchronized (BLOCK) {
            LogUtil.i(TAG, "onConnectError(Throwable)->BLOCK.notifyAll()");
            BLOCK.notifyAll();
        }
    }

    /**
     * 销毁资源，并停止重连
     */
    @Override
    public void destroy() {
        destroyed = true;
        stopReconnect();
        mWebSocketManager = null;
    }
}
