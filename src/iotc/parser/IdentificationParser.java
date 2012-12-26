package iotc.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import iotc.Identification;
import iotc.db.*;
import org.hibernate.*;
import java.util.Set;

/**
 * デバイス，センサ識別子パーサ
 * @author atsushi-o
 */
public final class IdentificationParser {
    private static final Pattern IDPATTERN = Pattern.compile("([^:]+)(::[^:]+)*");
    public static Identification parse(String idstring) {
        Identification id = new Identification();
        Matcher m = IDPATTERN.matcher(idstring);
        Session s = HibernateUtil.getSessionFactory().openSession();
        Room r = null;
        Device d = null;

        if (!m.matches()) return id;

        for (int i = 1; i <= m.groupCount(); i++) {
            String str = m.group(i);
            switch(i) {
                case 1: {
                    Query q = s.getNamedQuery("Room.findFromName");
                    q.setString("name", str);
                    Object o = q.uniqueResult();
                    if (o instanceof Room) {
                        r = (Room)o;
                        id.setRoom(r);
                    } else {
                        q = s.getNamedQuery("Device.findFromAlias");
                        q.setString("alias", str);
                        o = q.uniqueResult();
                        if (o instanceof Device) {
                            id.setRoom(((Device)o).getRoom());
                            id.setDevice((Device)o);
                        } else {
                            q = s.getNamedQuery("Command.findFromAlias");
                            q.setString("alias", str);
                            o = q.uniqueResult();
                            if (o instanceof Command) {
                                d = ((Command)o).getDevice();
                                id.setRoom(d.getRoom());
                                id.setDevice(d);
                                id.setCommand((Command)o);
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    if (r == null) break;
                    for (Device dev : (Set<Device>)r.getDevices()) {
                        if (dev.getName().equals(str.substring(2))) {
                            d = dev;
                            id.setDevice(dev);
                        }
                    }
                    if (id.getDevice() == null) {
                        Query q = s.getNamedQuery("Sensor.findFromRoomAndType");
                        q.setInteger("roomID", r.getId());
                        q.setString("type", str.substring(2));
                        List<Sensor> ss = q.list();
                        if (ss.size() > 0) {
                            id.setSensors(ss);
                        }
                    }
                    break;
                }
                case 3: {
                    if (d == null) break;
                    for (Sensor sens : (Set<Sensor>)d.getSensors()) {
                        if (sens.getName().equals(str.substring(2))
                                || sens.getSensorType().getName().equals(str.substring(2))) {
                            List<Sensor> l = new ArrayList();
                            l.add(sens);
                            id.setSensors(l);
                            break;
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
        s.close();

        return id;
    }
    /* インスタンス化抑制 */
    private IdentificationParser() {}

    public static void main(String[] arg) {
        System.out.println(IdentificationParser.parse("リビングON"));
    }
}
