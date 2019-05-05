package org.aspectj.runtime.internal.cflowstack;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class ThreadStackImpl11 implements ThreadStack {
    private static final int COLLECT_AT = 20000;
    private static final int MIN_COLLECT_AT = 100;
    private Stack cached_stack;
    private Thread cached_thread;
    private int change_count;
    private Hashtable stacks;

    public ThreadStackImpl11() {
        this.stacks = new Hashtable();
        this.change_count = 0;
    }

    public synchronized Stack getThreadStack() {
        if (Thread.currentThread() != this.cached_thread) {
            this.cached_thread = Thread.currentThread();
            this.cached_stack = (Stack) this.stacks.get(this.cached_thread);
            if (this.cached_stack == null) {
                this.cached_stack = new Stack();
                this.stacks.put(this.cached_thread, this.cached_stack);
            }
            this.change_count++;
            if (this.change_count > Math.max(MIN_COLLECT_AT, COLLECT_AT / Math.max(1, this.stacks.size()))) {
                Vector stack = new Stack();
                Enumeration keys = this.stacks.keys();
                while (keys.hasMoreElements()) {
                    Thread thread = (Thread) keys.nextElement();
                    if (!thread.isAlive()) {
                        stack.push(thread);
                    }
                }
                Enumeration elements = stack.elements();
                while (elements.hasMoreElements()) {
                    this.stacks.remove((Thread) elements.nextElement());
                }
                this.change_count = 0;
            }
        }
        return this.cached_stack;
    }

    public void removeThreadStack() {
    }
}
