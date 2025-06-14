package net.openvpn.openvpn;

import java.util.AbstractList;
import java.util.RandomAccess;

public class ClientAPI_LLVector extends AbstractList<Long> implements RandomAccess
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public ClientAPI_LLVector() {
        this(ovpncliJNI.new_ClientAPI_LLVector__SWIG_0(), true);
    }

    public ClientAPI_LLVector(int size, long value) {
        this(ovpncliJNI.new_ClientAPI_LLVector__SWIG_2(size, value), true);
    }

    public ClientAPI_LLVector(long[] array) {
        this();
        reserve(array.length);
        for (long val : array) {
            add(val);
        }
    }

    public ClientAPI_LLVector(Iterable<Long> iterable) {
        this();
        for (Long val : iterable) {
            add(val);
        }
    }

    public ClientAPI_LLVector(ClientAPI_LLVector other) {
        this(
            ovpncliJNI.new_ClientAPI_LLVector__SWIG_1(
                getCPtr(other), other
            ),
            true
        );
    }

    protected ClientAPI_LLVector(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_LLVector obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_LLVector(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    protected void finalize() {
        try {
            delete();
        } finally {
            try {
                super.finalize();
            } catch (Throwable ignored) {
            }
        }
    }

    // Core access methods

    public Long get(int index) {
        return doGet(index);
    }

    public Long set(int index, Long value) {
        return doSet(index, value);
    }

    public void add(int index, Long value) {
        modCount++;
        doAdd(index, value);
    }

    public boolean add(Long value) {
        modCount++;
        doAdd(value);
        return true;
    }

    public Long remove(int index) {
        modCount++;
        return doRemove(index);
    }

    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        doRemoveRange(fromIndex, toIndex);
    }

    public int size() {
        return doSize();
    }

    public boolean isEmpty() {
        return ovpncliJNI.ClientAPI_LLVector_isEmpty(swigCPtr, this);
    }

    public long capacity() {
        return ovpncliJNI.ClientAPI_LLVector_capacity(swigCPtr, this);
    }

    public void clear() {
        ovpncliJNI.ClientAPI_LLVector_clear(swigCPtr, this);
    }

    public void reserve(long size) {
        ovpncliJNI.ClientAPI_LLVector_reserve(swigCPtr, this, size);
    }

    // Internal native forwarding

    private void doAdd(long value) {
        ovpncliJNI.ClientAPI_LLVector_doAdd__SWIG_0(swigCPtr, this, value);
    }

    private void doAdd(int index, long value) {
        ovpncliJNI.ClientAPI_LLVector_doAdd__SWIG_1(swigCPtr, this, index, value);
    }

    private long doGet(int index) {
        return ovpncliJNI.ClientAPI_LLVector_doGet(swigCPtr, this, index);
    }

    private long doSet(int index, long value) {
        return ovpncliJNI.ClientAPI_LLVector_doSet(swigCPtr, this, index, value);
    }

    private long doRemove(int index) {
        return ovpncliJNI.ClientAPI_LLVector_doRemove(swigCPtr, this, index);
    }

    private void doRemoveRange(int start, int end) {
        ovpncliJNI.ClientAPI_LLVector_doRemoveRange(swigCPtr, this, start, end);
    }

    private int doSize() {
        return ovpncliJNI.ClientAPI_LLVector_doSize(swigCPtr, this);
    }
}
