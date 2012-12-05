package iotc.medium;

import twitter4j.*;

/**
 * Twitterメディア
 * @author atsushi-o
 */
public class Twitter extends AbstractMedium implements UserStreamListener {
    private twitter4j.Twitter t;
    private TwitterStream ts;

    public Twitter() {
        t = new TwitterFactory().getInstance();
        ts = new TwitterStreamFactory().getInstance();
        ts.addListener(this);
        ts.user(new String[]{"tm_sys_"});
    }

    public void stop() {
        ts.cleanUp();
    }

    /* Implementation of AbstractMedium */
    @Override
    public boolean Send(int logId, int userId, String message) {
        //TODO: implement this
        try {
            t.updateStatus(message);
        } catch (TwitterException ex) {
            return false;
        }
        return true;
    }

    /* Implementation of UserStreamListener */
    @Override
    public void onStatus(Status status) {
        if (!status.getText().startsWith("@tm_sys_")) return;

        //TODO: implement this
        super.fireReceiveEvent(0, status.getText(), -1);
    }

    @Override
    public void onFollow(User user, User user1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onDirectMessage(DirectMessage dm) {
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
    public void onFavorite(User user, User user1, Status status) {}
    @Override
    public void onUnfavorite(User user, User user1, Status status) {}
    @Override
    public void onUserListMemberAddition(User user, User user1, UserList ul) {}
    @Override
    public void onUserListMemberDeletion(User user, User user1, UserList ul) {}
    @Override
    public void onUserListSubscription(User user, User user1, UserList ul) {}
    @Override
    public void onUserListUnsubscription(User user, User user1, UserList ul) {}
    @Override
    public void onUserListCreation(User user, UserList ul) {}
    @Override
    public void onUserListUpdate(User user, UserList ul) {}
    @Override
    public void onUserListDeletion(User user, UserList ul) {}
    @Override
    public void onUserProfileUpdate(User user) {}
    @Override
    public void onBlock(User user, User user1) {}
    @Override
    public void onUnblock(User user, User user1) {}
    @Override
    public void onDeletionNotice(StatusDeletionNotice sdn) {}
    @Override
    public void onTrackLimitationNotice(int i) {}
    @Override
    public void onScrubGeo(long l, long l1) {}
    @Override
    public void onStallWarning(StallWarning sw) {}
}
