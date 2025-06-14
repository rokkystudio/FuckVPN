package net.openvpn.openvpn;

import java.util.AbstractList;
import java.util.RandomAccess;

public class DnsOptions_AddressList extends AbstractList<DnsAddress> implements RandomAccess {
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public DnsOptions_AddressList() {
        this(ovpncliJNI.new_DnsOptions_AddressList__SWIG_0(), true);
    }

    public DnsOptions_AddressList(int count, DnsAddress prototype) {
        this(ovpncliJNI.new_DnsOptions_AddressList__SWIG_2(count, DnsAddress.getCPtr(prototype), prototype), true);
    }

    public DnsOptions_AddressList(DnsOptions_AddressList other) {
        this(ovpncliJNI.new_DnsOptions_AddressList__SWIG_1(getCPtr(other), other), true);
    }

    public DnsOptions_AddressList(DnsAddress[] array) {
        this();
        reserve(array.length);
        for (DnsAddress addr : array) {
            add(addr);
        }
    }

    public DnsOptions_AddressList(Iterable<DnsAddress> iterable) {
        this();
        for (DnsAddress addr : iterable) {
            add(addr);
        }
    }

    protected DnsOptions_AddressList(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(DnsOptions_AddressList obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_DnsOptions_AddressList(swigCPtr);
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

    // --- List methods ---

    @Override
    public void add(int index, DnsAddress element) {
        modCount++;
        ovpncliJNI.DnsOptions_AddressList_doAdd__SWIG_1(swigCPtr, this, index, DnsAddress.getCPtr(element), element);
    }

    @Override
    public boolean add(DnsAddress element) {
        modCount++;
        ovpncliJNI.DnsOptions_AddressList_doAdd__SWIG_0(swigCPtr, this, DnsAddress.getCPtr(element), element);
        return true;
    }

    @Override
    public DnsAddress get(int index) {
        return new DnsAddress(ovpncliJNI.DnsOptions_AddressList_doGet(swigCPtr, this, index), false);
    }

    @Override
    public DnsAddress set(int index, DnsAddress element) {
        return new DnsAddress(
                ovpncliJNI.DnsOptions_AddressList_doSet(swigCPtr, this, index, DnsAddress.getCPtr(element), element),
                true
        );
    }

    @Override
    public DnsAddress remove(int index) {
        modCount++;
        return new DnsAddress(ovpncliJNI.DnsOptions_AddressList_doRemove(swigCPtr, this, index), true);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        ovpncliJNI.DnsOptions_AddressList_doRemoveRange(swigCPtr, this, fromIndex, toIndex);
    }

    @Override
    public int size() {
        return ovpncliJNI.DnsOptions_AddressList_doSize(swigCPtr, this);
    }

    @Override
    public void clear() {
        ovpncliJNI.DnsOptions_AddressList_clear(swigCPtr, this);
    }

    @Override
    public boolean isEmpty() {
        return ovpncliJNI.DnsOptions_AddressList_isEmpty(swigCPtr, this);
    }

    // --- Utility methods ---

    public long capacity() {
        return ovpncliJNI.DnsOptions_AddressList_capacity(swigCPtr, this);
    }

    public void reserve(long capacity) {
        ovpncliJNI.DnsOptions_AddressList_reserve(swigCPtr, this, capacity);
    }
}
