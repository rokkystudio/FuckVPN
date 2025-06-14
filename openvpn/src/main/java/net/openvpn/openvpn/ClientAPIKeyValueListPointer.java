package net.openvpn.openvpn;

/**
 * Тип-обёртка для указателя на std::vector<openvpn::ClientAPI::KeyValue>.
 * Используется SWIG для JNI-связи.
 */
public class ClientAPIKeyValueListPointer {
    private transient long swigCPtr;

    protected ClientAPIKeyValueListPointer() {
        this.swigCPtr = 0;
    }

    protected ClientAPIKeyValueListPointer(long cPtr, boolean unused) {
        this.swigCPtr = cPtr;
    }

    protected static long getCPtr(ClientAPIKeyValueListPointer obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }
}