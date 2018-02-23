package info.xiancloud.apidoc.handler;

public abstract class BaseBuildHandler implements BuildHandler {

    private BuildCallback callback;

    public BuildHandler callback(BuildCallback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * invokeCallback after bytes are ready.
     *
     * @param bytes md file bytes
     */
    protected void invokeCallback(byte[] bytes) {
        if (callback != null) {
            callback.call(bytes);
        }
    }
}
