package net.openvpn.openvpn.ipc;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import java.util.HashMap;
import java.util.UUID;

public class IPCSender {
    public static final String TAG = "IPCSender";
    private final HashMap<String, IPCCallback> callbackMap = new HashMap<>();
    private final HashMap<String, IPCChunkList> chunk_queue_in = new HashMap<>();
    private Handler handler;
    private HandlerThread handler_thread;
    private final Messenger messenger;
    private final HashMap<String, IPCTokenQueue> token_queue_out = new HashMap<>();

    public interface Delegate {
        <T> void send(String str, T t);

        <T> void send(String str, T t, IPCCallback iPCCallback);
    }

    private static class IncomingHandler extends Handler {
        IPCSender parent;

        IncomingHandler(Looper looper, IPCSender iPCSender) {
            super(looper);
            this.parent = iPCSender;
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                this.parent.handleResponse(message);
            } else if (i == 2) {
                this.parent.handleResponseChunk(message);
            } else if (i == 3) {
                this.parent.handleRequestChunkAck(message);
            } else if (i != 4) {
                super.handleMessage(message);
            } else {
                this.parent.handleResponseChunkFin(message);
            }
        }
    }

    public IPCSender() {
        HandlerThread handlerThread = new HandlerThread("SenderHandlerThread");
        this.handler_thread = handlerThread;
        handlerThread.start();
        this.handler = new IncomingHandler(this.handler_thread.getLooper(), this);
        this.messenger = new Messenger(this.handler);
    }

    private void doSendChunked(String str) {
        IPCTokenQueue iPCTokenQueue = this.token_queue_out.get(str);
        if (iPCTokenQueue != null && iPCTokenQueue.hasNext()) {
            boolean oneLeft = iPCTokenQueue.oneLeft();
            IPCChunkToken next = iPCTokenQueue.next();
            String str2 = iPCTokenQueue.action;
            Messenger messenger2 = iPCTokenQueue.messenger;
            IPCChunk iPCChunk = new IPCChunk(next, str2, str);
            Message obtain = Message.obtain(this.handler, oneLeft ? 4 : 2);
            obtain.replyTo = this.messenger;
            iPCChunk.writeTo(obtain);
            try {
                messenger2.send(obtain);
                if (oneLeft) {
                    this.token_queue_out.remove(str);
                }
            } catch (RemoteException e) {
                Log.e(TAG, "doSendChunked", e);
                throw e;
            } catch (Throwable th) {
                if (oneLeft) {
                    this.token_queue_out.remove(str);
                }
                throw th;
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleRequestChunkAck(Message message) {
        try {
            doSendChunked(IPCMessage.readFrom(message).id);
        } catch (RemoteException e) {
            Log.e(TAG, "handleRequestChunkAck", e);
        }
    }

    /* access modifiers changed from: private */
    public void handleResponse(Message message) {
        final IPCMessage readFrom = IPCMessage.readFrom(message);
        final IPCCallback remove = this.callbackMap.remove(readFrom.id);
        if (remove != null) {
            this.handler.post(new Runnable() {
                public void run() {
                    try {
                        synchronized (remove) {
                            remove.setValue(readFrom.getData());
                            remove.setResult(remove.call());
                            remove.notifyAll();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void handleResponseChunk(Message message) {
        IPCChunk readFrom = IPCChunk.readFrom(message);
        String str = readFrom.id;
        String str2 = readFrom.action_name;
        if (this.callbackMap.get(str) != null) {
            IPCChunkList iPCChunkList = this.chunk_queue_in.get(str);
            if (iPCChunkList == null) {
                iPCChunkList = new IPCChunkList();
                this.chunk_queue_in.put(str, iPCChunkList);
            }
            iPCChunkList.add(readFrom);
            Message obtain = Message.obtain((Handler) null, 3, message.arg1, message.arg2);
            new IPCMessage(str2, str, null).writeTo(obtain);
            try {
                message.replyTo.send(obtain);
            } catch (RemoteException e) {
                Log.e(TAG, "handleResponseChunk", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleResponseChunkFin(Message message) {
        final Object obj;
        IPCChunk readFrom = IPCChunk.readFrom(message);
        String str = readFrom.id;
        final IPCCallback remove = this.callbackMap.remove(str);
        if (remove != null) {
            IPCChunkList remove2 = this.chunk_queue_in.remove(str);
            if (remove2 == null) {
                remove2 = new IPCChunkList();
            }
            remove2.add(readFrom);
            remove2.sort();
            try {
                obj = ((IPCChunk) remove2.get(0)).getCreator().createFromTokens(remove2.getTokens());
            } catch (IPCChunkable.BadChunkableException e) {
                Log.e(TAG, "handleResponseChunkFin", e);
                obj = null;
            }
            this.handler.post(new Runnable() {
                public void run() {
                    try {
                        synchronized (remove) {
                            remove.setValue(obj);
                            remove.setResult(remove.call());
                            remove.notifyAll();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private <T extends IPCChunkable> void sendChunked(Messenger messenger2, String str, T t, IPCCallback iPCCallback) {
        String uuid = UUID.randomUUID().toString();
        this.token_queue_out.put(uuid, new IPCTokenQueue(t.getTokens(), messenger2, uuid, str));
        this.callbackMap.put(uuid, iPCCallback);
        doSendChunked(uuid);
    }

    public void send(Messenger messenger2, String str) {
        send(messenger2, str, (Object) null, (IPCCallback) null);
    }

    public <T> void send(Messenger messenger2, String str, T t) {
        send(messenger2, str, t, (IPCCallback) null);
    }

    public <T> void send(Messenger messenger2, String str, T t, IPCCallback iPCCallback) {
        if (messenger2 != null) {
            if (t instanceof IPCChunkable) {
                IPCChunkable iPCChunkable = (IPCChunkable) t;
                if (iPCChunkable.shouldChunk()) {
                    sendChunked(messenger2, str, iPCChunkable, iPCCallback);
                    return;
                }
            }
            Message obtain = Message.obtain(this.handler, 1);
            String uuid = UUID.randomUUID().toString();
            obtain.replyTo = this.messenger;
            new IPCMessage(str, uuid, t).writeTo(obtain);
            this.callbackMap.put(uuid, iPCCallback);
            messenger2.send(obtain);
        }
    }

    public void send(Messenger messenger2, String str, IPCCallback iPCCallback) {
        send(messenger2, str, (Object) null, iPCCallback);
    }
}
