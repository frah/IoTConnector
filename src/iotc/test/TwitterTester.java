package iotc.test;

import static iotc.common.StringUtils.isNullOrEmpty;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 実験用ランダムツイート生成クラス
 * User: atsushi-o
 * Date: 13/01/27
 * Time: 4:13
 */
public class TwitterTester implements Runnable, ExpEventListener {
    private Thread th;
    private Twitter twitter;
    private FileWriter fw;
    private Map<Long, LogContainer> timeLogs;
    private boolean isRunning = false;
    private final int dummyRange;

    private static final String[] COMS;
    private static final String OUT_PATH;
    private static final Logger LOG;

    static {
        COMS = new String[]{
                "GetTemperature",
                "GetIlluminance",
                "GetHumidity"
        };
        OUT_PATH = "C:\\output.csv";
        LOG = Logger.getLogger(TwitterTester.class.getName());
    }

    /**
     * ダミーデバイスの数を指定して初期化
     * @param range 使用するダミーデバイスの数
     * @throws TwitterException Twitterの設定に失敗した場合
     */
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
                OAuthAuthorization oaa = new OAuthAuthorization(new ConfigurationBuilder().build());
                oaa.setOAuthConsumer(cKey, cSec);
                oaa.setOAuthAccessToken(null);
                RequestToken req = oaa.getOAuthRequestToken();
                Desktop.getDesktop().browse(new URI(req.getAuthorizationURL()));
                System.out.println("Open the following URL and grant access to your account:");
                System.out.println(req.getAuthorizationURL());
                String pin = JOptionPane.showInputDialog("Enter the PIN(if available) or just hit enter.");
                if (isNullOrEmpty(pin)) return null;
                AccessToken at = oaa.getOAuthAccessToken(req, pin);
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

    /**
     * ランダムツイートの生成を開始する
     * @throws IOException ログファイルを開くのに失敗した場合
     */
    public synchronized void start() throws IOException {
        fw = new FileWriter(OUT_PATH, false);
        timeLogs = Collections.synchronizedMap(new HashMap());

        isRunning = true;
        th.start();
        LOG.info("Random tweet thread for testing has started");
    }

    /**
     * ランダムツイートの生成を停止する
     */
    public synchronized void stop() {
        try {
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot write to file", ex);
        }
        LOG.info("Random tweet thread for testing will be stopped");

        isRunning = false;
        th.interrupt();
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
                long t = System.currentTimeMillis();

                st = twitter.updateStatus(sb.toString());
                if (st != null) {
                    LOG.log(Level.INFO, "New status: {0} [{1}]", new Object[]{st.getText(), st.getId()});
                    LogContainer lc = timeLogs.get(st.getId());
                    if (lc != null) {
                        lc.setTweetTime(t);
                    } else {
                        lc = new LogContainer(t);
                    }
                    timeLogs.put(st.getId(), lc);
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
                Thread.sleep(rand.nextInt(30000));
            } catch (InterruptedException e) {}
        }

        LOG.info("Random tweet thread for testing has stopped");
    }

    @Override
    public void onReceiveCommand(String mediumId) {
        long t = System.currentTimeMillis();
        Long key = Long.valueOf(mediumId);
        LogContainer lc = timeLogs.get(key);
        if (lc != null) {
            lc.setFetchTime(t);
        } else {
            lc = new LogContainer();
            lc.setFetchTime(t);
        }
        timeLogs.put(key, lc);
    }

    @Override
    public void onCommandComplete(String mediumId) {
        long t = System.currentTimeMillis();
        Long key = Long.valueOf(mediumId);
        LogContainer lc = timeLogs.get(key);
        if (lc != null) {
            lc.setExecTime(t);
            save(key, lc);
        }
    }

    private void save(Long key, LogContainer lc) {
        try {
            fw.write(lc.toString());
            fw.flush();
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Log output error", e);
        }
        timeLogs.remove(key);
    }

    class LogContainer {
        private long tweetTime;
        private long fetchTime;
        private long execTime;

        public LogContainer(){}
        public LogContainer(long tweetTime) {
            this.tweetTime = tweetTime;
        }

        public void setTweetTime(long tweetTime) {
            this.tweetTime = tweetTime;
        }

        public void setFetchTime(long fetchTime) {
            this.fetchTime = fetchTime;
        }

        public void setExecTime(long execTime) {
            this.execTime = execTime;
        }

        public boolean isOk() {
            return (tweetTime > 0 && fetchTime > 0 && execTime > 0);
        }

        @Override public String toString() {
            return tweetTime + "," + fetchTime + "," + execTime + "\r\n";
        }
    }
}
