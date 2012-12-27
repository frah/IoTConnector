package iotc;

import iotc.db.Log;
import iotc.db.User;
import iotc.event.CommandEventListener;
import iotc.medium.Medium;
import iotc.parser.CommandExpression;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 受信したメッセージの解釈，コマンドの実行を行う
 * @author atsushi-o
 */
public class CommandOperator implements CommandEventListener {
    private static final ResourceBundle rb;
    private static final Logger LOG;
    static {
        rb = ResourceBundle.getBundle("iotc.i18n.CommandOperator");
        LOG = Logger.getLogger(CommandOperator.class.getName());
    }

    @Override
    public void onReceiveCommand(Medium sender, User user, String command, Log log) {
        LOG.log(Level.INFO, "[{0}] Command received from {1} via {2}: {3}", new Object[]{log.getId(), user.getName(), sender, command});
        CommandExpression ce = new CommandExpression(command);
        if (!ce.exec(sender, log)) {
            sender.Send(log, user, rb.getString("errorMessage"));
        }
    }
}
