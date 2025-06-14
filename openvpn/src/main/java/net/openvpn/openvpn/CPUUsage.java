package net.openvpn.openvpn;

/**
 * Класс для измерения использования CPU времени потока (в секундах).
 */
public class CPUUsage
{
    private final double startTime = cpuUsage();
    private double endTime = 0.0;
    private boolean halted = false;

    // Метод должен быть реализован через JNI и возвращать использование CPU в секундах
    private static native double cpuUsage();

    /** Останавливает замер CPU времени. Повторный вызов не изменит результат. */
    public void stop() {
        if (!halted) {
            endTime = cpuUsage();
            halted = true;
        }
    }

    /** Возвращает использование CPU в секундах от момента создания до остановки (или текущего момента). */
    public double usage() {
        return (halted ? endTime : cpuUsage()) - startTime;
    }
}