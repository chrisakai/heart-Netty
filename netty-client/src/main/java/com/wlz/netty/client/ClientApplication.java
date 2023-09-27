package com.wlz.netty.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wlz.common.entity.DeviceInfo;
import com.wlz.netty.client.initializer.ClientStart;
import javafx.scene.Parent;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


/**
 *  服务器启动类
*/
@SpringBootApplication
@PropertySource(value= "classpath:/application.properties")
/*因为这个包不在默认的springboot当前目录下，需要扫描，SpringBeanFactory才可以加载到context*/
@ComponentScan(basePackages = {"com.wlz.common.util"})
public class ClientApplication {

	public static void main(String[] args) throws Exception {

		String serverIP = "192.168.3.184";
		int serverPort = 8888;
		Socket socket = new Socket(serverIP, serverPort);

		byte[] bytes = new byte[256*1024*1024];

		OutputStream outputStream = socket.getOutputStream();

		//固定包头
		bytes[0] = (byte)0xEA;
		bytes[1] = (byte)0x82;
		bytes[2] = (byte)0x7D;
		bytes[3] = (byte)0xBA;
		//数据总长度
		bytes[4] = (byte)0x00;
		bytes[5] = (byte)0x00;
		bytes[6] = (byte)0x00;
		bytes[7] = (byte)0xDB;
		//消息序号
		bytes[8] = (byte)0xEA;
		bytes[9] = (byte)0x82;
		bytes[10] = (byte)0x7D;
		bytes[11] = (byte)0xBA;
		//处理器标识
		bytes[12] = (byte)0xEA;
		bytes[13] = (byte)0x82;
		bytes[14] = (byte)0x7D;
		bytes[15] = (byte)0xBA;
		//序列化类型
		bytes[16] = (byte)0x00;
		bytes[17] = (byte)0x00;
		bytes[18] = (byte)0x00;
		bytes[19] = (byte)0x33;
		//拓展消息头
		bytes[20] = (byte)0x00;
		bytes[21] = (byte)0x00;
		bytes[22] = (byte)0x00;
		bytes[23] = (byte)0x00;
		//拓展消息超时
		bytes[24] = (byte)0x00;
		bytes[25] = (byte)0x00;
		bytes[26] = (byte)0x00;
		bytes[27] = (byte)0x00;


		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDoorSerial(1);
		deviceInfo.setAssetCode("11002301");
		deviceInfo.setDeviceIMEI("3249238923479");
		deviceInfo.setDeviceSn("QWHUIHHEWO783");
		deviceInfo.setDeviceIp("192.168.3.184");
		deviceInfo.setOnline(1);
		deviceInfo.setWorksite("2001235");
		deviceInfo.setBattery((int) (Math.random()*101));

		JSONArray jsonArray = new JSONArray();
		jsonArray.add(deviceInfo);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("size", jsonArray.size());
		jsonObject.put("data", jsonArray);

		String messg = jsonObject.toJSONString();
		System.out.println(Integer.toHexString(messg.getBytes(StandardCharsets.UTF_8).length));
		//数据长度
		bytes[28] = (byte)0x00;
		bytes[29] = (byte)0x00;
		bytes[30] = (byte)0x00;
		bytes[31] = (byte)0x9F;

		for (int i = 0; i < messg.getBytes(StandardCharsets.UTF_8).length; i++) {
			bytes[32+i] =  messg.getBytes()[i];
		}
		outputStream.write(bytes, 0, messg.length() + 32);
		outputStream.flush();
		outputStream.close();

//		SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder()
//				.sources(Parent.class)
//				.child(ClientApplication.class);
//
//		//springApplicationBuilder.application().addInitializers(new ClientStart());
//
//		ConfigurableApplicationContext context = springApplicationBuilder.run(args);
//		//等到springApplicationBuilder启动后再启动ClientStar(),要不然会报空指针异常
//		new ClientStart().initialize(context);

	}

}
