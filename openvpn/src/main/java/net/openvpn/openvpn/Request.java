package net.openvpn.openvpn;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;

public class Request
{
    public static class Cancelled extends Exception {
        public Cancelled() {
            super("Request cancelled");
        }
    }

    public static class Ping implements Callable<Boolean> {
        private static final String TAG = "Request.Ping";
        private volatile boolean isCancelled = false;

        private final Options options;
        private PingTask request;

        public static class Options {
            private String url;
            private Collection<Integer> repeatTimeouts;
            private boolean shouldRepeat;

            public String getUrl() {
                return url;
            }

            public Options setUrl(String url) {
                this.url = url;
                return this;
            }

            public Collection<Integer> getRepeatTimeouts() {
                return repeatTimeouts;
            }

            public boolean getShouldRepeat() {
                return shouldRepeat;
            }

            public Options setRepeatTimeouts(Collection<Integer> repeatTimeouts) {
                this.repeatTimeouts = repeatTimeouts;
                return this;
            }

            public Options setShouldRepeat(boolean shouldRepeat) {
                this.shouldRepeat = shouldRepeat;
                return this;
            }
        }

        public Ping(Options options) {
            this.options = options;
        }

        private Boolean doPing() throws Exception {
            request = new PingTask(options.getUrl());
            Boolean result = request.call();
            request = null;
            return result;
        }

        private void waitFor(int seconds) {
            if (seconds <= 0 || isCancelled) return;
            synchronized (this) {
                try {
                    Log.d(TAG, "Waiting for " + seconds + "s...");
                    wait(seconds * 1000L);
                } catch (InterruptedException ignored) {
                }
            }
        }

        @Override
        public Boolean call() throws Exception {
            boolean reachable = false;

            if (!options.getShouldRepeat()) {
                return doPing();
            }

            Iterator<Integer> iterator = options.getRepeatTimeouts().iterator();
            while (!isCancelled && !reachable && iterator.hasNext()) {
                int timeout = iterator.next();
                if (isCancelled) {
                    Log.d(TAG, "Ping cancelled, breaking cycle...");
                    break;
                }

                Log.d(TAG, "Checking reachability for " + options.getUrl());
                reachable = doPing();
                if (!reachable) waitFor(timeout);
            }
            Log.d(TAG, "Ping resolved with REACHABILITY=" + reachable);
            return reachable;
        }

        public void cancel() {
            synchronized (this) {
                if (isCancelled) return;
                Log.d(TAG, "Cancelling ping...");
                isCancelled = true;
                notifyAll();
                request = null;
            }
        }

        public boolean isCancelled() {
            return isCancelled;
        }
    }

    public static class PingTask implements Callable<Boolean> {
        private final String urlString;

        public PingTask(String urlString) {
            this.urlString = urlString;
        }

        @Override
        public Boolean call() {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                connection.connect();
                int responseCode = connection.getResponseCode();
                return (200 <= responseCode && responseCode < 400);
            } catch (IOException e) {
                return false;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }
}
