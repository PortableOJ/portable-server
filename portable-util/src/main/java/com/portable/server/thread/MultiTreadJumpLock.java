package com.portable.server.thread;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

import com.portable.server.exception.PortableErrors;
import com.portable.server.exception.PortableException;

/**
 * @author shiroha
 */
public class MultiTreadJumpLock implements Closeable {

    private final AtomicBoolean locked;

    public MultiTreadJumpLock() {
        this.locked = new AtomicBoolean(false);
    }

    public MultiTreadJumpLock lock() throws PortableException {
        if (tryLock()) {
            return this;
        }
        throw PortableErrors.LOCAL_THREAD_LOCK_FAIL.ofThrow();
    }

    public Boolean tryLock() {
        if (locked.get()) {
            return false;
        }

        synchronized (this) {
            if (locked.get()) {
                return false;
            }
        }

        locked.set(true);
        return true;
    }

    @Override
    public void close() {
        locked.set(false);
    }
}
