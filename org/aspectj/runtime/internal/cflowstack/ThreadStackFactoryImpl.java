package org.aspectj.runtime.internal.cflowstack;

import java.util.Stack;

public class ThreadStackFactoryImpl implements ThreadStackFactory {

    /* renamed from: org.aspectj.runtime.internal.cflowstack.ThreadStackFactoryImpl.1 */
    static class C19771 {
    }

    private static class ThreadCounterImpl extends ThreadLocal implements ThreadCounter {

        static class Counter {
            protected int value;

            Counter() {
                this.value = 0;
            }
        }

        private ThreadCounterImpl() {
        }

        ThreadCounterImpl(C19771 c19771) {
            this();
        }

        public void dec() {
            Counter threadCounter = getThreadCounter();
            threadCounter.value--;
        }

        public Counter getThreadCounter() {
            return (Counter) get();
        }

        public void inc() {
            Counter threadCounter = getThreadCounter();
            threadCounter.value++;
        }

        public Object initialValue() {
            return new Counter();
        }

        public boolean isNotZero() {
            return getThreadCounter().value != 0;
        }

        public void removeThreadCounter() {
            remove();
        }
    }

    private static class ThreadStackImpl extends ThreadLocal implements ThreadStack {
        private ThreadStackImpl() {
        }

        ThreadStackImpl(C19771 c19771) {
            this();
        }

        public Stack getThreadStack() {
            return (Stack) get();
        }

        public Object initialValue() {
            return new Stack();
        }

        public void removeThreadStack() {
            remove();
        }
    }

    public ThreadCounter getNewThreadCounter() {
        return new ThreadCounterImpl(null);
    }

    public ThreadStack getNewThreadStack() {
        return new ThreadStackImpl(null);
    }
}
