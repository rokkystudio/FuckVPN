package net.openvpn.openvpn.ipc;

import java.util.concurrent.Callable;

/**
 * Базовый класс для обратных вызовов, используемых в межпроцессной или межпоточной передаче данных.
 * Хранит входное значение и результат, обеспечивает синхронизированный доступ и механизм ожидания.
 *
 * <p>Используется совместно с IPC-сообщениями. Входное значение {@code value} передаётся обработчику,
 * результат {@code result} возвращается по завершению работы {@link #call()}.</p>
 *
 * @param <T> Тип входного значения
 * @param <K> Тип возвращаемого результата
 */
public abstract class IPCCallback<T, K> implements Callable<K> {
    private static final long AWAIT_TIMEOUT_MS = 5000;

    private T value;
    private K result;

    /**
     * Ожидает завершения выполнения {@link #call()} не более {@value AWAIT_TIMEOUT_MS} мс.
     */
    public synchronized void await() throws InterruptedException {
        wait(AWAIT_TIMEOUT_MS);
    }

    public synchronized T getValue() {
        return value;
    }

    public synchronized void setValue(T value) {
        this.value = value;
    }

    public synchronized K getResult() {
        return result;
    }

    public synchronized void setResult(K result) {
        this.result = result;
    }
}
