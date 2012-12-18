package iotc.parser;

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
    private static final Pattern IDPATTERN = Pattern.compile("([^:]+)(::[^:]+).");
    public static Identification parse(String idstring) {
        Identification id = new Identification();
        Matcher m = IDPATTERN.matcher(idstring);
        Session s = HibernateUtil.getSessionFactory().openSession();
        Room r = null;
        Device d = null;

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
                    break;
                }
                case 3: {
                    if (d == null) break;

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
}
