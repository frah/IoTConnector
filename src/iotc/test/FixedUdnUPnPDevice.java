package iotc.test;

import org.apache.commons.codec.digest.DigestUtils;
import org.itolab.morihit.clinkx.UPnPDevice;

/**
 * UDNがFriendly Nameにより一意に決定するUPnPデバイス
 * @author atsushi-o
 */
public class FixedUdnUPnPDevice extends UPnPDevice {
    private final String udn;

    public FixedUdnUPnPDevice(String friendlyName) {
        super(friendlyName);
        this.udn = DigestUtils.sha1Hex(friendlyName);
    }

    @Override
    public String getUDN() {
        return this.udn;
    }
}
