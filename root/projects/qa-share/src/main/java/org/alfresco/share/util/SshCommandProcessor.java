package org.alfresco.share.util;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author  Sergey Kardash
 */
public class SshCommandProcessor extends AbstractUtils{

    private final JSch jsch = new JSch();
    private Session session;
    private Channel channel;

    public void connect() {
        try {
            if (isSecureSession) {
                jsch.addIdentity(pathToKeys, "passphrase");
                session = jsch.getSession(serverUser, sshHost, serverShhPort);
            } else {
                session = jsch.getSession(serverUser, sshHost, serverShhPort);
                session.setPassword(serverPass);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setServerAliveInterval(50000);
            session.connect();
        } catch (JSchException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void disconnect() {
        if (session != null)
            session.disconnect();
    }

    public String executeCommand(String command) {
        StringBuilder rv = new StringBuilder();
        try {
            if (session == null || !session.isConnected()) {
                connect();
            }
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    rv.append(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        } catch (Exception ex) {
            rv.append(ex.getMessage());
        }
        return rv.toString();
    }

    public void connect(int timeOut) {
        try {
            if (isSecureSession) {
                jsch.addIdentity(pathToKeys, "passphrase");
                session = jsch.getSession(serverUser, sshHost, serverShhPort);
            } else {
                session = jsch.getSession(serverUser, sshHost, serverShhPort);
                session.setPassword(serverPass);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setServerAliveInterval(timeOut*1000);
            session.connect(timeOut*1000);
        } catch (JSchException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String executeCommand(String command, int timeOut) {
        StringBuilder rv = new StringBuilder();
        try {
            if (session == null || !session.isConnected()) {
                connect(timeOut);
            }

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            //((ChannelExec)channel).setPty(true);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            int k = 0;
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    rv.append(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    break;
                }
                try {
                    k++;
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                if (k > timeOut){
                    System.out.println("Timeout " + timeOut + " seconds is exceeded");
                    break;
                }
            }
        } catch (Exception ex) {
            rv.append(ex.getMessage());
        }
        return rv.toString();
    }
}
