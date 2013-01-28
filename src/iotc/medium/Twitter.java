package iotc.medium;

import iotc.db.*;
import iotc.db.User;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections15.map.LRUMap;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import twitter4j.*;

/**
 * Twitterメディア
 * @author atsushi-o
 */
public class Twitter extends AbstractMedium implements UserStreamListener {
    private twitter4j.Twitter t;
    private TwitterStream ts;
    private LRUMap<Long, Log> relations;
    private boolean isSilent = false;

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
        ts.user(new String[]{ACCOUNT.substring(1)});
        relations = new LRUMap(50);
        LOG.info("Twitter stream listening start.");
    }

    public Twitter(boolean isSilent) {
        this();
        this.isSilent = isSilent;
    }

    public void stop() {
        ts.cleanUp();
        LOG.info("Twitter stream listening stop.");
    }

    /* Implementation of Medium */
    @Override
    protected String _send(String message, String user, String replyId) {
        StringBuilder sb = new StringBuilder(message);
        if (user != null) {
            sb.insert(0, "@" + user + " ");
        }

        // Check message length
        if (sb.length() > 140) {
            LOG.log(Level.WARNING, "Too long message error: {0}", sb.toString());
            sb.replace(139, sb.length(), "…");
        }

        StatusUpdate st = new StatusUpdate(sb.toString());
        if (replyId != null) {
            Long twId = Long.valueOf(replyId);
            if (twId != null) {
                st.setInReplyToStatusId(twId);
            }
        }
        Status s = null;
        try {
            if (!isSilent) {
                s = t.updateStatus(st);
            }
            LOG.log(Level.INFO, "Twitter updated: {0}{1}", new Object[]{st, isSilent?" (silent)":""});

        } catch (TwitterException ex) {
            if (ex.getErrorCode() != 187) {
                LOG.log(Level.WARNING, "Twitter send error", ex);
                return null;
            }

            LOG.log(Level.FINER, ex.getErrorMessage());
            String ctm = String.valueOf(System.currentTimeMillis());
            return _send(message.concat(" ("+ctm.substring(ctm.length()/2)+")"), user, replyId);
        }

        if (isSilent) return "0000000000000";
        else return s!=null?Long.toString(s.getId()):null;
    }

    /* Implementation of UserStreamListener */
    @Override
    public void onStatus(Status status) {
        String mes = status.getText();
        if (!mes.startsWith(ACCOUNT)) return;

        mes = mes.substring(mes.indexOf(ACCOUNT)+ACCOUNT.length()+1);

        /* Search user from DB
            and if user is not saved yet, create new user entity */
        User u = null;
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.getNamedQuery("User.findFromAlias");
        q.setString("alias", "%"+status.getUser().getScreenName()+"%");
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
            p.setPower(PowerEnum.ANONYMOUS.getId());
            p.setType(PowerType.BASIC.getId());

            Transaction t = s.beginTransaction();
            try {
                Serializable id = s.save(u);
                u = (User)s.load(User.class, id);

                p.setUserId(u.getId());
                s.save(p);
                t.commit();
            } catch (HibernateException ex) {
                t.rollback();
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

            synchronized (this) {
                relations.put(status.getId(), l);
                LOG.log(Level.INFO, "Receive new message: {0} from {1} reference to {2}", new Object[]{mes, u.getName(), l});
                super.fireReceiveEvent(u, mes, l);
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Any error occured. Event not fired.", ex);
            s.getTransaction().rollback();
        }

        s.close();
    }

    @Override
    public void onFollow(twitter4j.User user, twitter4j.User user1) {
    }

    @Override
    public void onDirectMessage(twitter4j.DirectMessage dm) {
    }

    @Override
    public void onException(Exception excptn) {
    }

    @Override public void onDeletionNotice(long l, long l1) {}
    @Override public void onFriendList(long[] longs) {}
    @Override public void onFavorite(twitter4j.User user, twitter4j.User user1, Status status) {}
    @Override public void onUnfavorite(twitter4j.User user, twitter4j.User user1, Status status) {}
    @Override public void onUserListMemberAddition(twitter4j.User user, twitter4j.User user1, twitter4j.UserList ul) {}
    @Override public void onUserListMemberDeletion(twitter4j.User user, twitter4j.User user1, twitter4j.UserList ul) {}
    @Override public void onUserListSubscription(twitter4j.User user, twitter4j.User user1, twitter4j.UserList ul) {}
    @Override public void onUserListUnsubscription(twitter4j.User user, twitter4j.User user1, twitter4j.UserList ul) {}
    @Override public void onUserListCreation(twitter4j.User user, twitter4j.UserList ul) {}
    @Override public void onUserListUpdate(twitter4j.User user, twitter4j.UserList ul) {}
    @Override public void onUserListDeletion(twitter4j.User user, twitter4j.UserList ul) {}
    @Override public void onUserProfileUpdate(twitter4j.User user) {}
    @Override public void onBlock(twitter4j.User user, twitter4j.User user1) {}
    @Override public void onUnblock(twitter4j.User user, twitter4j.User user1) {}
    @Override public void onDeletionNotice(twitter4j.StatusDeletionNotice sdn) {}
    @Override public void onTrackLimitationNotice(int i) {}
    @Override public void onScrubGeo(long l, long l1) {}
    @Override public void onStallWarning(twitter4j.StallWarning sw) {}
}
