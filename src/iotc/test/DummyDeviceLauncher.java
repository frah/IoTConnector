package iotc.test;

import iotc.db.*;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;

/**
 * 実験用ダミーデバイス生成クラス
 * User: atsushi-o
 * Date: 13/01/26
 * Time: 23:51
 */
public class DummyDeviceLauncher {
    private ArrayList<DummyUPnPDevice> dummies;

    /**
     * ダミーデバイスの数を指定して初期化する
     * @param deviceNum ダミーデバイスの数
     */
    public DummyDeviceLauncher(int deviceNum) {
        dummies = new ArrayList<>(deviceNum);
        init(deviceNum);
    }

    private void init(int num) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        Room r = (Room)s.load(Room.class, 1);

        for (int i = 0; i < num; i++) {
            String fName = String.format("Dummy-%03d", i);
            DummyUPnPDevice duppd = new DummyUPnPDevice(fName, true);
            String udn = duppd.getUDN();

            Query q = s.getNamedQuery("Device.findFromUDN");
            q.setString("udn", udn);
            Object o = q.uniqueResult();
            if (o == null || !(o instanceof Device)) {
                // 登録がない場合はデバイスを自動登録
                Device d = new Device();
                d.setName(fName);
                d.setUdn(udn);
                d.setType(DeviceType.OtherUPnP.getId());
                d.setRoom(r);

                Transaction t = null;
                try {
                    t = s.beginTransaction();
                    s.save(d);
                    t.commit();
                    s.flush();
                } catch (HibernateException ex) {
                    if (t != null) t.rollback();
                }
                t = null;

                // Get系コマンドを自動登録
                for (String getVar : new String[]{"Temperature", "Illuminance", "Humidity"}) {
                    String comName = "Get"+getVar;
                    Command c = new Command();
                    c.setDevice(d);
                    c.setName(comName);
                    c.setCommand(comName);
                    c.setPower(PowerEnum.ANONYMOUS.getId());
                    c.setType(CommandType.UPnPAction.getId());

                    try {
                        t = s.beginTransaction();
                        s.save(c);
                        t.commit();
                    } catch (HibernateException ex) {
                        if (t != null) t.rollback();
                    }
                    t = null;
                }

                // Temperatureセンサを自動登録
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
            dummies.add(duppd);
        }
        s.close();
    }

    /**
     * ダミーデバイスを全て開始する
     */
    public void start() {
        for (DummyUPnPDevice duppd : dummies) {
            duppd.start();
        }
    }

    /**
     * ダミーデバイスを全て停止する
     */
    public void stop() {
        for (DummyUPnPDevice duppd : dummies) {
            duppd.stop();
        }
    }
}
