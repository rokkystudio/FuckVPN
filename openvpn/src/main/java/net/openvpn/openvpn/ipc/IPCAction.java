package net.openvpn.openvpn.ipc;

/**
 * Представляет действие для IPC (межпроцессного взаимодействия),
 * содержащее имя действия и обработчик обратного вызова.
 */
public class IPCAction<T, K>
{
    public final String name;
    public final IPCCallback<T, K> callback;

    public IPCAction(String name, IPCCallback<T, K> callback) {
        this.name = name;
        this.callback = callback;
    }
}