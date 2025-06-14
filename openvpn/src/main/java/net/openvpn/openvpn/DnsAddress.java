package net.openvpn.openvpn;

public class DnsAddress {
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public DnsAddress() {
        this(ovpncliJNI.new_DnsAddress(), true);
    }

    protected DnsAddress(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(DnsAddress obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_DnsAddress(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            delete();
        } finally {
            super.finalize();
        }
    }

    public String getAddress() {
        return ovpncliJNI.DnsAddress_address_get(swigCPtr, this);
    }

    public void setAddress(String address) {
        ovpncliJNI.DnsAddress_address_set(swigCPtr, this, address);
    }

    public long getPort() {
        return ovpncliJNI.DnsAddress_port_get(swigCPtr, this);
    }

    public void setPort(long port) {
        ovpncliJNI.DnsAddress_port_set(swigCPtr, this, port);
    }

    public String to_string() {
        return ovpncliJNI.DnsAddress_to_string(swigCPtr, this);
    }

    public void validate(String input) {
        ovpncliJNI.DnsAddress_validate(swigCPtr, this, input);
    }
}
