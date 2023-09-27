package com.wlz.common.packet;

/**
 * @Author Apollo
 * @Date 2023/9/18 11:36
 * @Version 1.0
 */
public class Packet {
    private int magic;
    private int length;
    private byte messageNo;
    private byte sign;
    private byte serialType;
    private byte messageHead;
    private int messageTimeout;
    private int dataLength;
    private byte[] data;

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getMessageNo() {
        return messageNo;
    }

    public void setMessageNo(byte messageNo) {
        this.messageNo = messageNo;
    }

    public byte getSign() {
        return sign;
    }

    public void setSign(byte sign) {
        this.sign = sign;
    }

    public byte getSerialType() {
        return serialType;
    }

    public void setSerialType(byte serialType) {
        this.serialType = serialType;
    }

    public byte getMessageHead() {
        return messageHead;
    }

    public void setMessageHead(byte messageHead) {
        this.messageHead = messageHead;
    }

    public int getMessageTimeout() {
        return messageTimeout;
    }

    public void setMessageTimeout(int messageTimeout) {
        this.messageTimeout = messageTimeout;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
