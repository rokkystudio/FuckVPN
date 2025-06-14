package net.openvpn.openvpn;

public class DnsDomain {
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public DnsDomain() {
        this(ovpncliJNI.new_DnsDomain(), true);
    }

    protected DnsDomain(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(DnsDomain obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_DnsDomain(swigCPtr);
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

    public String getDomain() {
        return ovpncliJNI.DnsDomain_domain_get(swigCPtr, this);
    }

    public void setDomain(String value) {
        ovpncliJNI.DnsDomain_domain_set(swigCPtr, this, value);
    }

    public String to_string() {
        return ovpncliJNI.DnsDomain_to_string(swigCPtr, this);
    }

    public void validate(String value) {
        ovpncliJNI.DnsDomain_validate(swigCPtr, this, value);
    }
}
