package org.ats.atrf.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Processor {
    private InputStream  stdInputStream;
    private OutputStream outputStream;
    private List<Object> sourceBuffer;
    private List<String> commandList;
    private boolean      processing;
    private Charset      charset;

    public Processor(String... commands) {
        this.sourceBuffer = new ArrayList<Object>();
        this.commandList = new ArrayList<String>();
        this.processing = false;
        this.charset = Charset.forName("UTF-8");
        // this.charset = Charset.forName("GBK");

        Collections.addAll(commandList, commands);
    }

    public Processor attachCommand(String... commands) {
        this.commandList.addAll(Arrays.asList(commands));
        return this;
    }

    public Processor writeSource(Object... sources) {
        if (processing) {
            OutputStreamWriter writer;
            try {
                writer = new OutputStreamWriter(outputStream, "UTF-8");
                for (Object s : sourceBuffer) {
                    System.out.println("writing:" + s);
                    writer.write(s.toString());
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Collections.addAll(sourceBuffer, sources);
        }
        return this;
    }

    protected void handleOutput(String content) {
        System.out.println("STDINPUT :" + content);
    }

    public String process() {
        System.out.println("cmds : " + commandList.toString());
        try {
            ProcessBuilder builder = new ProcessBuilder(commandList);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            outputStream = process.getOutputStream();
            stdInputStream = process.getInputStream();

            processing = true;
            Thread inputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(stdInputStream, charset));
                    while (processing) {
                        try {
                            String line;
                            if ((line = inputReader.readLine()) != null) {
                                handleOutput(line.toString());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            inputThread.start();

            while (!processing) {// wait process to start
            }
            writeSource(sourceBuffer.toArray());

            int result = process.waitFor();
            System.out.println("process result : " + result);
            processing = false;
            return Integer.toString(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
