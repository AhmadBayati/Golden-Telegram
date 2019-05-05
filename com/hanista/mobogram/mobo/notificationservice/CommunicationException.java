package com.hanista.mobogram.mobo.notificationservice;

/* renamed from: com.hanista.mobogram.mobo.notificationservice.a */
public class CommunicationException extends Exception {
    private int f1972a;

    public CommunicationException(int i) {
        this.f1972a = i;
    }

    public CommunicationException(int i, Throwable th) {
        super(th);
        this.f1972a = i;
    }
}
