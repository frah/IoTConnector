package iotc;

import iotc.gui.MainOverviewWindow;
import iotc.medium.SMediumMap;
import iotc.medium.Twitter;
import iotc.test.DummyDeviceLauncher;
import iotc.test.DummySunSPOTDevice;
import iotc.test.TermTester;
import iotc.test.TwitterTester;

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

    private MainOverviewWindow ovw;

    private DummyDeviceLauncher dummy;
    private DummySunSPOTDevice dsun;
    private TwitterTester ttester;

    private static final String DEFAULT_LOGGING_PROPERTIES;
    private static final String DEBUG_LOGGING_PROPERTIES;
    private static final Logger LOG;
    static {
        DEFAULT_LOGGING_PROPERTIES = "default_logging.properties";
        DEBUG_LOGGING_PROPERTIES = "debug_logging.properties";
        LOG = Logger.getLogger(IoTConnector.class.getName());
    }

    public IoTConnector(boolean debug, int dummyNum, int testMode) {
        this.debug = debug;

        // <editor-fold defaultstate="collapsed" desc="ロギング設定読み込み">
        java.io.InputStream in;
        if (debug) {
            LOG.info("Start with DEBUG MODE");
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
        valChk = new VariableChecker();
        upnp.addListener(ovw, valChk);

        operator = new CommandOperator();

        SMediumMap.put(new Twitter(debug));
        SMediumMap.addListenerForAll(operator);

        if (debug && dummyNum > 0) {
            LOG.log(Level.INFO, "DummyDevice num: {0}", dummyNum);
            LOG.log(Level.INFO, "Run testing script: {0}", testMode);

            switch (testMode) {
                case 1: {
                    dummy = new DummyDeviceLauncher(dummyNum);
                    dummy.start();

                    try {
                        ttester = new TwitterTester(dummyNum, 100);
                        operator.setExpListener(ttester);
                        ttester.start();
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, "Initialize TwitterTester failed", e);
                    }
                    break;
                }
                case 2: {
                    dummy = new DummyDeviceLauncher(dummyNum-1);
                    dummy.start();
                    new TermTester().start();
                    break;
                }
                case 3: {
                    dsun = new DummySunSPOTDevice("SunSPOT-dummy");
                }
            }
        }

        // VM終了時の処理
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("IoTConnector will shutdown.");
                if (IoTConnector.this.debug) {
                    if (ttester == null) ttester.stop();
                    if (dummy == null) dummy.stop();
                    if (dsun == null) dsun.stop();
                }
                upnp.stop();
            }
        });
    }

    public static void main(String[] args) {
        boolean debug = false;
        int dummyNum = 0;
        int testMode = 0;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-D":
                    debug = true;
                    break;
                case "-d":
                    if (++i < args.length) {
                        dummyNum = Integer.valueOf(args[i]);
                    }
                    break;
                case "-t":
                    if (++i < args.length) {
                        testMode = Integer.valueOf(args[i]);
                    }
                default:
                    break;
            }
        }
        new IoTConnector(debug, dummyNum, testMode);
    }
}
