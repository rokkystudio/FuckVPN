package net.openvpn.openvpn;

import java.util.AbstractList;
import java.util.RandomAccess;

public class ClientAPI_ServerEntryVector
        extends AbstractList<ClientAPI_ServerEntry>
        implements RandomAccess
{

    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public ClientAPI_ServerEntryVector() {
        this(ovpncliJNI.new_ClientAPI_ServerEntryVector__SWIG_0(), true);
    }

    public ClientAPI_ServerEntryVector(int size, ClientAPI_ServerEntry value) {
        this(
                ovpncliJNI.new_ClientAPI_ServerEntryVector__SWIG_2(
                        size,
                        ClientAPI_ServerEntry.getCPtr(value),
                        value
                ),
                true
        );
    }

    protected ClientAPI_ServerEntryVector(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    public ClientAPI_ServerEntryVector(Iterable<ClientAPI_ServerEntry> values) {
        this();
        for (ClientAPI_ServerEntry value : values) {
            add(value);
        }
    }

    public ClientAPI_ServerEntryVector(ClientAPI_ServerEntryVector other) {
        this(
                ovpncliJNI.new_ClientAPI_ServerEntryVector__SWIG_1(
                        getCPtr(other),
                        other
                ),
                true
        );
    }

    // Manual iteration is used instead of addAll() due to JNI vector semantics
    public ClientAPI_ServerEntryVector(ClientAPI_ServerEntry[] array) {
        this();
        reserve(array.length);
        for (ClientAPI_ServerEntry value : array) {
            add(value);
        }
    }

    private void doAdd(int index, ClientAPI_ServerEntry value) {
        ovpncliJNI.ClientAPI_ServerEntryVector_doAdd__SWIG_1(
                swigCPtr, this,
                index,
                ClientAPI_ServerEntry.getCPtr(value),
                value
        );
    }

    private void doAdd(ClientAPI_ServerEntry value) {
        ovpncliJNI.ClientAPI_ServerEntryVector_doAdd__SWIG_0(
                swigCPtr, this,
                ClientAPI_ServerEntry.getCPtr(value),
                value
        );
    }

    private ClientAPI_ServerEntry doGet(int index) {
        return new ClientAPI_ServerEntry(
                ovpncliJNI.ClientAPI_ServerEntryVector_doGet(
                        swigCPtr, this, index
                ),
                false
        );
    }

    private ClientAPI_ServerEntry doRemove(int index) {
        return new ClientAPI_ServerEntry(
                ovpncliJNI.ClientAPI_ServerEntryVector_doRemove(
                        swigCPtr, this, index
                ),
                true
        );
    }

    private void doRemoveRange(int fromIndex, int toIndex) {
        ovpncliJNI.ClientAPI_ServerEntryVector_doRemoveRange(
                swigCPtr, this, fromIndex, toIndex
        );
    }

    private ClientAPI_ServerEntry doSet(int index, ClientAPI_ServerEntry value) {
        return new ClientAPI_ServerEntry(
                ovpncliJNI.ClientAPI_ServerEntryVector_doSet(
                        swigCPtr, this,
                        index,
                        ClientAPI_ServerEntry.getCPtr(value),
                        value
                ),
                true
        );
    }

    private int doSize() {
        return ovpncliJNI.ClientAPI_ServerEntryVector_doSize(swigCPtr, this);
    }

    protected static long getCPtr(ClientAPI_ServerEntryVector obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    @Override
    public void add(int index, ClientAPI_ServerEntry value) {
        modCount++;
        doAdd(index, value);
    }

    @Override
    public boolean add(ClientAPI_ServerEntry value) {
        modCount++;
        doAdd(value);
        return true;
    }

    public long capacity() {
        return ovpncliJNI.ClientAPI_ServerEntryVector_capacity(swigCPtr, this);
    }

    @Override
    public void clear() {
        ovpncliJNI.ClientAPI_ServerEntryVector_clear(swigCPtr, this);
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_ServerEntryVector(swigCPtr);
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

    @Override
    public ClientAPI_ServerEntry get(int index) {
        return doGet(index);
    }

    @Override
    public boolean isEmpty() {
        return ovpncliJNI.ClientAPI_ServerEntryVector_isEmpty(swigCPtr, this);
    }

    @Override
    public ClientAPI_ServerEntry remove(int index) {
        modCount++;
        return doRemove(index);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        doRemoveRange(fromIndex, toIndex);
    }

    public void reserve(long n) {
        ovpncliJNI.ClientAPI_ServerEntryVector_reserve(swigCPtr, this, n);
    }

    @Override
    public ClientAPI_ServerEntry set(int index, ClientAPI_ServerEntry value) {
        return doSet(index, value);
    }

    @Override
    public int size() {
        return doSize();
    }
}
