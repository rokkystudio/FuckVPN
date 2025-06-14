package com.openvpn.openvpn.dpc;

public class Request
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public static class Antivirus {
        protected transient boolean swigCMemOwn;
        private transient long swigCPtr;

        public synchronized void delete() {
            if (swigCPtr != 0) {
                if (swigCMemOwn) {
                    swigCMemOwn = false;
                    dpc_apiJNI.delete_Request_Antivirus(swigCPtr);
                }
                swigCPtr = 0;
            }
        }

        @Override
        protected void finalize() {
            delete();
        }
    }

    public static class Certificate {
        protected transient boolean swigCMemOwn;
        private transient long swigCPtr;

        public synchronized void delete() {
            if (swigCPtr != 0) {
                if (swigCMemOwn) {
                    swigCMemOwn = false;
                    dpc_apiJNI.delete_Request_Certificate(swigCPtr);
                }
                swigCPtr = 0;
            }
        }

        @Override
        protected void finalize() {
            delete();
        }
    }

    public static class DiscEncryption {
        protected transient boolean swigCMemOwn;
        private transient long swigCPtr;

        public synchronized void delete() {
            if (swigCPtr != 0) {
                if (swigCMemOwn) {
                    swigCMemOwn = false;
                    dpc_apiJNI.delete_Request_DiscEncryption(swigCPtr);
                }
                swigCPtr = 0;
            }
        }

        @Override
        protected void finalize() {
            delete();
        }
    }

    public static class EPKICertificate {
        protected transient boolean swigCMemOwn;
        private transient long swigCPtr;

        public synchronized void delete() {
            if (swigCPtr != 0) {
                if (swigCMemOwn) {
                    swigCMemOwn = false;
                    dpc_apiJNI.delete_Request_EPKICertificate(swigCPtr);
                }
                swigCPtr = 0;
            }
        }

        @Override
        protected void finalize() {
            delete();
        }
    }

    public static class EPKISignature {
        protected transient boolean swigCMemOwn;
        private transient long swigCPtr;

        public synchronized void delete() {
            if (swigCPtr != 0) {
                if (swigCMemOwn) {
                    swigCMemOwn = false;
                    dpc_apiJNI.delete_Request_EPKISignature(swigCPtr);
                }
                swigCPtr = 0;
            }
        }

        @Override
        protected void finalize() {
            delete();
        }
    }

    public static class OSInfo {
        protected transient boolean swigCMemOwn;
        private transient long swigCPtr;

        public synchronized void delete() {
            if (swigCPtr != 0) {
                if (swigCMemOwn) {
                    swigCMemOwn = false;
                    dpc_apiJNI.delete_Request_OSInfo(swigCPtr);
                }
                swigCPtr = 0;
            }
        }

        @Override
        protected void finalize() {
            delete();
        }
    }

    protected Request(long ptr, boolean ownsMemory) {
        this.swigCMemOwn = ownsMemory;
        this.swigCPtr = ptr;
    }

    public static Request fromJSON(JsonValue jsonValue) {
        return new Request(
                dpc_apiJNI.Request_fromJSON(JsonValue.getCPtr(jsonValue), jsonValue),
                true
        );
    }

    protected static long getCPtr(Request obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                dpc_apiJNI.delete_Request(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    @Override
    protected void finalize() {
        delete();
    }
}
