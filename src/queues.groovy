@GrabResolver(name = 'artifactory', root = 'http://flmaven.westlan.com/artifactory/repo/')
@Grab(group = 'com.ibm', module = 'com.ibm.mqjms', version = '7.5.0')
@Grab(group = 'com.ibm', module = 'com.ibm.mq.jmqi', version = '7.5.0')
@Grab(group = 'com.ibm', module = 'dhbcore', version = '7.5.0')
@Grab(group = 'com.ibm', module = 'connector', version = '7.5.0')
@Grab(group = 'com.ibm', module = 'com.ibm.mq.commonservices', version = '7.5.0')
@Grab(group = 'com.ibm', module = 'jms', version = '7.5.0')
@Grab(group = 'com.ibm', module = 'com.ibm.mq.headers', version = '7.5.0')

import javax.jms.JMSException
import javax.jms.Session

import com.ibm.jms.JMSMessage
import com.ibm.jms.JMSTextMessage
import com.ibm.mq.jms.JMSC
import com.ibm.mq.jms.MQQueue
import com.ibm.mq.jms.MQQueueConnection
import com.ibm.mq.jms.MQQueueConnectionFactory
import com.ibm.mq.jms.MQQueueReceiver
import com.ibm.mq.jms.MQQueueSender
import com.ibm.mq.jms.MQQueueSession


// Todo: Set these to the real values
final String hostName = "hostname"
final String queueURI = "queue:///QUEUE.NAME"
final String QueueManager = "MANAGERNAME"
final String channel = "CHANNELNAME"

try {
    MQQueueConnectionFactory cf = new MQQueueConnectionFactory()

    // Config
    cf.setHostName(hostName)
    cf.setPort(1414)
    cf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP)
    cf.setQueueManager(QueueManager)
    cf.setChannel(channel)

    MQQueueConnection connection = (MQQueueConnection) cf.createQueueConnection()
    MQQueueSession session = (MQQueueSession) connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)
    MQQueue queue = (MQQueue) session.createQueue(queueURI)
    MQQueueSender sender = (MQQueueSender) session.createSender(queue)
    MQQueueReceiver receiver = (MQQueueReceiver) session.createReceiver(queue)

    long uniqueNumber = System.currentTimeMillis() % 1000
    JMSTextMessage message = (JMSTextMessage) session.createTextMessage("Simple Message ${uniqueNumber}")

    // Start the connection
    connection.start()

    sender.send(message)
    println("Sent message:\n" + message)

    JMSMessage receivedMessage = (JMSMessage) receiver.receive(10000)
    println("\nReceived message:\n${receivedMessage}")

    sender.close()
    receiver.close()
    session.close()
    connection.close()

    println("\nSUCCESS\n")
}
catch (JMSException jmsex) {
    println(jmsex)
    println("\nFAILURE\n")
}
catch (Exception ex) {
    println(ex)
    println("\nFAILURE\n")
}
