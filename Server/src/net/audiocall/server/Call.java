package net.audiocall.server;

import net.audiocall.Constants;

public class Call {

    public enum State {
        DIALING,
        TALKING,
        FINISHED
    }

    private final ClientHandler caller;
    private final ClientHandler callee;
    private final long startTime;
    private State state;

    public Call(ClientHandler caller, ClientHandler callee) {
        this.caller = caller;
        this.callee = callee;
        this.startTime = System.currentTimeMillis();
    }

    public ClientHandler getCaller() {
        return caller;
    }

    public ClientHandler getCallee() {
        return callee;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isDialingTimeout() {
        return System.currentTimeMillis() - startTime > Constants.SERVER_CALL_DIALING_TIMEOUT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Call call = (Call) o;

        if (!caller.equals(call.caller)) return false;
        return callee.equals(call.callee);
    }

    @Override
    public int hashCode() {
        int result = caller.hashCode();
        result = 31 * result + callee.hashCode();
        return result;
    }
}
