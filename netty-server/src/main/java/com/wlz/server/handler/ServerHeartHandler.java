package com.wlz.server.handler;

import com.wlz.common.protobuf.Command;
import com.wlz.common.protobuf.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.wlz.server.components.ChannelRepository;


/** 实现心跳的hander  支持超时断开客户端避免浪费资源
  * @Author: myzf
  * @Date: 2019/2/23 13:25
  * @param
*/
@Component(value = "serverHeartHandler")
@ChannelHandler.Sharable
public class ServerHeartHandler extends ChannelInboundHandlerAdapter {
	public Logger log = LoggerFactory.getLogger(this.getClass());


	private final AttributeKey<String> clientInfo = AttributeKey.valueOf("clientInfo");
	// 设置6秒检测chanel是否接受过心跳数据
	private static final int READ_WAIT_SECONDS = 6;

	// 定义客户端没有收到服务端的pong消息的最大次数
	private static final int MAX_UN_REC_PING_TIMES = 3;

	// 失败计数器：未收到client端发送的ping请求
	private int unRecPingTimes = 0 ;

	@Autowired
	@Qualifier("channelRepository")
	private ChannelRepository channelRepository;




	/*空闲触发器 心跳基于空闲实现*/
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			String type = "";
			if (event.state() == IdleState.READER_IDLE) {
				type = "read idle";
			} else if (event.state() == IdleState.WRITER_IDLE) {
				type = "write idle";
			} else if (event.state() == IdleState.ALL_IDLE) {
				type = "all idle";
			}
			if(unRecPingTimes >= MAX_UN_REC_PING_TIMES){
				// 连续超过N次未收到client的ping消息，那么关闭该通道，等待client重连
				ctx.channel().close();
			}else{
				// 失败计数器加1

				unRecPingTimes++;
			}
			log.debug(ctx.channel().remoteAddress()+"超时类型：" + type);
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Message.MessageBase msgBase = (Message.MessageBase)msg;
		String clientId = msgBase.getClientId();

		Channel ch = channelRepository.get(clientId);
		if(null == ch){
			ch = ctx.channel();
			channelRepository.put(clientId, ch);
		}
		/*认证处理*/
		if(msgBase.getCmd().equals(Command.CommandType.AUTH)){
			log.info("收到客户端Id是"+clientId+"的建立连接请求...");
			Attribute<String> attr = ctx.attr(clientInfo);
			attr.set(clientId);
			channelRepository.put(clientId, ctx.channel());

			ctx.writeAndFlush(createData(clientId, Command.CommandType.AUTH_BACK, "客户端Id是"+clientId+"的建立连接请求success！").build());

		}else if(msgBase.getCmd().equals(Command.CommandType.PING)){
			//处理ping消息
			ctx.writeAndFlush(createData(clientId, Command.CommandType.PONG, "服务器响应客户端Id是"+clientId+"的心跳包").build());
			unRecPingTimes = 0;

		}else{
			if(ch.isOpen()){
				//触发下一个handler
				ctx.fireChannelRead(msg);

			}
		}
		ReferenceCountUtil.release(msg);
	}


	/** 构建不同类型的数据 基于protobuff
	  * @Author: myzf
	  * @Date: 2019/2/23 13:25
	  * @param
	*/
	private Message.MessageBase.Builder createData(String clientId, Command.CommandType cmd, String data){
		Message.MessageBase.Builder msg = Message.MessageBase.newBuilder();
		msg.setClientId(clientId);
		msg.setCmd(cmd);
		msg.setData(data);
		return msg;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Attribute<String> attr = ctx.attr(clientInfo);
		String clientId = attr.get();
		log.error("客户端断开链接，Id是 " + clientId+"----错误详情是======"+cause.getMessage());

	}
}
