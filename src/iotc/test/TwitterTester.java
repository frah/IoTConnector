package iotc.test;

import static iotc.common.StringUtils.isNullOrEmpty;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 実験用ランダムツイート生成クラス
 * User: atsushi-o
 * Date: 13/01/27
 * Time: 4:13
 */
public class TwitterTester implements Runnable {
    private Thread th;
    private Twitter twitter;
    private boolean isRunning = false;
    private final int dummyRange;
    private static final String[] COMS;
    private static final Logger LOG;

    static {
        COMS = new String[]{
                "GetTemperature",
                "GetIlluminance",
                "GetHumidity"
        };
        LOG = Logger.getLogger(TwitterTester.class.getName());
    }

    public TwitterTester(int range) throws TwitterException {
        Configuration conf = configure();
        if (conf == null) {
            throw new TwitterException("Cannot configure twitter4j");
        }
        twitter = new TwitterFactory(conf).getInstance();
        dummyRange = range;
        th = new Thread(this);
    }

    private Configuration configure() {
        Properties p = new Properties();
        try {
            p.load(this.getClass().getResourceAsStream("twitter4j.properties"));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Properties file cannot load", e);
            return null;
        }

        String cKey = p.getProperty("oauth.consumerKey");
        String cSec = p.getProperty("oauth.consumerSecret");
        String aTok = p.getProperty("oauth.accessToken");
        String aSec = p.getProperty("oauth.accessTokenSecret");

        if (isNullOrEmpty(cKey) || isNullOrEmpty(cSec)) {
            // Cannot configure
            LOG.severe("ConsumerKey or ConsumerSecret is empty. Not configurable");
            return null;
        }

        if (isNullOrEmpty(aTok) || isNullOrEmpty(aSec)) {
            // Not authorized
            LOG.info("Twitter account for testing has not authorized yet");
            try {
                Twitter tw = TwitterFactory.getSingleton();
                tw.setOAuthConsumer(cKey, cSec);
                RequestToken req = tw.getOAuthRequestToken();
                Desktop.getDesktop().browse(new URI(req.getAuthorizationURL()));
                String pin = JOptionPane.showInputDialog("Enter the PIN(if available) or just hit enter.");
                if (isNullOrEmpty(pin)) return null;
                AccessToken at = tw.getOAuthAccessToken(req, pin);
                aTok = at.getToken();
                aSec = at.getTokenSecret();
                p.setProperty("oauth.accessToken", aTok);
                p.setProperty("oauth.accessTokenSecret", aSec);
                p.store(new FileOutputStream(new File(this.getClass().getResource("twitter4j.properties").toURI())), null);
            } catch (TwitterException|URISyntaxException|IOException ex) {
                LOG.log(Level.SEVERE, "Twitter authentication failed", ex);
                return null;
            }
        }

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey(cKey)
                .setOAuthConsumerSecret(cSec)
                .setOAuthAccessToken(aTok)
                .setOAuthAccessTokenSecret(aSec);
        return cb.build();
    }

    public synchronized void start() {
        isRunning = true;
        th.start();
        LOG.info("Random tweet thread for testing has started");
    }

    public synchronized void stop() {
        isRunning = false;
        LOG.info("Random tweet thread for testing will be stopped");
    }

    @Override
    public void run() {
        int tweetNum = 0;
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(140);
        Status st = null;

        while (isRunning) {
            sb.append("@tm_sys_ ")
                    .append("[").append(++tweetNum).append("] ")
                    .append("Living::Dummy-")
                    .append(String.format("%03d", rand.nextInt(this.dummyRange)))
                    .append("を")
                    .append(COMS[rand.nextInt(COMS.length)]);

            try {
                st = twitter.updateStatus(sb.toString());
                if (st != null) {
                    LOG.log(Level.INFO, "New status: {0} [{1}]", new Object[]{st.getText(), st.getId()});
                } else {
                    LOG.warning("Some error is occurred on tweeting");
                    tweetNum--;
                }
            } catch (TwitterException e) {
                LOG.log(Level.WARNING, "Tweet failed", e);
                tweetNum--;
            }

            // clean
            sb.setLength(0);
            st = null;

            try {
                Thread.sleep(rand.nextInt(5000));
            } catch (InterruptedException e) {}
        }

        LOG.info("Random tweet thread for testing has stopped");
    }
}
