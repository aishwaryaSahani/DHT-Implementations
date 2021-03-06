import UtilityTest.md5
import akka.actor.{ActorPath, ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import com.simulation.{CANActorDriver, ChordActorDriver}
import com.simulation.actors.chord.supervisors.SupervisorActor
import com.simulation.beans.EntityDefinition
import org.scalatest.{BeforeAndAfter, FlatSpecLike, MustMatchers, stats}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ListBuffer
import scala.io.Source

class SimulationTest extends TestKit(ActorSystem("actorSystemTest")) with FlatSpecLike with BeforeAndAfter with MustMatchers {
  var nodeIndex: Int = -1

  "Hash" should "be generated properly" in {
    assert(md5("test", 3) < math.pow(2, 3))
  }

  "User Actor object after creation" should "not be null" in {
    val sender = TestProbe()
    val id: Int = 999
    val actorSystem = ActorSystem("actorSystem")
    val userActor = actorSystem.actorOf(Props(new UserActorTest(id, actorSystem)), "user_actor")
    sender.send(userActor, UserActorTest.createUserActor(id))
    val state = sender.expectMsgType[ActorPath]
    assert(state != null)
  }

  "User Actor object" should "be correctly created" in {
    val sender = TestProbe()
    val id: Int = 999
    val actorSystem = ActorSystem("actorSystem")
    val userActor = actorSystem.actorOf(Props(new UserActorTest(id, actorSystem)))
    sender.send(userActor, UserActorTest.createUserActor(id))
    val state = sender.expectMsgType[ActorPath]
    assert(state.name == id.toString && userActor.path + "/" + id.toString == state.address + "/" + state.elements.mkString("/"))
  }

  "6" should "belong in range 3 to 0" in {
    val id: Int = 999
    val serverActor = new ServerActorTest(id, 15)
    assert(serverActor.belongs(1,3,2))
    assert(serverActor.belongs(4,0,7))
  }

  "Node" should "be properly created for CHORD" in {
    nodeIndex = ChordActorDriver.createServerNode()
    assert(nodeIndex != -1)
  }

  "Data" should "be loaded properly for CHORD" in {
    val result = ChordActorDriver.loadData(5)
    assert(result != "")
  }

  "Data" should "be present for CHORD" in {
    val result = ChordActorDriver.getData(5)
    assert(result != "")
  }

  "Node" should "be removed properly for CHORD" in {
    val result = ChordActorDriver.removeNode(nodeIndex)
    assert(result == true)
  }

  "Node" should "be properly created for CAN" in {
    nodeIndex = CANActorDriver.createServerNodeCAN()
    assert(nodeIndex != -1)
  }

  "Data" should "be loaded properly for CAN" in {
    val result = CANActorDriver.loadData(5)
    assert(result != "")
  }

  "Data" should "be present for CAN" in {
    val result = CANActorDriver.getData(5)
    assert(result != "")
  }

  "Node" should "be removed properly for CAN" in {
    val result = CANActorDriver.removeNode(nodeIndex)
    assert(result == true)
  }

}
