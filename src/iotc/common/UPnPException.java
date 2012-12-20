package iotc.common;

/**
 * UPnP関連の例外
 * @author atsushi-o
 */
public class UPnPException extends Exception {

    /**
     * Creates a new instance of
     * <code>UPnPException</code> without detail message.
     */
    public UPnPException() {
    }

    /**
     * Constructs an instance of
     * <code>UPnPException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UPnPException(String msg) {
        super(msg);
    }
}
