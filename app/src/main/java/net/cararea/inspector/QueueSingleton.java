package net.cararea.inspector;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class QueueSingleton {
    private static QueueSingleton instance;
    private RequestQueue queue;

    public static QueueSingleton getInstance(Context applicationContext) {
        if (instance == null) {
            instance = new QueueSingleton(applicationContext);
        }
        return instance;
    }

    private QueueSingleton(Context context) {
        Cache cache = new DiskBasedCache(context.getCacheDir(), 5 * 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        queue.start();
    }

    RequestQueue getQueue() {
        return queue;
    }
}
