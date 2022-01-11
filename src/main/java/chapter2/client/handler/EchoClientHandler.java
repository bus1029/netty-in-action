package chapter2.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import static io.netty.channel.ChannelHandler.Sharable;

@Sharable // Handler 인스턴스들이 채널들 사이에서 공유될 수 있음을 의미
@Slf4j
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
  /*
  Channel 이 활성화 되었을 때 메시지를 전송
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
  }

  /*
   서버에서 전송한 메시지는 Transport 계층의 전송 방식(stream-oriented)에 따라 한 번에 전송되지 않고 나눠서 전송될 수 있다
   즉, 아무리 작은 메시지라도 나눠서 전송될 경우 channelRead0 가 여러번 불려질 수 있다
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
    log.info("Client received: {}", msg.toString(CharsetUtil.UTF_8));
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.error(cause.toString());
    ctx.close();
  }
}
