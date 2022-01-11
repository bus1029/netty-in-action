package chapter2.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import static io.netty.channel.ChannelHandler.Sharable;

@Slf4j
@Sharable // ChannelHandler 가 복수개의 Channel 에 대해서 안전하게 공유될 수 있다는 것을 의미
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
  /*
  입력되는 각 메시지에 대해 호출되는 함수
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf inMsg = (ByteBuf) msg;
    log.info("Server received: {}", inMsg.toString(CharsetUtil.UTF_8));
    ctx.write(inMsg); // Flushing 없이 받은 메시지를 다시 전송한다
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    // 쌓여있는 메시지를 Flushing 하고 Channel 을 닫는다
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
            .addListener(ChannelFutureListener.CLOSE);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.error(cause.toString());
    ctx.close();
  }
}
