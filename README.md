### Netty心跳服务器
**Springboot2.0.8**集成 **netty4** ，使用protobuf作为ping的数据交换，比json更加的小巧,占用数据量更小，可用于任何第三方应用做心跳监控

### 应用场景

比如公司可以使用的一种业务场景是这样的：
现有多台服务器，有a业务的 有b业务的 有c业务的 有d业务项目等等，有时候用户量大或者其他因素，这些服务器down掉了，大多数情况都是基于运维监控报警的，但是由于项目都是多台机器部署同一个项目，项目挂了后centos主机并不能很快检查出来，一般时间会比较长在半小时之内，甚至有的时候是用户来打电话告知项目down掉了，这对公司来说影响多么的大。
挂上了项目心跳后，可以实时看到100多台机器的实时ping值是否正常，当然你也可以通过定时请求http接口来判断是否应用正常，熟知http接口的请求耗费资源，使用本项目的ping或者其他鉴权，protobuff基本上消耗网路开销非常的小，响应快，netty的长连接上线下立马感知，性能稳定好。


![监控服务器心跳](https://images.gitee.com/uploads/images/2019/0224/181351_7c363a66_1225689.png "a.png")
![监控服务器心跳](https://images.gitee.com/uploads/images/2019/0224/181553_eeb752eb_1225689.png "b.png")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0224/182236_f2b830bd_1225689.png "c.png")

### 开发环境
- JDK8+
- IntelliJ IDEA 
- springboot 2.0.8.RELEASE
- Netty4.1.6.Final
- protobuf-java3.3.0
- Redis
- Thymeleaf模板
- Echarts图表
- Tomcat8.5
### 项目说明
1. 已完成功能
  - 客户端授权验证(基于protoBuff)
  - 心跳检测(基于protoBuff)
  - 断线重连
  - 计算ping值(支持到微秒)
  - 其他子业务模块(架子已搭进去)
  - 为了方便测试，项目已经支持跨域访问
  
### 说明
> Netty是一个NIO客户端服务器框架，可以快速轻松地开发协议服务器和客户端等网络应用程序。它极大地简化并简化了TCP和UDP套接字服务器等网络编程。 “快速简便”并不意味着最终的应用程序会受到可维护性或性能问题的影响。Netty经过精心设计，具有丰富的协议，如FTP，SMTP，HTTP以及各种二进制和基于文本的传统协议。因此，Netty成功地找到了一种在不妥协的情况下实现易于开发，性能，稳定性和灵活性的方法。4x(推荐,稳定,jdk1.6+)

  几个常见的类
- ServerBootstrap
> netty服务器的帮助类

- NioEventLoopGroup
> 处理I/O操作的多线程事件循环相当于一组线程池，服务器一般需要指定两个NioEventLoopGroup，一个作为监控tcp连接的，一个作为处理io事件的，前者默认1个线程就可以，后者最好是cpu核心数的2倍  客户端用一个就够了

- NioServerSocketChannel
> 主要是server端接收建立SocketChannel用的

- NioSocketChannel
> 主要是客户端接收SocketChannel用的


- ChannelInboundHandlerAdapter
> 可以重写的各种事件处理程序方法,包括channelRead()、exceptionCaught()等方法

- SimpleChannelInboundHandler
> 可以重写的各种事件处理程序方法,包括channelRead0()方法 这个可以跟指定的消息类型比上面的，如果自定义的消息类型用这个稍微多一点

- ChannelPipeline
> 存放各种处理器，包括解码器，编码器等自定义处理器，idleStateHandler一定要放在第一个，传送数据时，编码器和解码器一定要放在前面，这个加载是分顺序的

- ChannelHandlerContext
> ChannelHandlerContext的writeAndFlush和ChannelHandlerContext.channel().writeAndFlush()是有区别的
  前者是从当前hanler从后往前找OutputboundHandler，然后交给它执行的，后者是从最后一个开始执行handler的，最常见就是写错pipeline中的顺序后，客户端或服务器发消息就收不到了

- ChannelInitializer
> 客户端和服务器都要用这个的SocketChannel，初始化的时候，加载ChannelPipeline

- Bootstrap
> 客户端的连接帮助类

- idleStateHandler
> Netty 可以使用 IdleStateHandler 来实现连接管理，当连接空闲时间太长（没有发送、接收消息）时则会触发一个事件，我们便可在该事件中实现心跳机制


基本以上的几个类就可以满足日常的80%需求了，剩下就是书写各自的业务

### 心跳服务器实现详细过程
1. 客户端网络空闲5秒没有进行写操作是，进行发送一次ping心跳给服务端；用自定义的消息格式类，其中包含验证auth、ping、pong等类型来代表不同的业务，首次连接时候在ChannelInboundHandlerAdapter中可以建立连接，客户端会发送一次auth类型的授权信息(用户名+盐值密码)，服务器收到后作出校验响应，只有收到服务器的auth_ack响应客户端才能做出连接，否则断开当前链接，释放链路；

2. 客户端如果在下一个发送ping心跳周期来临时，还没有收到服务端pong的心跳应答，则失败心跳计数器加1；

3. 每当客户端收到服务端的pong心跳应答后，失败心跳计数器清零；

4. 如果连续超过3次没有收到服务端的心跳回复，则断开当前连接，在5秒后进行重连操作，直到重连成功，否则每隔5秒又会进行重连；

5. 服务端网络空闲状态到达10秒后，服务端心跳失败计数器加1；

6. 只要收到客户端的ping消息，服务端心跳失败计数器清零；

7. 服务端连续3次没有收到客户端的ping消息后，将关闭链路，释放资源，等待客户端重连；

心跳相关验证

当客户端超过三次没有收到服务器pong，客户端主动断开当前channel连接，重连服务器
![客户端验证](https://images.gitee.com/uploads/images/2019/0225/191246_50ac041e_1225689.jpeg "QQ截图20190225135106.jpg")

当服务器超过三次没有收到客户端ping，服务器主动断开当前客户端的channel连接
![服务器验证](https://images.gitee.com/uploads/images/2019/0225/191457_0e850402_1225689.jpeg "2.jpg")
### ping值设计过程
ping值是基于客户端来的，即客户端发送ping之后，会先记录当前的时间(默认单位是ms)，支持微秒的方法，当客户端再次收到响应后，记录当前时间a，因为数据传输双方时间是相同的，所以拿当前时间b-a,再除以2就是ping值了。然后把这个ping值，记录到redis中去。这里使用的是
System.currentTimeMillis(),用new Date().getTime()时间会出错，不知道为啥？看源码是一样的逻辑不大理解
> 起初是基于服务器计时的，但是服务器和客户端不可能在一台主机上，导致时间存在偏差，ping值就不准确了。
存到redis主要是为了使echarts访问最近几次的ping状态。起初想做成websocket的，但是这样的话，第一次进来当前页面数据会不全，而且不好日后做历史ping的统计
![ping测试](https://images.gitee.com/uploads/images/2019/0225/211504_16e5c54d_1225689.jpeg "2.jpg")

### 注意点
本项目的客户端启动是基于springboot的，主要是nettyClient是构造函数方法实现的类，并没有加入spring管理，主要是为了断线重现的时候，获取到的是当前的客户socket。所以并不能使用spring的依赖注入其他类。需要手工加载，这里使用的是implements ApplicationContextAware方法，这里注意要把这个类设置成关闭懒加载，否则这里项目启动得到的ApplicationContext为null，springboot中在bean前加入 @Lazy(value = false) spring中加入lazy-init=false即可，特别注意，否则会没有监控数据

如果你的项目是jdk1.6或jdk1.7的，注意把项目中这个地方改成这种书写方式，因为这两个不支持jdk8+的Lambdas表达式

以下是jdk1.6或jdk1.7写法
```
ChannelFuture f = bootstrap.connect().addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture futureListener) throws Exception {
						final EventLoop eventLoop = futureListener.channel().eventLoop();
						if (!futureListener.isSuccess()) {
							futureListener.channel().eventLoop().schedule(new Callable<Bootstrap>() {
								@Override
								public Bootstrap call() throws Exception {
									return doConnect(new Bootstrap(), eventLoop);
								}
							},5, TimeUnit.SECONDS);
						}
					}
					});
```
	eventLoop.schedule(new Callable<Bootstrap>() {
			@Override
			public Bootstrap call() throws Exception {
				return nettyClient.doConnect(new Bootstrap(), eventLoop);
			}
		}, 10L, TimeUnit.SECONDS);
```

```
jdk8+则是以下写法，项目中默认用的这种写法

```
ChannelFuture f = bootstrap.connect().addListener((ChannelFuture futureListener)->{
					final EventLoop eventLoop = futureListener.channel().eventLoop();
					if (!futureListener.isSuccess()) {
						futureListener.channel().eventLoop().schedule(() -> doConnect(new Bootstrap(), eventLoop), 5, TimeUnit.SECONDS);
					}
				});
```


```
eventLoop.schedule(() -> nettyClient.doConnect(new Bootstrap(), eventLoop), 10L, TimeUnit.SECONDS);
```

### 使用方法

项目采用聚合工程，分模块的，server、client、common。
由于netty4不支持生成clientId了，需要你手动指定一个，这样每次建立连接的时候Channel进行绑定，默认的工具类项目已经集成。这个日后会搞一套分布式生成id的方法，现在手动指定，这样监控的时候好区分你的主机
项目下载下来后，打包，把server.jar部署到生产服务器，默认web监控端口10003，netty端口19999，修改你的redis地址，启动server
客户端打包后加入到你的项目中，详情

1.启动server

2.打开

http://localhost:10003/monitor.do

3.启动客户端建立连接，就可以看到实时心跳监控数据了，按客户端数量显示图标


嵌入到你的其他系统方法
```
NettyClient client = new NettyClient();
		client.run();
```
加入到spring项目中方法：
```
@Component

public class ClientStart implements ApplicationListener<ContextRefreshedEvent> {

	public Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		NettyClient client = new NettyClient();
		try {
			client.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

```

加入springboot项目中的方法：
```
@SpringBootApplication
public class ClientApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ClientApplication.class, args);
		NettyClient client = new NettyClient();//加入启动
		client.run();
	}
```


前端监控采用js定时函数来实现，5s中请求后台数据，动态刷新echarts的值，前端模板框架没用过的也不要紧，项目已经接口支持跨域操作，你可以直接打开
- netty-server/resources下的monitor.html页面，就可以看到监控页面了。

也可通过后端访问监控页面方式：
- http://localhost:10003/monitor.do

服务器启动后，当有一个客户端连上时，监控页面会显示一台机器的心跳，同理多个客户端连上时，监控页面能显示所有连上的客户端状态

- 全屏默认最多显示两个监测图，已适配不同分辨率

- 单客户端连接实例图
 ![输入图片说明](https://images.gitee.com/uploads/images/2019/0225/211725_1fc18d9a_1225689.jpeg "1.jpg")
- 多个客户端连接实例图
![输入图片说明](https://images.gitee.com/uploads/images/2019/0225/192127_c0bbb8d3_1225689.jpeg "c.jpg")



### 多谢大家的打赏
![输入图片说明](https://images.gitee.com/uploads/images/2019/0224/184235_9dcd0a3b_1225689.jpeg "微信图片_20190224184201.jpg")