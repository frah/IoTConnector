package iotc;

import iotc.db.HibernateUtil;
import iotc.gui.MainOverviewWindow;
import iotc.medium.SMediumMap;
import iotc.medium.Twitter;
import iotc.test.DummySunSPOTDevice;
import iotc.test.DummyUPnPDevice;
import org.hibernate.Session;

import java.util.ArrayList;
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
    private CommandOperator operator;
    private VariableChecker valChk;
    private Session masterSession;

    private MainOverviewWindow ovw;

    /** Variables for DEBUG */
    private ArrayList<DummyUPnPDevice> dummy;
    private DummySunSPOTDevice dsun;
    /** Variables for DEBUG END */

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

        masterSession = HibernateUtil.getSessionFactory().openSession();
        upnp = UPnPDevices.getInstance();
        ovw = new MainOverviewWindow();
        ovw.setVisible(true);
        valChk = new VariableChecker();
        upnp.addListener(ovw, valChk);

        operator = new CommandOperator();

        SMediumMap.put(new Twitter());
        SMediumMap.addListenerForAll(operator);

        if (debug) {
            LOG.info("Start with DEBUG MODE");
            dummy = new ArrayList();
            dummy.add(new DummyUPnPDevice("Dummy-c0d2c761-777a-43bb-be25-7e9ccd714044"));
            dsun = new DummySunSPOTDevice("SunSPOT-dummy");
            for (DummyUPnPDevice d : dummy) {
                d.start();
            }
        }

        // VM終了時の処理
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("IoTConnector will shutdown.");
                if (IoTConnector.this.debug) {
                    for (DummyUPnPDevice d : dummy) {
                        d.stop();
                    }
                    dsun.stop();
                }
                upnp.stop();
                masterSession.close();
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
