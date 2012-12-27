package iotc.parser;

import iotc.Identification;
import iotc.common.UPnPException;
import iotc.db.*;
import iotc.medium.Medium;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.itolab.morihit.clinkx.UPnPRemoteAction;

import java.util.*;
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
        /** UPnPコマンドを実行する */
        EXEC_COMMAND    (rb.getString("ct.EXEC_COMMAND.regex"), "device", "command") {
            @Override protected void process(Medium medium, Log log, Session session, Object... args) throws Exception {
                if (args.length == 0 || !(args[0] instanceof Identification)) {
                    throw new IllegalArgumentException();
                }

                Identification id = (Identification)args[0];
                Command c = id.getCommand();
                User u = log.getUser();

                if (c.getPower() > u.getPowerForUserId().getPower()) {
                    throw new UPnPException("Low power");
                }

                UPnPRemoteAction uppra = EntityMapUtil.dbToUPnP(c);
                if (!uppra.invoke()) {
                    throw new UPnPException("UPnPRemoteAction invocation failed");
                }
            }
        },
        /** 指定されたデバイスのコマンド一覧を返す */
        GET_COMLIST     (rb.getString("ct.GET_COMLIST.regex"), "device") {
            @Override protected void process(Medium medium, Log log, Session session, Object... args) throws Exception {
                if (args.length == 0 || !(args[0] instanceof Identification)) {
                    throw new IllegalArgumentException();
                }

                Identification id = (Identification)args[0];
                User u = log.getUser();

                Device d = (Device)session.load(Device.class, id.getDevice().getId());
                StringBuilder sb = new StringBuilder();
                for (Command c : (Set<Command>)d.getCommands()) {
                    if (c.getPower() <= u.getPowerForUserId().getPower()) {
                        sb.append(c.getName()).append(", ");
                    }
                }
                if (sb.length() > 0) {
                    sb.setLength(sb.length()-2);
                } else {
                    sb.append(rb.getString("ct.GET_COMLIST.message"));
                }

                medium.Send(log, log.getUser(), sb.toString());
            }
        },
        /** 指定された部屋のデバイス一覧を返す */
        GET_DEVLIST     (rb.getString("ct.GET_DEVLIST.regex"), "room") {
            @Override protected void process(Medium medium, Log log, Session session, Object... args) throws Exception {
                if (args.length == 0 || !(args[0] instanceof Identification)) {
                    throw new IllegalArgumentException();
                }

                Identification id = (Identification)args[0];

                Room r = (Room)session.load(Room.class, id.getRoom().getId());
                StringBuilder sb = new StringBuilder();
                for (Device d : (Set<Device>)r.getDevices()) {
                    sb.append(d.getName()).append(", ");
                }
                sb.setLength(sb.length()-2);

                medium.Send(log, log.getUser(), sb.toString());
            }
        },
        /** 部屋一覧を返す */
        GET_ROOMLIST    (rb.getString("ct.GET_ROOMLIST.regex")) {
            @Override protected void process(Medium medium, Log log, Session session, Object... args) throws Exception {
                List<Room> l = session.getNamedQuery("Room.findAll").list();
                StringBuilder sb = new StringBuilder();
                for (Room r : l) {
                    sb.append(r.getName()).append(", ");
                }
                sb.setLength(sb.length() - 2);

                medium.Send(log, log.getUser(), sb.toString());
            }
        },
        /** センサ一覧を返す */
        GET_SENSLIST    (rb.getString("ct.GET_SENSLIST.regex"), "device") {
            @Override protected void process(Medium medium, Log log, Session session, Object... args) throws Exception {
                if (args.length == 0 || !(args[0] instanceof Identification)) {
                    throw new IllegalArgumentException();
                }

                Identification id = (Identification)args[0];

                Device d = id.getDevice();
                Iterable<Sensor> sensors = null;
                if (d == null && id.getRoom() != null) {
                    Query q = session.getNamedQuery("Sensor.findFromRoom");
                    q.setInteger("roomID", id.getRoom().getId());
                    sensors = q.list();
                } else if (d != null) {
                    d = (Device)session.load(Device.class, d.getId());
                    sensors = d.getSensors();
                } else {
                    throw new IllegalArgumentException("Room or Device id is not found");
                }

                StringBuilder sb = new StringBuilder();
                if (sensors != null) {
                    for (Sensor s : sensors) {
                        sb.append(s.getSensorType().getName()).append(", ");
                    }
                }
                if (sb.length() > 0) {
                    sb.setLength(sb.length()-2);
                } else {
                    sb.append(rb.getString("ct.GET_SENSLIST.message"));
                }

                medium.Send(log, log.getUser(), sb.toString());
            }
        },
        /** 未定義コマンド */
        UNKNOWN         ("") {
            @Override protected void process(Medium medium, Log log, Session session, Object... args) throws Exception {
                medium.Send(log, log.getUser(), rb.getString("ct.UNKNOWN.message"));
            }
        };

        private final Pattern regex;
        private final String[] argNames;
        private CommandType(final String regex, final String... argNames) {
            this.regex = Pattern.compile(regex);
            this.argNames = argNames;
        }

        /**
         * ログレコードをアップデートする
         * @param session
         * @param log
         * @param state
         * @param com
         * @param variable
         */
        private static void updateLog(Session session, Log log, LogState state, Command com, String variable) {
            Transaction t = session.beginTransaction();
            try {
                log.setState(state.getId());
                log.setCommand(com);
                log.setComVariable(variable);

                session.merge(log);

                t.commit();
            } catch (HibernateException ex) {
                LOG.log(Level.SEVERE, "Update log entity failed", ex);
                t.rollback();
            }
        }

        /**
         * ログの状態を変更する
         * @param session
         * @param log
         * @param state
         */
        private static void updateLog(Session session, Log log, LogState state) {
            updateLog(session, log, state, null, null);
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
            LOG.log(Level.FINER, "pattern: {0}, com: {1}", new Object[]{regex.pattern(), com});
            return regex.matcher(com).find();
        }

        /**
         * このコマンドの操作を実行する
         *
         * @param medium
         * @param log
         * @param args
         * @return
         */
        public boolean exec(Medium medium, Log log, Object... args) {
            LogState ls = LogState.ERROR;
            Session s = HibernateUtil.getSessionFactory().openSession();
            Transaction t = s.beginTransaction();
            try {
                Log l = (Log)s.load(Log.class, log.getId());
                process(medium, l, s, args);
                t.commit();
                ls = LogState.COMPLETE;
                return true;
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "Command "+name()+" execution failed", ex);
                t.rollback();
                return false;
            } finally {
                updateLog(s, log, ls);
                s.close();
            }
        }

        /**
         * コマンド処理の中身
         * @param medium
         * @param log
         * @param session
         * @param args
         * @throws Exception
         */
        protected abstract void process(Medium medium, Log log, Session session, Object... args) throws Exception;

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
    private Map<String, String> args;

    /**
     * コマンドのマッチングを行い初期化を行う
     * @param command
     */
    public CommandExpression(String command) {
        this.commandStr = command;
        this.type = CommandType.UNKNOWN;
        for (CommandType ct : CommandType.values()) {
            if (ct.isMatch(command)) {
                this.type = ct;
                break;
            }
        }

        LOG.log(Level.INFO, "Command type: {0}", type.name());
        args = type.getArgs(command);
        for (String n : type.getArgNames()) {
            if (n.matches("room|device|sensor")) {
                id = IdentificationParser.parse(args.get(n));
            } else if (n.matches("command")) {
                id = IdentificationParser.parseCommand(id, args.get(n));
            }
        }

        // Try to parse alias name
        if (type.equals(CommandType.UNKNOWN)) {
            id = IdentificationParser.parse(commandStr);
            if (id.getCommand() != null) {
                type = CommandType.EXEC_COMMAND;
            }
        }
    }

    public String getCommandStr() {
        return commandStr;
    }

    public CommandType getType() {
        return type;
    }

    public boolean exec(Medium medium, Log log) {
        return this.type.exec(medium, log, id);
    }
}
