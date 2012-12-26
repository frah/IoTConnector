package iotc.parser;

import iotc.Identification;
import iotc.db.HibernateUtil;
import iotc.db.Log;
import iotc.db.Room;
import iotc.medium.Medium;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * コマンド構文解析，実行クラス
 * Created with IntelliJ IDEA.
 * User: atsushi-o
 * Date: 12/12/26
 * Time: 10:44
 * To change this template use File | Settings | File Templates.
 */
public class CommandExpression {
    private static final ResourceBundle rb;
    private static final Logger LOG;
    static {
        rb = ResourceBundle.getBundle("iotc.i18n.CommandExpression");
        LOG = Logger.getLogger(CommandExpression.class.getName());
    }

    /**
     * コマンドの定義
     */
    static enum CommandType {
        EXEC_COMMAND    (rb.getString("ct.EXEC_COMMAND.regex"), "device", "command") {
            @Override public boolean exec(Identification id, Medium medium, Log log) {
                return false;
            }
        },
        GET_COMLIST     (rb.getString("ct.GET_COMLIST.regex"), "device") {
            @Override public boolean exec(Identification id, Medium medium, Log log) {
                return false;
            }
        },
        GET_DEVLIST     (rb.getString("ct.GET_DEVLIST.regex"), "room") {
            @Override public boolean exec(Identification id, Medium medium, Log log) {
                return false;
            }
        },
        GET_ROOMLIST    (rb.getString("ct.GET_ROOMLIST.regex")) {
            @Override public boolean exec(Identification id, Medium medium, Log log) {
                medium.Send(log, log.getUser(), getRoomList());
                return true;
            }
        },
        UNKNOWN         ("") {
            @Override public boolean exec(Identification id, Medium medium, Log log) {
                medium.Send(log, log.getUser(), rb.getString("ct.UNKNOWN.message"));
                return false;
            }
        };

        private static String getRoomList() {
            Session s = HibernateUtil.getSessionFactory().openSession();
            List<Room> l = s.getNamedQuery("Room.findAll").list();
            StringBuilder sb = new StringBuilder();
            for (Room r : l) {
                sb.append(r.getName()).append(", ");
            }
            sb.setLength(sb.length()-2);
            s.close();
            return sb.toString();
        }

        private final Pattern regex;
        private final String[] argNames;
        private CommandType(final String regex, final String... argNames) {
            this.regex = Pattern.compile(regex);
            this.argNames = argNames;
        }

        /**
         * このコマンドの書式パターンを返す
         * @return
         */
        public Pattern getPattern() {
            return regex;
        }

        /**
         * このコマンドの引数名の配列を返す
         * @return
         */
        public String[] getArgNames() {
            return argNames;
        }

        /**
         * 与えられたコマンド文から引数のMapを生成する
         * @param com
         * @return
         */
        public Map<String, String> getArgs(String com) {
            Matcher m = regex.matcher(com);
            if (!m.find()) return null;

            Map<String, String> ret = new HashMap();
            for (String g : argNames) {
                ret.put(g, m.group(g));
            }
            return ret;
        }

        /**
         * 与えられた文字列がこのコマンドの書式にマッチするかチェックする
         * @param com
         * @return
         */
        public boolean isMatch(String com) {
            LOG.log(Level.INFO, "pattern: {0}, com: {1}", new Object[]{regex.pattern(), com});
            return regex.matcher(com).find();
        }

        /**
         * このコマンドの操作を実行する
         * @param args
         * @return
         */
        public abstract boolean exec(Identification id, Medium medium, Log log);

        /**
         * 与えられた文字列にマッチするコマンドを返す．なければnull
         * @param com
         * @return
         */
        public static CommandType searchCommand(String com) {
            for (CommandType ct : values()) {
                if (ct.isMatch(com)) return ct;
            }
            return null;
        }
    }

    private final String commandStr;
    private CommandType type;
    private Identification id;

    /**
     * コマンドのマッチングを行い初期化を行う
     * @param command
     */
    public CommandExpression(String command) {
        this.commandStr = command;
        for (CommandType ct : CommandType.values()) {
            if (ct.isMatch(command)) {
                this.type = ct;
                break;
            }
        }
        if (this.getType() == null) this.type = CommandType.UNKNOWN;

        LOG.log(Level.INFO, "Command type: {0}", type.name());
        for (String n : type.getArgNames()) {
        }
    }

    public String getCommandStr() {
        return commandStr;
    }

    public CommandType getType() {
        return type;
    }

    public boolean exec(Medium medium, Log log) {
        return this.type.exec(id, medium, log);
    }
}
