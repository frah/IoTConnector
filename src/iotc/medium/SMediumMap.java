package iotc.medium;

import iotc.event.CommandEventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 全てのMediumを管理するシングルトンクラス
 * User: atsushi-o
 * Date: 13/01/16
 * Time: 19:28
 */
public class SMediumMap {
    private static Map<String, Medium> mediumInstances;

    static {
        mediumInstances = Collections.synchronizedMap(new HashMap());
    }

    private SMediumMap() {}

    /**
     * クラス名からMediumのインスタンスを得る．
     * なければインスタンスを生成する
     * @param className クラス名
     * @return Mediumのインスタンス
     * @throws IllegalArgumentException クラスが見つからなかった場合，Mediumの実装クラスでなかった場合
     */
    public static Medium get(String className) throws IllegalArgumentException {
        Medium m = mediumInstances.get(className);
        if (m == null) {
            Class clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException("There is no class of such names", ex);
            }

            if (isMedium(clazz)) {
                m = get(clazz);
            } else {
                throw new IllegalArgumentException("This class is not implementation of Medium");
            }
        }
        return m;
    }

    /**
     * クラスオブジェクトからMediumのインスタンスを得る
     * @param clazz クラスオブジェクト
     * @return Mediumのインスタンス
     * @throws IllegalArgumentException インスタンスの生成に失敗した時
     */
    public static Medium get(Class<? extends Medium> clazz) throws IllegalArgumentException {
        Medium m = mediumInstances.get(clazz.getName());
        if (m == null) {
            try {
                m = clazz.newInstance();
            } catch (InstantiationException|IllegalAccessException ex) {
                throw new IllegalArgumentException("Failed to create new instance", ex);
            }
            mediumInstances.put(clazz.getName(), m);
        }
        return m;
    }

    /**
     * 新しいMediumのインスタンスを登録する
     * @param medium 登録するMediumインスタンス
     */
    public static void put(Medium medium) {
        mediumInstances.put(medium.getClass().getName(), medium);
    }

    /**
     * 登録済みの全てのMediumに対してCommandEventListenerを登録する
     * @param l
     */
    public static void addListenerForAll(CommandEventListener l) {
        for (Medium m : mediumInstances.values()) {
            m.addListener(l);
        }
    }

    private static boolean isMedium(Class clazz) {
        for (Class c : clazz.getInterfaces()) {
            if (c.equals(Medium.class)) return true;
        }
        return false;
    }
}
