package it.mbolis.explore.actors;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;

public class JetlangNIO {

    private static Charset charset = Charset.forName("UTF-8");
    private static CharsetEncoder encoder = charset.newEncoder();
    private static CharsetDecoder decoder = charset.newDecoder();

    private static ByteBuffer fromString(String message) {
        encoder.
    }

    static class ReadMessage extends Thread {
        final SocketChannel channel;

        public ReadMessage(SocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            try {

                int read = channel.read(buffer);
                while (read >= 0) {
                    buffer.flip();

                }

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open().bind(null);
        System.out.println(server.getLocalAddress());

        Selector selector = Selector.open();

        while (true) {
            SocketChannel channel = server.accept();
            channel.configureBlocking(false);
            SelectionKey masterKey = channel.register(selector, SelectionKey.OP_READ, new StringBuilder());

            new Thread(() -> {
                boolean open = true;
                while (open) {
                    try {
                        selector.select();
                        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                        while (keys.hasNext()) {
                            SelectionKey key = keys.next();
                            if (key.isReadable()) {
                                SocketChannel ch = (SocketChannel) key.channel();
                                new ReadMessage(ch).start();
                            }
                            keys.remove();
                        }
                    } catch (IOException e) {
                        break;
                    }
                }
            }).start();
        }
    }
}
