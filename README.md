JMS Test Util: Active MQ provider
============

Introduction
----

This is an Active MQ provider for  JMS Test Util


Building
----

To download the project you can use:

```
git clone https://github.com/orpiske/jms-test-provider-activemq.git -b
jms-test-provider-activemq-1.0.0
```

You can use Maven to build the project. No additional configuration or setup
should* be required. To compile and installgi the project, please run:

```
mvn clean install
```

The build system will generate deliverables in zip, tar.bz2, tar.gz format.
Project specific jars will also be generated.

Note: no additional configuration should be required, however if the host
system has any service running on port 61616 (such as ActiveMQ), it may be necessary to
change the TestConfiguration class. It is located
 in the test package. The following line should be modified:

```
public static final String CONNECTOR = "tcp://localhost:61616";
```


Usage
----

Annotate the test class with:

```
@RunWith(JmsTestRunner.class)
@Provider(
        value = ActiveMqProvider.class,
        configuration = ActiveMqConfiguration.class)
```

References
----

* [Main Site](http://orpiske.net/)
* [Apache Active MQ](http://activemq.apache.org/)

