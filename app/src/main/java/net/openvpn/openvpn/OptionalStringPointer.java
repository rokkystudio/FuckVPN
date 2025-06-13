package net.openvpn.openvpn;

public class OptionalStringPointer
{
    private transient long swigCPtr;

    protected OptionalStringPointer() {
        this.swigCPtr = 0;
    }

    protected OptionalStringPointer(long cPtr, boolean unused) {
        this.swigCPtr = cPtr;
    }

    protected static long getCPtr(OptionalStringPointer obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }
}
