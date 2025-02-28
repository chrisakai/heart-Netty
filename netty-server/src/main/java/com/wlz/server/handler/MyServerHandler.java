package com.wlz.server.handler;

/**
 * @Author Apollo
 * @Date 2023/9/18 18:06
 * @Version 1.0
 */
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wlz.common.entity.DeviceInfo;
import com.wlz.common.util.HttpUrl;
import com.wlz.common.util.HttpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * channelAction
     * channel 通道 action 活跃的
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端：" + ctx.channel().localAddress().toString() + " 通道开启！");
    }

    /**
     * channelInactive
     * channel 通道 Inactive 不活跃的
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     */
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端：" + ctx.channel().localAddress().toString() + " 通道关闭！");
        // 关闭流
    }

    /**
     *
     * @author Taowd
     * TODO  此处用来处理收到的数据中含有中文的时  出现乱码的问题
     * @param buf
     * @return
     */
    private String getMessage(ByteBuf buf) {
        byte[] con = new byte[buf.readableBytes()];
        buf.readBytes(con);
        try {
            return new String(con, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 功能：读取客户端发送过来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf readmsg = (ByteBuf) msg;

        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) msg;

        for(Map.Entry<String, Object> entry : map.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = entry.getValue().toString();
            System.out.println(mapKey+":"+mapValue);
        }

        LinkedHashMap<String, Object> mapOut = map;
        mapOut.remove("dataTime");
        mapOut.remove("headFlag");
        mapOut.remove("length");
        mapOut.remove("messageNo");
        mapOut.remove("sign");
        mapOut.remove("serialType");
        mapOut.remove("messageHead");
        mapOut.remove("messageTimeout");
        mapOut.remove("DataLen");

        String jsonOutString = JSON.toJSONString(mapOut.get("json")).replaceFirst("^\\\"", "")
                .replaceFirst("\\\"$","");
        System.out.println("数据解析内容:" + jsonOutString);

        //业务逻辑处理
        HttpUtils.sendPost(HttpUrl.pushUpdate(),jsonOutString);
        //业务逻辑处理

        //必须释放，如果继承simplechannelinboundhandler会自动释放，但是报文处理写在channelRead0
        ReferenceCountUtil.release(msg);

    }

    /**
     * 功能：读取完毕客户端发送过来的数据之后的操作
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("...............数据接收-完毕...............");
        // 第一种方法：写一个空的buf，并刷新写出区域。完成后关闭sock channel连接。
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//        String returnInfo = "服务端已接收数据！";
//        ctx.writeAndFlush(Unpooled.copiedBuffer(returnInfo, CharsetUtil.UTF_8)).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 功能：服务端发生异常的操作
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("...............业务处理异常...............");
        cause.printStackTrace();
        ctx.close();
    }

}

