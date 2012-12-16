package iotc.db;

import iotc.UPnPDevices;
import iotc.UPnPException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.itolab.morihit.clinkx.UPnPRemoteAction;
import org.itolab.morihit.clinkx.UPnPRemoteActionArgument;
import org.itolab.morihit.clinkx.UPnPRemoteDevice;
import org.itolab.morihit.clinkx.UPnPRemoteStateVariable;

/**
 * UPnPデバイスとDBエンティティを結びつける補助クラス
 * @author atsushi-o
 */
public class EntityMapUtil {
    private EntityMapUtil() {}

    /**
     * デバイスエンティティから実UPnPデバイスのインスタンスを取得する
     * @param d デバイスエンティティ
     * @return 実UPnPデバイスインスタンス
     * @throws UPnPException UPnPデバイスが有効でない場合
     */
    public static UPnPRemoteDevice dbToUPnP(Device d) throws UPnPException {
        UPnPRemoteDevice rd = UPnPDevices.getInstance().getAvailableUPnPDevice(d.getUdn());
        if (rd == null) {
            throw new UPnPException("This device is not available");
        }
        return rd;
    }

    /**
     * センサエンティティから実UPnPデバイスのVariableインスタンスを取得する
     * @param s センサインスタンス
     * @return 対応する実UPnPデバイスのVariableインスタンス
     * @throws UPnPException UPnPデバイスが有効でない，またはセンサが見つからなかった場合
     */
    public static UPnPRemoteStateVariable dbToUPnP(Sensor s) throws UPnPException {
        Device d = s.getDevice();
        UPnPRemoteDevice rd = UPnPDevices.getInstance().getAvailableUPnPDevice(d.getUdn());
        if (rd == null) {
            throw new UPnPException("This sensor is not available");
        }
        UPnPRemoteStateVariable var = rd.getRemoteStateVariable(s.getName());
        if (var == null) {
            throw new UPnPException("No such named sensor");
        }

        return var;
    }

    /**
     * コマンドエンティティから実UPnPデバイスのActionインスタンスを取得する
     * @param c コマンドエンティティ
     * @return 対応する，引数設定済みの実UPnPデバイスのActionインスタンス
     * @throws UPnPException 対応するUPnPデバイスが有効でない，又は引数が不正である場合
     */
    public static UPnPRemoteAction dbToUPnP(Command c) throws UPnPException {
        Device d = c.getDevice();
        if (c.getType() == CommandType.SunSPOT.getId()) {
            for (Device dev : (java.util.Set<Device>)d.getRoom().getDevices()) {
                if (dev.getType() == DeviceType.SunSPOT.getId()) {
                    d = dev;
                    break;
                }
            }
            if (d.getType() != DeviceType.SunSPOT.getId()) {
                throw new UPnPException("SunSPOT device not found");
            }
        }

        UPnPRemoteDevice rd = dbToUPnP(d);

        String[] coms = c.getCommand().split("::");
        UPnPRemoteAction a = rd.getRemoteAction(coms[0]);
        Pattern p = Pattern.compile("(.*?)\\((.*?)\\)");
        for (int i = 1; i < coms.length; i++) {
            Matcher m = p.matcher(coms[i]);
            String argName = m.group(1);
            String argValue = m.group(2);
            for (UPnPRemoteActionArgument rarg : a.getRemoteActionInputArgumentList()) {
                if (rarg.getName().equals(argName)) {
                    Object obj;
                    try {
                        obj = rarg.getDataType().getJavaDataType().getConstructor(String.class).newInstance(argValue);
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new UPnPException("Illegal argument: " + argValue);
                    }
                    rarg.setValue(obj);
                }
            }
        }

        return a;
    }
}
