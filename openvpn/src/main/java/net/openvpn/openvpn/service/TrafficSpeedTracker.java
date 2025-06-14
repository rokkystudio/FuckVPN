package net.openvpn.openvpn.service;

import android.os.Handler;
import android.os.Looper;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.openvpn.openvpn.OpenVPNService;
import net.openvpn.openvpn.data.ConnectionStats;

public class TrafficSpeedTracker {
    private long current_bytes_in;
    private long current_bytes_out;
    private Queue<Integer> download_speed_q;
    /* access modifiers changed from: private */
    public Handler handler;
    private OpenVPNService mBoundService;
    private final int pollIntervalMilliseconds;
    private int previous_bytes_in;
    private int previous_bytes_out;
    private final Lock queue_lock = new ReentrantLock();
    private Runnable runnable;
    private final int samplesCount = 60;
    private long timestamp_ns;
    private Queue<Integer> upload_speed_q;

    public TrafficSpeedTracker(Looper looper, final int i, OpenVPNService openVPNService) {
        this.handler = new Handler(looper);
        this.pollIntervalMilliseconds = i;
        this.upload_speed_q = new LinkedList();
        this.download_speed_q = new LinkedList();
        for (int i2 = 0; i2 < 60; i2++) {
            this.upload_speed_q.add(0);
            this.download_speed_q.add(0);
        }
        this.timestamp_ns = System.nanoTime();
        this.mBoundService = openVPNService;
        this.runnable = new Runnable() {
            public void run() {
                TrafficSpeedTracker.this.saveSpeedData();
                TrafficSpeedTracker.this.handler.postDelayed(this, (long) i);
            }
        };
    }

    private int[] queue_to_array(Queue<Integer> queue) {
        int[] iArr;
        this.queue_lock.lock();
        try {
            int size = queue.size();
            Integer[] numArr = new Integer[size];
            iArr = new int[size];
            queue.toArray(numArr);
            for (int i = 0; i < size; i++) {
                iArr[i] = numArr[i].intValue();
            }
        } catch (Exception unused) {
            iArr = new int[60];
            Arrays.fill(iArr, 0);
        } catch (Throwable th) {
            this.queue_lock.unlock();
            throw th;
        }
        this.queue_lock.unlock();
        return iArr;
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    public void saveSpeedData() {
        OpenVPNService openVPNService = this.mBoundService;
        if (openVPNService != null) {
            ConnectionStats connectionStats = openVPNService.get_connection_stats();
            int i = (int) connectionStats.bytes_out;
            int i2 = (int) connectionStats.bytes_in;
            long nanoTime = System.nanoTime();
            int round = Math.round((float) (Math.round((float) ((nanoTime - this.timestamp_ns) / 1000000)) / 1000));
            if (round == 0) {
                round = 1;
            }
            this.queue_lock.lock();
            try {
                if (this.download_speed_q.size() + 1 > 60) {
                    this.download_speed_q.poll();
                }
                this.download_speed_q.add(new Integer(Math.round((float) ((i2 - this.previous_bytes_in) / round))));
                if (this.upload_speed_q.size() + 1 > 60) {
                    this.upload_speed_q.poll();
                }
                this.upload_speed_q.add(new Integer(Math.round((float) ((i - this.previous_bytes_out) / round))));
                this.queue_lock.unlock();
                this.current_bytes_out = (long) i;
                this.current_bytes_in = (long) i2;
                this.previous_bytes_out = i;
                this.previous_bytes_in = i2;
                this.timestamp_ns = nanoTime;
            } catch (Throwable th) {
                this.queue_lock.unlock();
                throw th;
            }
        }
    }

    public long getCurrentBytesIn() {
        return this.current_bytes_in;
    }

    public long getCurrentBytesOut() {
        return this.current_bytes_out;
    }

    public int[] getDownloadSpeed() {
        return queue_to_array(this.download_speed_q);
    }

    public int[] getUploadSpeed() {
        return queue_to_array(this.upload_speed_q);
    }

    public void revoke() {
        this.handler.removeCallbacks(this.runnable);
    }

    public void schedule() {
        saveSpeedData();
        this.handler.postDelayed(this.runnable, (long) this.pollIntervalMilliseconds);
    }
}
