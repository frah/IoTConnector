package iotc.medium;

import iotc.db.HibernateUtil;
import iotc.db.Log;
import iotc.db.LogState;
import iotc.db.Power;
import iotc.db.PowerType;
import iotc.db.User;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections15.map.LRUMap;
import org.hibernate.*;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamListener;

/**
 * Twitterメディア
 * @author atsushi-o
 */
public class Twitter extends AbstractMedium implements UserStreamListener {
    private twitter4j.Twitter t;
    private TwitterStream ts;
    private LRUMap<Long, Log> relations;

    private static final String ACCOUNT;
    private static final Logger LOG;
    static {
        ACCOUNT = "@tm_sys_";
        LOG = Logger.getLogger(Twitter.class.getName());
    }

    public Twitter() {
        t = new TwitterFactory().getInstance();
        ts = new TwitterStreamFactory().getInstance();
        ts.addListener(this);
        ts.user(new String[]{ACCOUNT});
        relations = (LRUMap<Long,Log>)Collections.synchronizedMap(new LRUMap(50));
    }

    public void stop() {
        ts.cleanUp();
    }

    /* Implementation of AbstractMedium */
    @Override
    public boolean Send(Log log, User user, String message) {
        StringBuilder sb = new StringBuilder(message);
        if (user != null) {
            String scname = user.getSpecificAliasName(this.getClass().getName());
            sb.insert(0, "@" + scname + " ");
        }

        // Check message length
        if (sb.length() > 140) {
            LOG.log(Level.WARNING, "Too long message error: {0}", sb.toString());
            sb.replace(139, sb.length(), "…");
        }

        StatusUpdate st = new StatusUpdate(sb.toString());
        if (log != null) {
            Long twId = Long.valueOf(log.getMediumId());
            if (twId != null) {
                st.setInReplyToStatusId(twId);
            }
        }
        try {
            t.updateStatus(st);
            LOG.log(Level.INFO, "Twitter updated: {0}", st);
        } catch (TwitterException ex) {
            LOG.log(Level.WARNING, "Twitter send error", ex);
            return false;
        }
        return true;
    }

    /* Implementation of UserStreamListener */
    @Override
    public void onStatus(Status status) {
        String mes = status.getText();
        if (!mes.startsWith(ACCOUNT)) return;

        mes = mes.substring(mes.indexOf(ACCOUNT)+1);

        /* Search user from DB
            and if user is not saved yet, create new user entity */
        User u = null;
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.getNamedQuery("User.findFromAlias");
        q.setString("alias", status.getUser().getScreenName());
        List<User> candidate = (List<User>)q.list();
        if (candidate != null && candidate.size() > 0) {
            for (User cu : candidate) {
                if (cu.getSpecificAliasName(Twitter.class.getName()).equals(status.getUser().getScreenName())) {
                    u = cu;
                    break;
                }
            }
        }
        if (u == null) {
            /* create new user entity */
            u = new User();
            u.setName(status.getUser().getScreenName());
            u.setAliasName(this.getClass().getName()+":"+status.getUser().getScreenName());
            Power p = new Power();
            p.setPower(0);
            p.setType(PowerType.BASIC.getId());

            try {
                s.beginTransaction();
                Serializable id = s.save(u);
                u = (User)s.load(User.class, id);
                p.setUserByUserId(u);
                s.save(p);
                s.getTransaction().commit();
            } catch (HibernateException ex) {
                s.getTransaction().rollback();
                LOG.log(Level.SEVERE, "Saving new user is failed", ex);
                s.close();
                return;
            }
        }

        /* Create new log */
        Log l = new Log();
        l.setUser(u);
        l.setMediumId(String.valueOf(status.getId()));
        l.setState(LogState.RECEIVED.getId());
        /* Check relations */
        if (status.getInReplyToStatusId() != -1) {
            Log rl = relations.get(status.getInReplyToStatusId());
            if (rl == null) {
                Query qq = s.getNamedQuery("Log.findFromMediumId");
                qq.setString("mediumId", String.valueOf(status.getInReplyToStatusId()));
                List ll = qq.list();
                if (ll != null && ll.size() > 0) {
                    rl = (Log)ll.get(0);
                }
            }

            if (rl == null) {
                LOG.log(Level.WARNING, "This status contains InReplyToStatusId, "
                        + "but system cannot find log which corresponds with it.: {0}", status.getInReplyToStatusId());
            }
            l.setLog(rl);
        }

        try {
            s.beginTransaction();
            s.save(l);
            s.getTransaction().commit();

            // synchronized
            relations.put(status.getId(), l);
            LOG.log(Level.INFO, "Receive new message: {0} from {1} reference to {2}", new Object[]{mes, u.getName(), l});
            super.fireReceiveEvent(u, mes, l);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Any error occured. Event not fired.", ex);
            s.getTransaction().rollback();
        }

        s.close();
    }

    @Override
    public void onFollow(twitter4j.User user, twitter4j.User user1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onDirectMessage(twitter4j.DirectMessage dm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onException(Exception excptn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onDeletionNotice(long l, long l1) {}
    @Override
    public void onFriendList(long[] longs) {}
    @Override
    public void onFavorite(twitter4j.User user, twitter4j.User user1, Status status) {}
    @Override
    public void onUnfavorite(twitter4j.User user, twitter4j.User user1, Status status) {}
    @Override
    public void onUserListMemberAddition(twitter4j.User user, twitter4j.User user1, twitter4j.UserList ul) {}
    @Override
    public void onUserListMemberDeletion(twitter4j.User user, twitter4j.User user1, twitter4j.UserList ul) {}
    @Override
    public void onUserListSubscription(twitter4j.User user, twitter4j.User user1, twitter4j.UserList ul) {}
    @Override
    public void onUserListUnsubscription(twitter4j.User user, twitter4j.User user1, twitter4j.UserList ul) {}
    @Override
    public void onUserListCreation(twitter4j.User user, twitter4j.UserList ul) {}
    @Override
    public void onUserListUpdate(twitter4j.User user, twitter4j.UserList ul) {}
    @Override
    public void onUserListDeletion(twitter4j.User user, twitter4j.UserList ul) {}
    @Override
    public void onUserProfileUpdate(twitter4j.User user) {}
    @Override
    public void onBlock(twitter4j.User user, twitter4j.User user1) {}
    @Override
    public void onUnblock(twitter4j.User user, twitter4j.User user1) {}
    @Override
    public void onDeletionNotice(twitter4j.StatusDeletionNotice sdn) {}
    @Override
    public void onTrackLimitationNotice(int i) {}
    @Override
    public void onScrubGeo(long l, long l1) {}
    @Override
    public void onStallWarning(twitter4j.StallWarning sw) {}
}
