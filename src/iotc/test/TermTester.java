package iotc.test;

import java.io.FileWriter;
import java.util.Map;
import java.util.logging.Logger;

/**
 * User: atsushi-o
 * Date: 13/01/28
 * Time: 19:48
 */
public class TermTester implements Runnable {
    private Thread th;
    private DummyUPnPDevice duppd;
    private FileWriter fw;
    private Map<Long, Long> timeLogs;
    private int count = 0;

    private static final String OUT_PATH;
    private static final Logger LOG;
    static {
        OUT_PATH = "C:\\Users\\atsushi-o\\Dropbox\\NAIST\\ubi-lab\\ex2-";
        LOG = Logger.getLogger(TermTester.class.getName());
    }

    public TermTester() {
        th = new Thread(this);
        init();
    }

    private void init() {
        duppd = new DummyUPnPDevice("");
    }

    public void start() {
        duppd.start();
        th.start();
    }

    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
