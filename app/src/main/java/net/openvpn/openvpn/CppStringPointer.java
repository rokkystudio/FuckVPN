package net.openvpn.openvpn;

/**
 * Обёртка SWIG для указателя на std::string.
 */
public class CppStringPointer
{
    private final transient long swigCPtr;

    protected CppStringPointer() {
        this.swigCPtr = 0;
    }

    protected CppStringPointer(long cPtr, boolean unused) {
        this.swigCPtr = cPtr;
    }

    protected static long getCPtr(CppStringPointer obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }
}