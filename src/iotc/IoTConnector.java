package iotc;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author atsushi-o
 */
public class IoTConnector {
    private UPnPDevices upnp;
    private static final Logger LOG;
    static {
        LOG = Logger.getLogger(IoTConnector.class.getName());
    }

    public IoTConnector() {
        upnp = UPnPDevices.getInstance();

        // VM終了時の処理
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("IoTConnector will shutdown.");
                upnp.stop();
            }
        });
    }

    public static void main(String[] args) {
        new IoTConnector();
    }
}
