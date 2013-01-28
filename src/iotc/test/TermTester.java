package iotc.test;

import iotc.db.*;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 遠隔自動制御用実験プログラム
 * User: atsushi-o
 * Date: 13/01/28
 * Time: 19:48
 */
public class TermTester implements Runnable, ExpEventListener {
    private Thread th;
    private DummyUPnPDevice duppd;
    private FileWriter fw;
    private LinkedList<Long> timeLogs;
    private int count = 0;

    private static final String OUT_PATH;
    private static final Logger LOG;
    static {
        OUT_PATH = "C:\\Users\\atsushi-o\\Dropbox\\NAIST\\ubi-lab\\ex2-";
        LOG = Logger.getLogger(TermTester.class.getName());
    }

    /**
     * イベントを発生させる回数を指定して初期化
     * @param count イベントを発生させる回数
     */
    public TermTester(int count) {
        th = new Thread(this);
        timeLogs = new LinkedList<>();
        this.count = count;
        init();
    }

    /**
     * ターゲットUPnPデバイスの初期化を行う
     */
    private void init() {
        duppd = new DummyUPnPDevice("TermTarget");
        String udn = duppd.getUDN();

        Session s = HibernateUtil.getSessionFactory().openSession();
        Room r = (Room)s.load(Room.class, 1);
        Query q = s.getNamedQuery("Device.findFromUDN");
        q.setString("udn", udn);
        Object o = q.uniqueResult();
        if (o == null || !(o instanceof Device)) {
            Device d = new Device();
            d.setName(duppd.getFriendlyName());
            d.setUdn(udn);
            d.setType(DeviceType.OtherUPnP.getId());
            d.setRoom(r);

            Transaction t = null;
            try {
                t = s.beginTransaction();
                s.save(d);
                t.commit();
            } catch (HibernateException ex) {
                if (t != null) t.rollback();
            }
            t = null;

            Sensor sens = new Sensor();
            sens.setDevice(d);
            sens.setName("Temperature");
            sens.setSensorType((SensorType)s.load(SensorType.class, 1));

            try {
                t = s.beginTransaction();
                s.save(sens);
                t.commit();
            } catch (HibernateException ex) {
                if (t != null) t.rollback();
            }
            t = null;
        }
        s.close();
    }

    /**
     * テストスレッドを開始する
     */
    public void start() {
        duppd.start();
        th.start();
    }

    @Override
    public void run() {
        Random rand = new Random(System.currentTimeMillis());

        for (int termNum : new int[]{1, 10, 50, 100}) {
            Session s = HibernateUtil.getSessionFactory().openSession();
            Transaction t = null;
            try {
                fw = new FileWriter(OUT_PATH+termNum+".csv");
            } catch (IOException e) {
                continue;
            }

            Query q = s.getNamedQuery("Device.findFromUDN");
            q.setString("udn", duppd.getUDN());
            Device d = (Device)q.uniqueResult();
            Sensor sens = (Sensor)d.getSensors().toArray()[0];

            /* 条件文を設定 */
            Set ts =  sens.getTerms();
            if (ts.size() != termNum) {
                ts.clear();
                sens.setTerms(ts);

                try {
                    t = s.beginTransaction();
                    s.update(sens);
                    t.commit();
                } catch (HibernateException ex) {
                    if (t != null) t.rollback();
                }
                t = null;

                for (int i = 0; i < termNum; i++) {
                    Term term = new Term();
                    term.setUser((User)s.load(User.class, 1));
                    term.setSensors(d.getSensors());
                    //TODO: Implement here
                    term.setTerm("");

                    try {
                        t = s.beginTransaction();
                        s.save(term);
                        t.commit();
                    } catch (HibernateException ex) {
                        if (t != null) t.rollback();
                    }
                    t = null;
                }
            }
            s.close();

            /* センサ値の切り替え（マッチイベントの発生） */
            for (int i = 0; i < count; i++) {
                //TODO: Implement here
                duppd.setTemperature(rand.nextFloat());
            }

            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
            }
            fw = null;
        }
    }

    @Override public void onReceiveCommand(String mediumId) {}

    @Override
    public void onCommandComplete(String mediumId) {
        Long a = timeLogs.poll();
        Long b = Long.valueOf(mediumId);
        try {
            fw.write(a+","+b+"\r\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (timeLogs.isEmpty()) {
            th.notify();
        }
    }
}
