package net.openvpn.openvpn;

public class DnsServer {
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    // === ENUM: Security ===
    public static final class Security {
        public static final Security Unset = new Security("Unset");
        public static final Security No = new Security("No");
        public static final Security Yes = new Security("Yes");
        public static final Security Optional = new Security("Optional");

        private static int swigNext = 0;
        private static final Security[] swigValues = {Unset, No, Yes, Optional};

        private final String swigName;
        private final int swigValue;

        private Security(String name) {
            this.swigName = name;
            this.swigValue = swigNext++;
        }

        private Security(String name, int value) {
            this.swigName = name;
            this.swigValue = value;
            swigNext = value + 1;
        }

        private Security(String name, Security other) {
            this.swigName = name;
            this.swigValue = other.swigValue;
            swigNext = this.swigValue + 1;
        }

        public static Security swigToEnum(int value) {
            for (Security s : swigValues) {
                if (s.swigValue == value) return s;
            }
            throw new IllegalArgumentException("No enum Security with value " + value);
        }

        public int swigValue() {
            return swigValue;
        }

        @Override
        public String toString() {
            return swigName;
        }
    }

    // === ENUM: Transport ===
    public static final class Transport {
        public static final Transport Unset = new Transport("Unset");
        public static final Transport Plain = new Transport("Plain");
        public static final Transport HTTPS = new Transport("HTTPS");
        public static final Transport TLS = new Transport("TLS");

        private static int swigNext = 0;
        private static final Transport[] swigValues = {Unset, Plain, HTTPS, TLS};

        private final String swigName;
        private final int swigValue;

        private Transport(String name) {
            this.swigName = name;
            this.swigValue = swigNext++;
        }

        private Transport(String name, int value) {
            this.swigName = name;
            this.swigValue = value;
            swigNext = value + 1;
        }

        private Transport(String name, Transport other) {
            this.swigName = name;
            this.swigValue = other.swigValue;
            swigNext = this.swigValue + 1;
        }

        public static Transport swigToEnum(int value) {
            for (Transport t : swigValues) {
                if (t.swigValue == value) return t;
            }
            throw new IllegalArgumentException("No enum Transport with value " + value);
        }

        public int swigValue() {
            return swigValue;
        }

        @Override
        public String toString() {
            return swigName;
        }
    }

    // === DnsServer ===
    public DnsServer() {
        this(ovpncliJNI.new_DnsServer(), true);
    }

    protected DnsServer(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(DnsServer obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_DnsServer(swigCPtr);
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

    // === Getters ===
    public DnsOptions_AddressList getAddresses() {
        long ptr = ovpncliJNI.DnsServer_addresses_get(swigCPtr, this);
        return ptr == 0 ? null : new DnsOptions_AddressList(ptr, false);
    }

    public DnsOptions_DomainsList getDomains() {
        long ptr = ovpncliJNI.DnsServer_domains_get(swigCPtr, this);
        return ptr == 0 ? null : new DnsOptions_DomainsList(ptr, false);
    }

    public String getSni() {
        return ovpncliJNI.DnsServer_sni_get(swigCPtr, this);
    }

    public Security getDnssec() {
        return Security.swigToEnum(ovpncliJNI.DnsServer_dnssec_get(swigCPtr, this));
    }

    public Transport getTransport() {
        return Transport.swigToEnum(ovpncliJNI.DnsServer_transport_get(swigCPtr, this));
    }

    // === Setters ===
    public void setAddresses(DnsOptions_AddressList list) {
        ovpncliJNI.DnsServer_addresses_set(swigCPtr, this, DnsOptions_AddressList.getCPtr(list), list);
    }

    public void setDomains(DnsOptions_DomainsList list) {
        ovpncliJNI.DnsServer_domains_set(swigCPtr, this, DnsOptions_DomainsList.getCPtr(list), list);
    }

    public void setSni(String sni) {
        ovpncliJNI.DnsServer_sni_set(swigCPtr, this, sni);
    }

    public void setDnssec(Security sec) {
        ovpncliJNI.DnsServer_dnssec_set(swigCPtr, this, sec.swigValue());
    }

    public void setTransport(Transport transport) {
        ovpncliJNI.DnsServer_transport_set(swigCPtr, this, transport.swigValue());
    }

    // === Utils ===
    public String to_string() {
        return ovpncliJNI.DnsServer_to_string__SWIG_1(swigCPtr, this);
    }

    public String to_string(String indent) {
        return ovpncliJNI.DnsServer_to_string__SWIG_0(swigCPtr, this, indent);
    }

    public String dnssec_string(Security sec) {
        return ovpncliJNI.DnsServer_dnssec_string(swigCPtr, this, sec.swigValue());
    }

    public String transport_string(Transport transport) {
        return ovpncliJNI.DnsServer_transport_string(swigCPtr, this, transport.swigValue());
    }
}
