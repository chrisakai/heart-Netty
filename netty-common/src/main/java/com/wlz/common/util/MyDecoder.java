package com.wlz.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

/**
 * @Author Apollo
 * @Date 2023/9/18 16:38
 * @Version 1.0
 */
public class MyDecoder extends ByteToMessageDecoder {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String clientIpAllowed = get("clientIpAllowed");  //该编码器只试用于指定客户端传入的数据，因为协议是根据客户端数据类型定义的
    private static int MAX_PACKAGE = 256*1024*1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        System.out.println("...............客户端连接服务器...............");
        String client = ctx.channel().remoteAddress().toString();
        System.out.println("客户端IP: " + client);
        System.out.println("连接时间："+ sdf.format(new Date()));
        //判断IP合法性
        if (!StringUtils.isEmpty(client)) {
            System.out.println("...............客户端IP合法，开始数据解析 ...............");
            //开始解析
            //定义一个map接收数据
            LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
            //定义个StringBuffer用于字符串拼接
            StringBuffer sb = new StringBuffer();
            //数据接收时间
            dataMap.put("dataTime",sdf.format(new Date()));

            //按照规定的数据协议读取数据
            //------------------包头-------------------
            //协议开始标志
            buf.resetReaderIndex();
            ByteBuf headFlag = buf.readBytes(4);
            System.out.println(ByteBufUtil.hexDump(headFlag));
            if(ByteBufUtil.hexDump(headFlag).contains("ea82")||ByteBufUtil.hexDump(headFlag).contains("7dba")){
                dataMap.put("headFlag", ByteBufUtil.hexDump(headFlag));

                //数据总长度
                ByteBuf  lengthBytes= buf.readBytes(4);
                int length = Integer.parseInt(ByteBufUtil.hexDump(lengthBytes), 16);
                dataMap.put("length", length);

                //消息序号
                ByteBuf  messageNo= buf.readBytes(4);
                dataMap.put("messageNo", convertByteBufToString(messageNo));

                //处理器标识
                ByteBuf  sign= buf.readBytes(4);
                dataMap.put("sign", convertByteBufToString(sign));

                //序列化类型
                ByteBuf  serialType= buf.readBytes(4);
                dataMap.put("serialType", convertByteBufToString(serialType));

                //拓展消息头
                ByteBuf  messageHead= buf.readBytes(4);
                dataMap.put("messageHead", convertByteBufToString(messageHead));

                //拓展消息超时
                ByteBuf messageTimeoutBuf= buf.readBytes(4);
                int messageTimeout = messageTimeoutBuf.readInt();
                dataMap.put("messageTimeout", messageTimeout);

                //本次数据包的长度
                ByteBuf DataLenBuf= buf.readBytes(4);
                int DataLen = DataLenBuf.readInt();
                dataMap.put("DataLen", DataLen);

                //--------------数据包---------------
                if (DataLen > 0) {
                    ByteBuf restData = buf.readBytes(DataLen);
                    String str = convertByteBufToString(restData);
                    System.out.println(str);
                    //这边str可直接转换传给后台数据处理API
                    dataMap.put("json", str);
//                    JSONObject jsonObject = JSONObject.parseObject(str);
//                    dataMap.put("size", jsonObject.getString("size"));
//                    JSONArray data = jsonObject.getJSONArray("data");
//                    //箱门序号
//                    dataMap.put("doorSerial", data.getJSONObject(0).getString("doorSerial"));
//
//                    //资产编号
//                    dataMap.put("assetCode", data.getJSONObject(0).getString("assetCode"));
//
//                    //终端IMEI
//                    dataMap.put("deviceIMEI", data.getJSONObject(0).getString("deviceIMEI"));
//
//                    //终端SN号
//                    dataMap.put("deviceSn", data.getJSONObject(0).getString("deviceSn"));
//
//                    //IP地址
//                    dataMap.put("deviceIp", data.getJSONObject(0).getString("deviceIp"));
//
//                    //在线状态
//                    dataMap.put("online", data.getJSONObject(0).getString("online"));
//
//                    //站点编号
//                    dataMap.put("worksite", data.getJSONObject(0).getString("worksite"));
//
//                    //当前电量
//                    dataMap.put("battery", data.getJSONObject(0).getString("battery"));

                    //--------------包尾---------------
                }

                //存到map中  ，传递到下一个业务处理的handler
                out.add(dataMap);
            }
            else {
                System.out.println("...............非指定协议数据 ...............");
                exceptionCaught(ctx, new Exception("非指定协议数据"));
                buf.clear();
            }

        } else {
            System.out.println("...............服务端暂不接收该IP数据 ...............");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("...............数据解析异常 ...............");
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * ByteBuf转String
     * @param buf
     * @return
     * @throws UnsupportedEncodingException
     */
    public String convertByteBufToString(ByteBuf buf) throws UnsupportedEncodingException {
        String str;
        if(buf.hasArray()) { // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }

    public static String get(String key) {
        Resource resource = new ClassPathResource("settings.properties");
        Properties props = null;
        String property = "";
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
            property = props.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return property;

    }
}
