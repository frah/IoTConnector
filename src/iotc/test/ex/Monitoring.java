package iotc.test.ex;

import twitter4j.*;

import javax.swing.*;

public class Monitoring extends UserStreamAdapter {
    private Twitter t;
    private TwitterStream ts;
    public Monitoring() {
        t = new TwitterFactory().getInstance();
        ts = new TwitterStreamFactory().getInstance();

        String temp = JOptionPane.showInputDialog("Please input max temperature.");
        if (temp != null || !temp.isEmpty()) {
            try {
                t.updateStatus("@tm_sys_ Living::SunSPOT1::Temperature > "+temp+"のとき通知");
            } catch (TwitterException e) {}

            ts.user();
        }
    }

    @Override
    public void onStatus(Status status) {
        if (status.getUser().getScreenName().equals("tm_sys_")) {
            if (status.getText().contains("条件を満たしました")) {
                JOptionPane.showMessageDialog(null, "異常な温度が検出されました！", "Fire alarm", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        Monitoring m = new Monitoring();
    }
}
