package net.openvpn.openvpn;

public class ClientAPI_DynamicChallenge
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public ClientAPI_DynamicChallenge() {
        this(ovpncliJNI.new_ClientAPI_DynamicChallenge(), true);
    }

    protected ClientAPI_DynamicChallenge(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_DynamicChallenge obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_DynamicChallenge(swigCPtr);
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

    public String getChallenge() {
        return ovpncliJNI.ClientAPI_DynamicChallenge_challenge_get(
                swigCPtr, this
        );
    }

    public void setChallenge(String value) {
        ovpncliJNI.ClientAPI_DynamicChallenge_challenge_set(
                swigCPtr, this, value
        );
    }

    public boolean getEcho() {
        return ovpncliJNI.ClientAPI_DynamicChallenge_echo_get(
                swigCPtr, this
        );
    }

    public void setEcho(boolean value) {
        ovpncliJNI.ClientAPI_DynamicChallenge_echo_set(
                swigCPtr, this, value
        );
    }

    public boolean getResponseRequired() {
        return ovpncliJNI.ClientAPI_DynamicChallenge_responseRequired_get(
                swigCPtr, this
        );
    }

    public void setResponseRequired(boolean value) {
        ovpncliJNI.ClientAPI_DynamicChallenge_responseRequired_set(
                swigCPtr, this, value
        );
    }

    public String getStateID() {
        return ovpncliJNI.ClientAPI_DynamicChallenge_stateID_get(
                swigCPtr, this
        );
    }

    public void setStateID(String value) {
        ovpncliJNI.ClientAPI_DynamicChallenge_stateID_set(
                swigCPtr, this, value
        );
    }
}
