package net.openvpn.openvpn;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import net.openvpn.openvpn.data.EventMsg;
import net.openvpn.openvpn.data.LogMsg;

public class MessageQueue
{
    private static final int MSG_EVENT = 1;
    private static final int MSG_LOG = 2;
    private static final String TAG = "MessageQueue";

    private final EventHandler eventHandler;
    private final LogHandler logHandler;
    private final Handler handler = new Handler(new Callback());

    public interface EventHandler {
        boolean on_event(EventMsg eventMsg);
    }

    public interface LogHandler {
        boolean on_log(LogMsg logMsg);
    }

    private class Callback implements Handler.Callback
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_EVENT:
                    return eventHandler.on_event((EventMsg) msg.obj);
                case MSG_LOG:
                    return logHandler.on_log((LogMsg) msg.obj);
                default:
                    Log.d(TAG, "SERV: unhandled message");
                    return false;
            }
        }
    }

    public MessageQueue(LogHandler logHandler, EventHandler eventHandler) {
        this.logHandler = logHandler;
        this.eventHandler = eventHandler;
    }

    public void post(int type, Object obj) {
        handler.sendMessage(handler.obtainMessage(type, obj));
    }

    public void post_event(String name) {
        post_event(name, "");
    }

    public void post_event(String name, String info) {
        post_event(new EventMsg(name, info));
    }

    public void post_event(EventMsg event) {
        post(MSG_EVENT, event);
    }

    public void post_log(String line) {
        LogMsg logMsg = new LogMsg();
        logMsg.line = line + "\n";
        post_log(logMsg);
    }

    public void post_log(LogMsg logMsg) {
        post(MSG_LOG, logMsg);
    }
}
