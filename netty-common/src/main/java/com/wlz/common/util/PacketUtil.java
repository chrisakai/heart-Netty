package com.wlz.common.util;

import com.wlz.common.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @Author Apollo
 * @Date 2023/9/18 11:33
 * @Version 1.0
 */
@Deprecated
public class PacketUtil {
    public static ByteBuf encode(Packet packet) {
        // 创建ByteBuf对象
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        // 获取序列化器序列化对象
        MySerializer serializer = SerializerFactory.getSerializer(packet.getSerialType());
        byte[] data = serializer.serialize(packet);
        // 按照通信协议填充ByteBuf
        byteBuf.writeInt(packet.getMagic());// 魔数
        byteBuf.writeInt(packet.getLength()); //
        byteBuf.writeByte(packet.getMessageNo()); //
        byteBuf.writeByte(packet.getSign()); //
        byteBuf.writeByte(packet.getSerialType());//
        byteBuf.writeByte(packet.getMessageHead()); //
        byteBuf.writeInt(packet.getMessageTimeout()); //
        byteBuf.writeInt(packet.getDataLength()); //
        byteBuf.writeBytes(packet.getData());
        byteBuf.writeInt(data.length);// 数据长度
        byteBuf.writeBytes(data); // 数据
        return byteBuf;
    }

//    public static Packet decode(ByteBuf byteBuf) {
//        // 暂不判断魔数，跳过
//        byteBuf.skipBytes(4);
//        // 暂不判断数据总长度，跳过
//        byteBuf.skipBytes(4);
//        // 暂不判断消息序号，跳过
//        byteBuf.skipBytes(4);
//        // 暂不判断处理器标识，跳过
//        byteBuf.skipBytes(4);
//        // 获取序列化方式
//        byte serialType = byteBuf.readByte();
//        // 暂不判断拓展消息头，跳过
//        byteBuf.skipBytes(4);
//        // 暂不判断拓展消息超时，跳过
//        byteBuf.skipBytes(4);
//        // 获取数据包长度
//        int length = byteBuf.readInt();
//        // 获取存储数据的字节数组
//        byte[] data = new byte[length];
//        byteBuf.readBytes(data);
//        // 反序列化数据，获取Packet
//        Packet res = SerializerFactory.getSerializer(serialType).deserialize(packetType, data);
//        return res;
//    }
}

