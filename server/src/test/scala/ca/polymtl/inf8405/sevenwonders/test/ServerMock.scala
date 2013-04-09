package ca.polymtl.inf8405.sevenwonders.test

import org.scalatest.{WordSpec, BeforeAndAfterAll}
import akka.actor.ActorSystem
import ca.polymtl.inf8405.sevenwonders.app.ServerImpl
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.matchers.{ShouldMatchers, MustMatchers}
import org.scalatest.concurrent.Eventually

class ServerSpec( _system: ActorSystem )
  extends TestKit(_system)
  with ImplicitSender
  with WordSpec
  with MustMatchers
  with ShouldMatchers
  with BeforeAndAfterAll
  with ServerMock
  with Eventually
{
  def this() = this(ActorSystem())

  override def afterAll {
    super.afterAll
    system.shutdown()
  }
}

trait ServerMock { self : BeforeAndAfterAll =>

  val system: ActorSystem
  private val server = new ServerImpl( system )

  val serverThread = new Thread( new Runnable {
    def run() {
      server.start()
    }
  })

  override def beforeAll {
    serverThread.start()
    Thread.sleep(1000) // wait for server to start
  }

  override def afterAll {
    server.stop()
  }
}