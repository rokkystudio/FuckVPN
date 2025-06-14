package net.openvpn.openvpn;

import java.util.AbstractList;
import java.util.RandomAccess;

public class DnsOptions_DomainsList extends AbstractList<DnsDomain> implements RandomAccess
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public DnsOptions_DomainsList() {
        this(ovpncliJNI.new_DnsOptions_DomainsList__SWIG_0(), true);
    }

    public DnsOptions_DomainsList(int count, DnsDomain prototype) {
        this(ovpncliJNI.new_DnsOptions_DomainsList__SWIG_2(count, DnsDomain.getCPtr(prototype), prototype), true);
    }

    public DnsOptions_DomainsList(DnsOptions_DomainsList other) {
        this(ovpncliJNI.new_DnsOptions_DomainsList__SWIG_1(getCPtr(other), other), true);
    }

    public DnsOptions_DomainsList(DnsDomain[] array) {
        this();
        reserve(array.length);
        for (DnsDomain item : array) {
            add(item);
        }
    }

    public DnsOptions_DomainsList(Iterable<DnsDomain> iterable) {
        this();
        for (DnsDomain item : iterable) {
            add(item);
        }
    }

    protected DnsOptions_DomainsList(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(DnsOptions_DomainsList obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_DnsOptions_DomainsList(swigCPtr);
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
    public void add(int index, DnsDomain element) {
        modCount++;
        ovpncliJNI.DnsOptions_DomainsList_doAdd__SWIG_1(swigCPtr, this, index, DnsDomain.getCPtr(element), element);
    }

    @Override
    public boolean add(DnsDomain element) {
        modCount++;
        ovpncliJNI.DnsOptions_DomainsList_doAdd__SWIG_0(swigCPtr, this, DnsDomain.getCPtr(element), element);
        return true;
    }

    @Override
    public DnsDomain get(int index) {
        return new DnsDomain(ovpncliJNI.DnsOptions_DomainsList_doGet(swigCPtr, this, index), false);
    }

    @Override
    public DnsDomain set(int index, DnsDomain element) {
        return new DnsDomain(
                ovpncliJNI.DnsOptions_DomainsList_doSet(swigCPtr, this, index, DnsDomain.getCPtr(element), element),
                true
        );
    }

    @Override
    public DnsDomain remove(int index) {
        modCount++;
        return new DnsDomain(ovpncliJNI.DnsOptions_DomainsList_doRemove(swigCPtr, this, index), true);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        ovpncliJNI.DnsOptions_DomainsList_doRemoveRange(swigCPtr, this, fromIndex, toIndex);
    }

    @Override
    public int size() {
        return ovpncliJNI.DnsOptions_DomainsList_doSize(swigCPtr, this);
    }

    @Override
    public void clear() {
        ovpncliJNI.DnsOptions_DomainsList_clear(swigCPtr, this);
    }

    public boolean isEmpty() {
        return ovpncliJNI.DnsOptions_DomainsList_isEmpty(swigCPtr, this);
    }

    public long capacity() {
        return ovpncliJNI.DnsOptions_DomainsList_capacity(swigCPtr, this);
    }

    public void reserve(long capacity) {
        ovpncliJNI.DnsOptions_DomainsList_reserve(swigCPtr, this, capacity);
    }
}
