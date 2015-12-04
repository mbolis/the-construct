package it.mbolis.explore.actors;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;

public class JetlangNIO {

    private static Charset charset = Charset.forName("UTF-8");
    private static CharsetEncoder encoder = charset.newEncoder();
    private static CharsetDecoder decoder = charset.newDecoder();

    static class ReadMessage extends Thread {
        final SocketChannel channel;

        public ReadMessage(SocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            try {

                StringBuilder message = new StringBuilder();
                while (channel.read(buffer) >= 0) {
                    buffer.flip();
                    CharBuffer chars = decoder.decode(buffer);
                    message.append(chars);

                    int eol = message.indexOf("\n");
                    if (eol >= 0) {
                        System.out.println(message.substring(0, eol));
                        message.delete(0, eol + 1);
                    }

                    buffer.clear();
                }
                System.out.println("connessione terminata.");

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {

        ExecutorService threadPool = newFixedThreadPool(4);
        AsynchronousChannelGroup threadGroup = AsynchronousChannelGroup.withThreadPool(threadPool);
        AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open(threadGroup).bind(null);
        System.out.println("listening on port: " + ((InetSocketAddress) serverChannel.getLocalAddress()).getPort());

        threadPool.submit(() -> serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

            @Override
            public void completed(AsynchronousSocketChannel result, Void attachment) {

            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                // TODO Auto-generated method stub

            }
        }));

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        Thread[] threads = new Thread[100];
        Runnable task = () -> {
            try {
                System.out.println(Thread.currentThread().getName() + ": " + queue.take());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(task, "task " + i);
            threads[i].start();
        }

        System.out.println("many many threads");
        for (int i = 0; i < 100; i++) {
            queue.add("miao!");
        }
    }
}
