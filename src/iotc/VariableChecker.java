package iotc;

import iotc.common.UPnPException;
import iotc.db.Device;
import iotc.db.EntityMapUtil;
import iotc.db.Sensor;
import iotc.event.UPnPEventListener;
import org.itolab.morihit.clinkx.UPnPRemoteDevice;
import org.itolab.morihit.clinkx.UPnPRemoteStateVariable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: atsushi-o
 * Date: 13/01/16
 * Time: 17:04
 */
public class VariableChecker implements UPnPEventListener {
    private static final Logger LOG;

    static {
        LOG = Logger.getLogger(VariableChecker.class.getName());
    }
    @Override
    public void onUpdateValue(UPnPRemoteStateVariable upprsv) {
        Sensor s;
        try {
            s = EntityMapUtil.upnpToDB(upprsv);
        } catch (UPnPException ex) {
            LOG.log(Level.INFO, "Ignore UPnP value change", ex);
            return;
        }
    }

    @Override public void onDetectNewDevice(UPnPRemoteDevice device) {}
    @Override public void onDetectKnownDevice(Device device) {}
    @Override public void onFailDevice(Device device) {}
}
