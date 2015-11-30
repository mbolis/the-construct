package it.mbolis.explore.session;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class AsyncSession implements Closeable {

	private static final Charset UTF8 = Charset.forName("utf-8");

	private final AsynchronousSocketChannel socketChannel;
	private final StringBuffer lineBuffer = new StringBuffer();

	private String name;

	public AsyncSession(AsynchronousSocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private final class OnReadComplete implements CompletionHandler<Integer, ByteBuffer> {

		private final CompletableFuture<String> future;

		public OnReadComplete(CompletableFuture<String> future) {
			this.future = future;
		}

		@Override
		public void completed(Integer nbytes, ByteBuffer buffer) {
			if (nbytes == -1) {
				return;
			}
			buffer.flip();
			CharBuffer chars = UTF8.decode(buffer);
			lineBuffer.append(chars);

			int newline = lineBuffer.indexOf("\n");
			if (newline < 0) {
				socketChannel.read(buffer, buffer, this);
				return;
			}
			String line = lineBuffer.substring(0, newline);
			lineBuffer.delete(0, newline + 1);
			future.complete(line);
		}

		@Override
		public void failed(Throwable exc, ByteBuffer buffer) {
		}
	}

	@FunctionalInterface
	private static interface FunctionalCompletionHandler<V, A> extends CompletionHandler<V, A> {

		void handle(Throwable error, V result, A attachment);

		@Override
		default void completed(V result, A attachment) {
			handle(null, result, attachment);
		}

		@Override
		default void failed(Throwable exc, A attachment) {
			handle(exc, null, attachment);
		}
	}

	public synchronized Future<String> readLine(Consumer<String> callback) {
		int newline = lineBuffer.indexOf("\n");
		if (newline >= 0) {
			String line = lineBuffer.substring(0, newline);
			lineBuffer.delete(0, newline + 1);
			callback.accept(line);
			return CompletableFuture.completedFuture(line);
		}

		ByteBuffer inBuffer = ByteBuffer.allocate(64);
		CompletableFuture<String> future = new CompletableFuture<>();
		future.thenAccept(callback);
		socketChannel.read(inBuffer, inBuffer, new OnReadComplete(future));
		return future;
	}

	public synchronized void send(String message) {
		ByteBuffer bytes = UTF8.encode(message);
		socketChannel.write(bytes);
	}

	public synchronized

	@Override public void close() throws IOException {
		socketChannel.close();
	}
}
