package iotc;

import iotc.common.UPnPException;
import iotc.db.*;
import iotc.event.UPnPEventListener;
import iotc.medium.SMediumMap;
import org.hibernate.Session;
import org.itolab.morihit.clinkx.UPnPRemoteDevice;
import org.itolab.morihit.clinkx.UPnPRemoteStateVariable;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: atsushi-o
 * Date: 13/01/16
 * Time: 17:04
 */
public class VariableChecker implements UPnPEventListener {
    private HashMap<String, Sensor> sensor;
    private static final Logger LOG;
    private static final Pattern TERM_PATTERN;

    static {
        LOG = Logger.getLogger(VariableChecker.class.getName());
        TERM_PATTERN = Pattern.compile("([-+]?\\d+\\.?[\\d]*) ?([=<>]) ?([-+]?\\d+\\.?[\\d]*)");
    }

    public VariableChecker() {
        sensor = new HashMap();
    }

    @Override
    public void onUpdateValue(UPnPRemoteStateVariable upprsv) {
        UPnPRemoteDevice upprd = upprsv.getRemoteService().getRemoteDevice();
        String key = upprd.getUDN()+upprsv.getName();
        Sensor s;

        s = sensor.get(key);
        if (s == null) {
            try {
                s = EntityMapUtil.upnpToDB(upprsv);
                sensor.put(key, s);
            } catch (UPnPException ex) {
                LOG.log(Level.INFO, "Ignore UPnP value change", ex);
                return;
            }
        }

        Session ses = HibernateUtil.getSessionFactory().openSession();
        ses.beginTransaction();
        s = (Sensor)ses.load(iotc.db.Sensor.class, s.getId());
        for (Term t : (Set<Term>)s.getTerms()) {
            //TODO: 複数の変数に対応できるように要修正
            String ts = t.getTerm();
            LOG.log(Level.FINE, "Check term: {0}", ts);
            ts = ts.replaceAll("([^: <>=\\d][^: <>=]+)(::[^: <>=]+)*", String.valueOf(upprsv.getValue()));
            Matcher m = TERM_PATTERN.matcher(ts);
            if (m.matches()) {
                boolean b = false;
                switch (m.group(2)) {
                    case "=":
                        b = (Double.valueOf(m.group(1)) == Double.valueOf(m.group(3)));
                        break;
                    case "<":
                        b = (Double.valueOf(m.group(1)) < Double.valueOf(m.group(3)));
                        break;
                    case ">":
                        b = (Double.valueOf(m.group(1)) > Double.valueOf(m.group(3)));
                        break;
                }
                LOG.log(Level.FINE, "{0} = {1}", new Object[]{ts, b});
                if (b) {
                    // Execute action
                    Log l = new Log();

                    //FIXME: create resource bundle
                    SMediumMap.get(iotc.medium.Twitter.class).send(null, t.getUser(), ts+"の条件を満たしました");
                }
            }
        }
    }

    @Override public void onDetectNewDevice(UPnPRemoteDevice device) {}
    @Override public void onDetectKnownDevice(Device device) {}
    @Override public void onFailDevice(Device device) {}
}
