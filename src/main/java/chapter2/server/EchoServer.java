package chapter2.server;

import chapter2.server.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class EchoServer {
  private final int port;

  private EchoServer(int port) {
    this.port = port;
  }

  public static void start(int input) throws InterruptedException {
    int inputPort = input;
    if (isInvalidPort(inputPort)) {
      log.error("Invalid port[{}], port was set to default port: 5556", inputPort);
      inputPort = 5556;
    }
    new EchoServer(inputPort).start();
  }

  private static boolean isInvalidPort(int port) {
    return port < 0 || port > 65535;
  }

  private void start() throws InterruptedException {
    final EchoServerHandler serverHandler = new EchoServerHandler();
    NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    ServerBootstrap bootstrap = new ServerBootstrap();

    try {
      bootstrap.group(eventLoopGroup)
              .channel(NioServerSocketChannel.class) // NIO Transport Channel 을 목적으로 channel 을 생성한다
              .localAddress(new InetSocketAddress(port))
              .childHandler(new ChannelInitializer<SocketChannel>() {
                // 새로운 연결이 들어올 때마다 새로운 자식 Channel 이 생성되고
                // ChannelInitializer 가 EchoServerHandler 를 Channel 의 ChannelPipeline 에 등록한다
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                  // Channel 의 pipeline 마지막에 직접 생성한 serverHandler 를 등록한다
                  ch.pipeline().addLast(serverHandler);
                }
              });
      // 기본적으로는 bind() 를 할 때까지 기다리지 않지만 sync() 를 걸어서 기다리게 한다
      ChannelFuture channelFuture = bootstrap.bind().sync();
      // Application 이 서버의 Channel 이 닫힐 때까지 기다린다
      channelFuture.channel().closeFuture().sync();
    } finally {
      // 생성된 스레드들을 포함해서 모든 자원들을 해제한다
      eventLoopGroup.shutdownGracefully();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    EchoServer.start(5556);
  }
}
