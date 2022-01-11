package chapter2.client;

import chapter2.client.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class EchoClient {
  private final String host;
  private final int port;

  private EchoClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void start(String inputHost, int inputPort) throws InterruptedException {
    String ip = inputHost;
    if (isInvalidIp(ip)) {
      log.error("Invalid Ip address[{}], ip was set to default ip(127.0.0.1).", inputHost);
      ip = "127.0.0.1";
    }

    int innerPort = inputPort;
    if (isInvalidPort(innerPort)) {
      log.error("Invalid port[{}], port was set to default port(8888).", innerPort);
      innerPort = 8888;
    }

    new EchoClient(ip, innerPort).start();
  }

  private boolean isInvalidPort(int port) {
    return port < 0 || port > 65535;
  }

  private boolean isInvalidIp(String ip) {
    String pattern = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
    return ip.matches(pattern);
  }


  private void start() throws InterruptedException {
    NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(eventLoopGroup)
              .channel(NioSocketChannel.class) // Channel type: NIO transport
              .remoteAddress(new InetSocketAddress(host, port))
              .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                  ch.pipeline().addLast(new EchoClientHandler());
                }
              });
      // 모든 준비가 완료되면 서버에 접속을 시도한다
      ChannelFuture channelFuture = bootstrap.connect().sync();
      channelFuture.channel().closeFuture().sync();
    } finally {
      eventLoopGroup.shutdownGracefully().sync();
    }
  }
}
