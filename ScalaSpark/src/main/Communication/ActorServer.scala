package Communication

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory



class ActorServer extends Actor{
  override def receive:Receive={
    case "start" => println("老娘已就绪 ！")

    case ClientMessage(msg) => {
      println(s"收到客户端消息：$msg")
      msg match {
        case "你叫啥" => sender() ! ServerMessage("铁扇公主")
        case "你是男是女" => sender() ! ServerMessage("老娘是男的")
        case "你有男票吗" => sender() ! ServerMessage("没有")
        case _ => sender() ! ServerMessage("What you say ?") //sender()发送端的代理对象， 发送到客户端的mailbox中 -> 客户端的receive
      }
    }
  }
}
object ActorServer{
  def main(args:Array[String]):Unit={
    val host="127.0.0.1"
    val port=8088
    val config=ConfigFactory.parseString(
      s"""
         |akka.actor.provider="akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname=$host
         |akka.remote.netty.tcp.port=$port
          """.stripMargin)
    val as=ActorSystem("Server",config)
    val sa=as.actorOf(Props[ActorServer], "gyb")
    sa ! "start"
  }
}