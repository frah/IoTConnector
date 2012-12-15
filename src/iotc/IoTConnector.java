package iotc;

import iotc.gui.MainOverviewWindow;
import iotc.test.DummyUPnPDevice;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * IoTConnectorメインクラス
 * @author atsushi-o
 */
public class IoTConnector {
    private boolean debug;
    private UPnPDevices upnp;
    private MainOverviewWindow ovw;
    private DummyUPnPDevice d;
    private static final String DEFAULT_LOGGING_PROPERTIES;
    private static final String DEBUG_LOGGING_PROPERTIES;
    private static final Logger LOG;
    static {
        DEFAULT_LOGGING_PROPERTIES = "default_logging.properties";
        DEBUG_LOGGING_PROPERTIES = "debug_logging.properties";
        LOG = Logger.getLogger(IoTConnector.class.getName());
    }

    public IoTConnector(boolean debug) {
        this.debug = debug;

        // <editor-fold defaultstate="collapsed" desc="ロギング設定読み込み">
        java.io.InputStream in;
        if (debug) {
            in = this.getClass().getResourceAsStream("/META-INF/"+DEBUG_LOGGING_PROPERTIES);
        } else {
            in = this.getClass().getResourceAsStream("/META-INF/"+DEFAULT_LOGGING_PROPERTIES);
        }
        if (in == null) {
            LOG.warning("Logging properties file not found");
        } else {
            try {
                LogManager.getLogManager().readConfiguration(in);
            } catch (java.io.IOException ex) {
                LOG.log(Level.WARNING, "Logging properties file reading failed", ex);
            } finally {
                try {
                    if (in != null) in.close();
                } catch (java.io.IOException ex) {}
            }
        }// </editor-fold>

        upnp = UPnPDevices.getInstance();
        ovw = new MainOverviewWindow();
        ovw.setVisible(true);
        upnp.addListener(ovw);

        if (debug) {
            d = new DummyUPnPDevice("Dummy-c0d2c761-777a-43bb-be25-7e9ccd714044");
            d.start();
        }

        // VM終了時の処理
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("IoTConnector will shutdown.");
                if (IoTConnector.this.debug) {
                    d.stop();
                }
                upnp.stop();
            }
        });
    }

    public static void main(String[] args) {
        boolean debug = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-d")) debug = true;
        }
        new IoTConnector(debug);
    }
}
