package com.wlz.server.handler;

import com.wlz.common.protobuf.Info;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.wlz.server.components.ChannelRepository;


/** 其他业务拓展模板参考
  * @Author: myzf
  * @Date: 2019/2/23 13:24
  * @param
*/
@Component(value = "otherServerHandler")
@ChannelHandler.Sharable
public class OtherServerHandler extends ChannelInboundHandlerAdapter{
	public Logger log = LoggerFactory.getLogger(this.getClass());

	private final AttributeKey<String> clientInfo = AttributeKey.valueOf("clientInfo");
//	private final RestTemplate http;

	@Autowired
	@Qualifier("channelRepository")
	private ChannelRepository channelRepository;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		Message.MessageBase msgBase = (Message.MessageBase)msg;
//		log.info(msgBase.getData());
//		ChannelFuture cf = ctx.writeAndFlush(
//				MessageBase.newBuilder()
//				.setClientId(msgBase.getClientId())
//				.setCmd(CommandType.UPLOAD_DATA_BACK)
//				.setData("这是业务层的逻辑")
//				.build()
//				);
		Info.InfoBase infoBase = (Info.InfoBase)msg;
		log.info(String.valueOf(infoBase.getAllFields()));
		ChannelFuture cf = ctx.writeAndFlush(
				infoBase.newBuilder()
						.setDoorSerial(infoBase.getDoorSerial())
						.setAssetCode(infoBase.getAssetCode())
						.setDeviceIMEI(infoBase.getDeviceIMEI())
						.setDeviceSn(infoBase.getDeviceSn())
						.setDeviceIp(infoBase.getDeviceIp())
						.setOnline(infoBase.getOnline())
						.setWorksite(infoBase.getWorksite())
						.setBattery(infoBase.getBattery())
						.build()
		);
		//todo 在这里写服务器读取成功后写入数据库的业务逻辑
//		url = ;
//		String parseResult = http.getForObject(url, String.class);
		/* 上一条消息发送成功后，立马推送一条消息 */
		cf.addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				if (future.isSuccess()){
					ctx.writeAndFlush(
							infoBase.newBuilder()
									.setDoorSerial(infoBase.getDoorSerial())
									.setAssetCode(infoBase.getAssetCode())
									.setDeviceIMEI(infoBase.getDeviceIMEI())
									.setDeviceSn(infoBase.getDeviceSn())
									.setDeviceIp(infoBase.getDeviceIp())
									.setOnline(infoBase.getOnline())
									.setWorksite(infoBase.getWorksite())
									.setBattery(infoBase.getBattery())
									.build()
							);
				}
			}
		});
		ReferenceCountUtil.release(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

	}



}
