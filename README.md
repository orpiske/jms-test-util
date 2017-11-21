JMS Test Util
============

Introduction
----

This is a JUnit-based JMS test utility.

Build Status
----

Build Status: [![Linux Build Status](https://travis-ci.org/orpiske/jms-test-util.svg?branch=master)](https://travis-ci.org/orpiske/jms-test-util)

Building
----


This is only required if you want to compile it manually. Skip to usage for using the 
binary versions available in the bintray Maven repository.

To download the project you can use:

```
git clone https://github.com/orpiske/jms-test-util.git -b jms-test-util-1.0.0
```

You can use Maven to build the project. No additional configuration or setup
should* be required. To compile and package the project, please run:


```
mvn clean package
```

The build system will generate deliverables in zip, tar.bz2, tar.gz format.
Project specific jars will also be generated.


To install, just run:

```
mvn clean install
```



Usage and Annotations
----

Using as Library
----

**Dependencies**:

To use this project as library on your project you have to add my personal 
[bintray](https://bintray.com/orpiske/libs-release/) repository to the pom.xml
file:

```
<repositories>
    <repository>
        <id>orpiske-repo</id>
        <url>https://dl.bintray.com/orpiske/libs-release</url>
    </repository>
</repositories>
```

Then, the library can be referenced as: 
```
<dependency>
    <groupId>net.orpiske</groupId>
    <artifactId>jms-test-util</artifactId>
    <version>1.1.0</version>
</dependency>
```

**Note**: replace version with the latest available version you wish to use.

**API Documentation**:

**API**:

The API documentation (javadoc) is available [here](http://www.orpiske.net/files/javadoc/jms-test-util-1.1/apidocs/). 

**Usage**:

Use the JmsTestRunner to run the tests (can be done with the @RunWith JUnit
annotation), annotate the producer, consumer fields and perform the test
exchanges.

```
@RunWith(JmsTestRunner.class)
@Provider(
        value = MockProvider.class,
        configuration = MockConfiguration.class)
```

The runner requires a JMS provider to run the tests. At this moment two
providers are available: MockProvider, which uses the MockRunner JMS
provider, and ActiveMqProvider, which creates and embedded ActiveMQ instance
to run the tests (provided in another project). Others may be added in the
future.



Member variables must be annotated with appropriate methods.


```
@Producer
```

This is the message producer. It is used to send a request to the server and
usually represents a client sending a transaction. It shares the same
session as the consumer. If not specified, it will send requests to a default
request queue (with replies set to the default response queue)

```
@Consumer
```

This represents a message consumer on the client-side. It shares the same
session as the message producer.By default it is tied to the  default reply
queue.


Addresses for both the @Consumer and @Producer can be specified with the
address property.


```
@Consumer(address = Defaults.REPLY_TO_QUEUE)
```

```
@JmsSession
```

Represents the active JMS Session.


```
@Listener
```

Represents a server. It receives the transactions sent by a producer,
pseudo-process it and returns a message that is read by a consumer. At this
moment the default ServerListener is not thread-safe, therefore there can be
only one test method per class.

Don't bother creating any of the fields. All of the fields properly annotated
 will be injected by the runner during startup.


Examples
----


A basic synchronous send/receive test:

```
@RunWith(JmsTestRunner.class)
@Provider(
        value = ActiveMqProvider.class,
        configuration = ActiveMqConfiguration.class)
public class SendReceiveTest extends AbstractSendReceive {

    @Consumer
    private MessageConsumer consumer;

    @Producer
    private MessageProducer producer;

    @JmsSession
    private Session session;

    @Test
    public void testSendReceive() throws JMSException {
        TextMessage request = session.createTextMessage("test");

        producer.send(textMessage);

        Message response = consumer.receive(5000);
    }
}
```

Using a server listener:

```
@RunWith(JmsTestRunner.class)
@Provider(
        value = MockProvider.class,
        configuration = MockConfiguration.class)
public class RequestReplyStringTest extends AbstractRequestReply<TextMessage> {
    @Consumer(address = Defaults.REPLY_TO_QUEUE)
    private MessageConsumer consumer;

    @Producer
    private MessageProducer producer;

    @JmsSession
    private Session session;

    @Listener
    private ServerListener listener;

    @Test
    public void testSendReceiveText() throws JMSException {
        Destination replyTo = session.createQueue(Defaults.REPLY_TO_QUEUE);

        Message request = session.createTextMessage("marco");
        request.setJMSReplyTo(replyTo);
        request.setStringProperty(ServerListener.REPLY_BUILDER,
                        StringReplyBuilder.class.getName());

        producer.send(request);

        Message response = consumer.receive(1000 * 5);
        // handle the response
    }
}
```

To customize the responses, so that you can elaborate more complex responses,
modify headers, etc, you can implement a ReplyBuilder. The interface it's
simple: given a Session and the request object, you can pseudo-process the
request and give an adequate response that matches your tests.


```
public class StringReplyBuilder implements ReplyBuilder {
    public Message build(Session session, Message request) throws JMSException {
        return Util.createMessage(session, "polo");
    }
}
```


Dependencies
----

Once compiled and installed, you can refer to the dependencies as:

```
<dependency>
    <groupId>net.orpiske</groupId>
    <artifactId>jms-test-util</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>net.orpiske</groupId>
    <artifactId>jms-test-util</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <type>test-jar</type>
    <scope>test</scope>
</dependency>
```

Running
----
The tests are run with JUnit. A main class is provided for running via
command-line but it is not yet documented.


References
----

* [Main Site](http://orpiske.net/)
* [ActiveMQ Provider](https://github.com/orpiske/jms-test-provider-activemq)


