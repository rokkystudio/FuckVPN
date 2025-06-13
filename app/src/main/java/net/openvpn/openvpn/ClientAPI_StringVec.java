package net.openvpn.openvpn;

import java.util.AbstractList;
import java.util.RandomAccess;

public class ClientAPI_StringVec extends AbstractList<String> implements RandomAccess
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public ClientAPI_StringVec() {
        this(ovpncliJNI.new_ClientAPI_StringVec__SWIG_0(), true);
    }

    public ClientAPI_StringVec(int count, String value) {
        this(ovpncliJNI.new_ClientAPI_StringVec__SWIG_2(count, value), true);
    }

    protected ClientAPI_StringVec(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    public ClientAPI_StringVec(Iterable<String> iterable) {
        this();
        for (String item : iterable) {
            add(item);
        }
    }

    public ClientAPI_StringVec(ClientAPI_StringVec other) {
        this(ovpncliJNI.new_ClientAPI_StringVec__SWIG_1(
                getCPtr(other), other
        ), true);
    }

    // Manual iteration is used instead of addAll() due to JNI vector semantics
    public ClientAPI_StringVec(String[] array) {
        this();
        reserve(array.length);
        for (String value : array) {
            add(value);
        }
    }

    private void doAdd(int index, String value) {
        ovpncliJNI.ClientAPI_StringVec_doAdd__SWIG_1(swigCPtr, this, index, value);
    }

    private void doAdd(String value) {
        ovpncliJNI.ClientAPI_StringVec_doAdd__SWIG_0(swigCPtr, this, value);
    }

    private String doGet(int index) {
        return ovpncliJNI.ClientAPI_StringVec_doGet(swigCPtr, this, index);
    }

    private String doRemove(int index) {
        return ovpncliJNI.ClientAPI_StringVec_doRemove(swigCPtr, this, index);
    }

    private void doRemoveRange(int start, int end) {
        ovpncliJNI.ClientAPI_StringVec_doRemoveRange(swigCPtr, this, start, end);
    }

    private String doSet(int index, String value) {
        return ovpncliJNI.ClientAPI_StringVec_doSet(swigCPtr, this, index, value);
    }

    private int doSize() {
        return ovpncliJNI.ClientAPI_StringVec_doSize(swigCPtr, this);
    }

    protected static long getCPtr(ClientAPI_StringVec obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    @Override
    public void add(int index, String element) {
        modCount++;
        doAdd(index, element);
    }

    @Override
    public boolean add(String element) {
        modCount++;
        doAdd(element);
        return true;
    }

    public long capacity() {
        return ovpncliJNI.ClientAPI_StringVec_capacity(swigCPtr, this);
    }

    @Override
    public void clear() {
        ovpncliJNI.ClientAPI_StringVec_clear(swigCPtr, this);
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_StringVec(swigCPtr);
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
    public String get(int index) {
        return doGet(index);
    }

    @Override
    public boolean isEmpty() {
        return ovpncliJNI.ClientAPI_StringVec_isEmpty(swigCPtr, this);
    }

    @Override
    public String remove(int index) {
        modCount++;
        return doRemove(index);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        doRemoveRange(fromIndex, toIndex);
    }

    public void reserve(long size) {
        ovpncliJNI.ClientAPI_StringVec_reserve(swigCPtr, this, size);
    }

    @Override
    public String set(int index, String element) {
        return doSet(index, element);
    }

    @Override
    public int size() {
        return doSize();
    }
}
