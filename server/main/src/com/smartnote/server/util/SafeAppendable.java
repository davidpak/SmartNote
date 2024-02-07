package com.smartnote.server.util;

import java.io.IOException;

public class SafeAppendable implements Appendable {
    private final Appendable a;

    public SafeAppendable(Appendable a) {
        this.a = a;
    }

    @Override
    public Appendable append(CharSequence csq) {
        try {
            a.append(csq);
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) {
        try {
            a.append(csq, start, end);
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Appendable append(char c) {
        try {
            a.append(c);
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
