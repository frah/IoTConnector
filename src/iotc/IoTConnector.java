package iotc;

import java.util.logging.Logger;
import java.util.logging.Level;
import iotc.gui.MainOverviewWindow;
import iotc.test.DummyUPnPDevice;

/**
 *
 * @author atsushi-o
 */
public class IoTConnector {
    private UPnPDevices upnp;
    private MainOverviewWindow ovw;
    private DummyUPnPDevice d;
    private static final Logger LOG;
    static {
        LOG = Logger.getLogger(IoTConnector.class.getName());
    }

    public IoTConnector() {
        upnp = UPnPDevices.getInstance();
        ovw = new MainOverviewWindow();
        ovw.setVisible(true);
        upnp.addListener(ovw);

        d = new DummyUPnPDevice("Dummy-c0d2c761-777a-43bb-be25-7e9ccd714044");
        d.start();

        // VM終了時の処理
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("IoTConnector will shutdown.");
                d.stop();
                upnp.stop();
            }
        });
    }

    public static void main(String[] args) {
        new IoTConnector();
    }
}
