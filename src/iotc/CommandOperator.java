package iotc;

import iotc.db.Log;
import iotc.db.User;
import iotc.event.CommandEventListener;
import iotc.medium.Medium;

/**
 *
 * @author atsushi-o
 */
public class CommandOperator implements CommandEventListener {
    @Override
    public void onReceiveCommand(Medium sender, User user, String command, Log log) {
        System.out.println("From: " + sender);
        System.out.println("User: " + user);
        System.out.println("Comm: " + command);
        System.out.println("Log : " + log);
    }
}
