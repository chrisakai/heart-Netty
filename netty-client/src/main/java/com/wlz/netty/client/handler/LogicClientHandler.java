package com.wlz.netty.client.handler;


import com.wlz.common.entity.DeviceInfo;
import com.wlz.common.protobuf.Info;
import com.wlz.common.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicClientHandler extends SimpleChannelInboundHandler<Message>{
	public Logger log = LoggerFactory.getLogger(this.getClass());

	private final static String CLIENTID = "test";

	// 连接成功后，向server发送消息
	@Override
	//todo 在这里编辑发送给服务端的单机数据
	//箱门序号，资产编号，终端IMEI，终端SN号，IP地址，在线状态，站点编号，当前电量
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		Message.MessageBase.Builder authMsg = Message.MessageBase.newBuilder();
//		authMsg.setClientId(CLIENTID);
//		authMsg.setCmd(Command.CommandType.AUTH);
//		authMsg.setData("This is auth data");


//		Info.InfoBase.Builder infoMsg = Info.InfoBase.newBuilder();
//
//		infoMsg.setDoorSerial(1);
//		infoMsg.setAssetCode("10000001");
//		infoMsg.setDeviceIMEI("868769020279064");
//		infoMsg.setDeviceSn("QNGF");
//		infoMsg.setDeviceIp("192.168.30.1");
//		infoMsg.setOnline(1);
//		infoMsg.setWorksite("2001235");
//		infoMsg.setBattery(80);

//		DeviceInfo[] deviceInfos = new DeviceInfo[]
//
//		ctx.writeAndFlush(infoMsg.build());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.debug("连接断开");
	}


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

	}
}
