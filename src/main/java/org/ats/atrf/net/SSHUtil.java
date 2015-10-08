package org.ats.atrf.net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.CharBuffer;

import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;

public class SSHUtil {
    private String             host;
    private int                port;
    private String             account;
    private String             password;

    private SshClient          client;
    private ClientChannel      channel;
    private ClientSession      session;

    private OutputReader       reader;
    private OutputStreamWriter writer;

    /**
     * 这个程序的线程池大概会在会话后1分钟后关闭
     * 会话大概会在10分钟后自动关闭
     * 
     * @param host
     * @param port
     * @param account
     * @param password
     */
    public SSHUtil(String host, int port, String account, String password) {
        this.host = host;
        this.port = port;
        this.account = account;
        this.password = password;
        
        this.client = SshClient.setUpDefaultClient();
    }

    public String execute(String... commands) {
        StringBuffer commandBuffer = new StringBuffer(commands[0]);
        for (int i = 1, sum = commands.length; i < sum; i++) {
            commandBuffer.append(" ").append(commands[i]);
        }
        String command = commandBuffer.toString();
        connect(ClientChannel.CHANNEL_EXEC, command);
        reader.work();
        shutdown();
        return command;
    }

    public void shell() {
        connect(ClientChannel.CHANNEL_SHELL, null);
        new Thread(reader).start();
    }

    private void connect(String type, String subtype) {
        client.start();
        try {
            session = client.connect(account, host, port).await().getSession();
            session.addPasswordIdentity(password);
            int ret;
            for (ret = ClientSession.WAIT_AUTH; (ret & ClientSession.WAIT_AUTH) != 0;) {
                session.auth();
                ret = session.waitFor(ClientSession.WAIT_AUTH | ClientSession.CLOSED | ClientSession.AUTHED, 0);
            }
            if (((ret & ClientSession.CLOSED) != 0) || ((ret & ClientSession.TIMEOUT) != 0)) {
                System.out.println("closed | timeout");
            } else if ((ret & ClientSession.AUTHED) != 0) {
                channel = session.createChannel(type, subtype);
                {
                    PipedOutputStream pipedOutputStream = new PipedOutputStream();
                    PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
                    channel.setOut(pipedOutputStream);
                    reader = new OutputReader(new InputStreamReader(pipedInputStream));
                }
                if (subtype == null) {
                    PipedInputStream pipedInputStream = new PipedInputStream();
                    PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
                    channel.setIn(pipedInputStream);
                    writer = new OutputStreamWriter(pipedOutputStream);
                }
                channel.open();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String command(String... commands) {
        StringBuffer commandBuffer = new StringBuffer(commands[0]);
        for (int i = 1, sum = commands.length; i < sum; i++) {
            commandBuffer.append(" ").append(commands[i]);
        }
        String command = commandBuffer.toString();
        try {
            writer.write(command);
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return command;
    }

    public void shutdown() {
        shutdown(0L);
    }

    public void shutdown(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.close(false);
        session.close(false);
        client.stop();
        reader.shutdown();
        if (null != writer) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public OutputReader getOutput() {
        return reader;
    }

    public static class OutputReader implements Runnable {

        private InputStreamReader reader;
        private StringBuffer      buffer;
        private boolean           living;

        private OutputReader(InputStreamReader reader) {
            this.reader = reader;
            this.buffer = new StringBuffer();
        }

        public void work() {
            buffer = new StringBuffer();
            CharBuffer kilobuffer = CharBuffer.allocate(1024);
            try {
                for (int size = reader.read(kilobuffer); size > 0;) {
                    kilobuffer.flip();
                    buffer.append(kilobuffer);
                    size = reader.read(kilobuffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void finalwork() {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public final void shutdown() {
            living = false;
        }

        @Override
        public void run() {
            long tick = 100L;
            living = true;

            for (; living;) {
                work();
                try {
                    Thread.sleep(tick);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            finalwork();
        }

        @Override
        public String toString() {
            return buffer.toString();
        }

    }

}