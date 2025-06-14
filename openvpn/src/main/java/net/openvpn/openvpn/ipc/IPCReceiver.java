package net.openvpn.openvpn.ipc;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class IPCReceiver
{
    private static final String TAG = "IPCReceiver";

    private final HashMap<String, IPCAction<?, ?>> actions = new HashMap<>();
    private final HashMap<String, IPCChunkList<IPCChunk<?>>> chunkQueueIn = new HashMap<>();
    private final HashMap<String, IPCTokenQueue> tokenQueueOut = new HashMap<>();

    private final Handler handler;
    private final HandlerThread handlerThread;

    public final Set<Messenger> clients = new HashSet<>();
    public final Messenger messenger;

    private static class IncomingHandler extends Handler {
        private final IPCReceiver parent;

        IncomingHandler(IPCReceiver parent) {
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1 -> parent.handleRequest(message);
                case 2 -> parent.handleRequestChunk(message);
                case 3 -> parent.handleResponseChunkAck(message);
                case 4 -> parent.handleRequestChunkFinal(message);
                case 5 -> parent.handleRegister(message);
                case 6 -> parent.handleUnregister(message);
                default -> super.handleMessage(message);
            }
        }
    }

    public IPCReceiver() {
        handlerThread = new HandlerThread("ReceiverHandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        messenger = new Messenger(new IncomingHandler(this));
    }

    private void doReplyWithChunks(String id) {
        IPCTokenQueue queue = tokenQueueOut.get(id);
        if (queue != null && queue.hasNext()) {
            boolean oneLeft = queue.oneLeft();
            IPCChunkToken<?> token = queue.next();
            IPCChunk<?> chunk = new IPCChunk<>(token, queue.action, id);

            Message reply = Message.obtain(handler, oneLeft ? 4 : 2);
            reply.replyTo = messenger;
            chunk.writeTo(reply);

            try {
                queue.messenger.send(reply);
            } catch (RemoteException e) {
                Log.e(TAG, "doReplyWithChunks", e);
            } finally {
                if (oneLeft) {
                    tokenQueueOut.remove(id);
                }
            }
        }
    }

    private void handleRegister(Message message) {
        clients.add(message.replyTo);
    }

    private void handleUnregister(Message message) {
        clients.remove(message.replyTo);
    }

    @SuppressWarnings("unchecked")
    private void handleRequest(Message message)
    {
        IPCMessage<?> msg = IPCMessage.readFrom(message);
        String id = msg.id;
        String action = msg.action_name;

        IPCAction<?, ?> rawAction = actions.get(action);
        if (rawAction != null)
        {
            // Приводим типы в рамках одной лямбды
            IPCCallback<Object, Object> callback = (IPCCallback<Object, Object>) rawAction.callback;
            Object value = msg.getData();
            Message original = Message.obtain(message);

            handler.post(() ->
            {
                try {
                    synchronized (callback) {
                        callback.setValue(value);
                        Object result = callback.call();
                        callback.setResult(result);
                        callback.notifyAll();

                        if (original.replyTo != null)
                        {
                            if (result instanceof IPCChunkable) {
                                IPCChunkable chunkable = (IPCChunkable) result;
                                if (chunkable.shouldChunk()) {
                                    replyWithChunks(original, action, id, chunkable);
                                    return;
                                }
                            }
                            Message reply = Message.obtain(null, 1, original.arg1, original.arg2);
                            new IPCMessage<>(action, id, result).writeTo(reply);
                            original.replyTo.send(reply);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "handleRequest", e);
                }
            });
        }
    }


    private void handleRequestChunk(Message message) {
        IPCChunk<?> chunk = IPCChunk.readFrom(message);
        String id = chunk.id;
        String action = chunk.action_name;

        if (actions.containsKey(action)) {
            // Получаем или создаем строго типизированный список
            IPCChunkList<IPCChunk<?>> list = chunkQueueIn.computeIfAbsent(id, k -> new IPCChunkList<>());

            list.add(chunk);  // ✅ Всё типизировано, без ошибок компиляции

            Message ack = Message.obtain(null, 3, message.arg1, message.arg2);
            new IPCMessage<>(action, id, null).writeTo(ack);

            try {
                message.replyTo.send(ack);
            } catch (RemoteException e) {
                Log.e(TAG, "handleRequestChunk", e);
            }
        }
    }


    private void handleRequestChunkFinal(Message message) {
        IPCChunk<?> finalChunk = IPCChunk.readFrom(message);
        String id = finalChunk.id;
        String action = finalChunk.action_name;

        IPCAction actionHandler = actions.get(action);
        if (actionHandler != null) {
            IPCChunkList<?> chunks = chunkQueueIn.remove(id);
            if (chunks == null) chunks = new IPCChunkList<>();
            chunks.add(finalChunk);
            chunks.sortChunks();

            Object data;
            try {
                data = chunks.get(0).getCreator().createFromTokens(chunks.getTokens());
            } catch (IPCChunkable.BadChunkableException e) {
                Log.e(TAG, "handleRequestChunkFinal", e);
                data = null;
            }

            IPCCallback<?, ?> callback = actionHandler.callback;
            Message original = Message.obtain(message);
            Object finalData = data;

            handler.post(() -> {
                try {
                    synchronized (callback) {
                        callback.setValue(finalData);
                        Object result = callback.call();
                        callback.setResult(result);
                        callback.notifyAll();

                        if (original.replyTo != null) {
                            if (result instanceof IPCChunkable chunkable && chunkable.shouldChunk()) {
                                replyWithChunks(original, action, id, chunkable);
                            } else {
                                Message reply = Message.obtain(null, 1, original.arg1, original.arg2);
                                new IPCMessage<>(action, id, result).writeTo(reply);
                                original.replyTo.send(reply);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "handleRequestChunkFinal", e);
                }
            });
        }
    }

    private void handleResponseChunkAck(Message message) {
        IPCMessage<?> msg = IPCMessage.readFrom(message);
        doReplyWithChunks(msg.id);
    }

    private void replyWithChunks(Message message, String action, String id, IPCChunkable chunkable) {
        tokenQueueOut.put(id, new IPCTokenQueue(chunkable.getTokens(), message.replyTo, id, action));
        doReplyWithChunks(id);
    }

    public IBinder getBinder() {
        return messenger.getBinder();
    }

    public void register(IPCAction action) {
        actions.put(action.name, action);
    }

    public void unregister(String name) {
        actions.remove(name);
    }
}
