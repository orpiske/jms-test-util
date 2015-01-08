JMS Test Util: Active MQ provider
============

Introduction
----

This is an Active MQ provider for  JMS Test Util


Building
----

You can use Maven to build the project. No additional configuration or setup
should* be required. To compile and package the project, please run:

```
mvn clean package
```

The build system will generate deliverables in zip, tar.bz2, tar.gz format.
Project specific jars will also be generated.

Note: no additional configuration should be required, however if the host system
has any service running on port 61616 (such as ActiveMQ), it may be necessary to
change the TestConfiguration class. It is located
 in the test package. The following line should be modified:

```
public static final String CONNECTOR = "tcp://localhost:61616";
```

Install
----
Just unpack the code. The project script is located in the bin directory and
it is named "runtest.sh".


Usage
----



```
export JAVA_OPTS="-Dnet.orpiske.jms.log.level=trace" ; ./runtest.sh
```

Available log levels are verbose, debug and trace.


References
----

* [Main Site](http://orpiske.net/)

