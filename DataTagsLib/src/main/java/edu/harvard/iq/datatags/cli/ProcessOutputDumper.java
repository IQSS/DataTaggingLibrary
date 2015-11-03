package edu.harvard.iq.datatags.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dumps an output stream into another one, or possibly to a file.
 * Instances run in their own thread, using {@link #start}. Client code can wait for the input stream to drain 
 * into the output one by calling {@link #await()}.
 * @author michael
 */
public class ProcessOutputDumper {
    
    final OutputStream out;
    final InputStream in;
    final CountDownLatch latch = new CountDownLatch(1);

    ProcessOutputDumper(InputStream anIn, Path outPath) throws IOException {
        in = anIn;
        out = Files.newOutputStream(outPath);
    }

    ProcessOutputDumper(InputStream anIn, OutputStream anOut) throws IOException {
        in = anIn;
        out = anOut;
    }

    public void await() throws InterruptedException {
        latch.await();
    }

    public void start() {
        new Thread(() -> {
            byte[] arr = new byte[1024];
            int lastRes = 0;
            while (lastRes != -1) {
                try {
                    lastRes = in.read(arr);
                    if (lastRes > 0) {
                        out.write(arr, 0, lastRes);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(VisualizeDecisionGraphCommand.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            latch.countDown();
        }).start();
    }
    
}
