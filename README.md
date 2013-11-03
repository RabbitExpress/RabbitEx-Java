RabbitEx-Java
=============

So you want to get into async messaging, but there's a lot to learn and it takes too long. While AMQP is a great protocol, it can be daunting for a development team unfamiliar with it. Well pick RabbitMQ and RabbitEx(press).
This will allow you to get up and running using a very simple Tibco RVesque subject based addressing system. Whats more is that it will be coming in mutiple languages, so if your shop is full of polyglots, they can all use the same library.

Create a Connection
-------------------

This is as simple as the following. 

```
RabbitEx rabbitEx = new RabbitConnectionFactory().rabbitConnection(HOSTNAME, PORT);
```

Slightly more advanced would be using virtual hosts and authentication:
```
RabbitEx rabbitEx = new RabbitConnectionFactory().rabbitConnection(HOSTNAME, PORT, VIRTUAL_HOST, USERNAME, PASSWORD);
```
Using  virtual hosts you can separate out environments while using the same Rabbit installation, 

e.g. 
```VIRTUAL_HOST = "development"``` or ```VIRTUAL_HOST = "test"```

Note: The virtual host will need to be set up using the management console (default http://localhost:15672)

Publish a Message
-----------------

In it's simplest form this is as follows:

```
rabbitEx.publish(EXCHANGE, SUBJECT, MESSAGES, null);
```

Exchanges are separations within the rabbit environment. This could be serivces (e.g. payments, email etc.). 
It is recommended not to use these for environmental separation (e.g. development and test), and instead use virtual hosts.

Subjects are used to separate messages within the exchange, but wildcards can be used (see later)

The 4th parameter, which is null here, can be used to set an "error exchange" and "error subject", which the consumer can use to notify of any errors processing a message.


Consume a Message
-----------------





