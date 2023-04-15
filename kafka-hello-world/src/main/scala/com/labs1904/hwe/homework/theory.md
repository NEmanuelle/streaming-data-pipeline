# Overview

Kafka has many moving pieces, but also has a ton of helpful resources to learn available online. In this homework, your
challenge is to write answers that make sense to you, and most importantly, **in your own words!**
Two of the best skills you can get from this class are to find answers to your questions using any means possible, and to
reword confusing descriptions in a way that makes sense to you. 

### Tips
* You don't need to write novels, just write enough that you feel like you've fully answered the question
* Use the helpful resources that we post next to the questions as a starting point, but carve your own path by searching on Google, YouTube, books in a library, etc to get answers!
* We're here if you need us. Reach out anytime if you want to ask deeper questions about a topic 
* This file is a markdown file. We don't expect you to do any fancy markdown, but you're welcome to format however you like

### Your Challenge
1. Create a new branch for your answers 
2. Complete all of the questions below by writing your answers under each question
3. Commit your changes and push to your forked repository

## Questions
#### What problem does Kafka help solve? Use a specific use case in your answer 
* Helpful resource: [Confluent Motivations and Use Cases](https://youtu.be/BsojaA1XnpM)
scalability, distribution of data, dealing with massive amounts of data in realtime. Credit card fraud can now
notify you right away when fraudulent activity happens. 
#### What is Kafka?
* Helpful resource: [Kafka in 6 minutes](https://youtu.be/Ch5VhJzaoaI) 
Kafka is a platform used to handle massive amounts of messages in realtime in a way
that is efficient, durable and fast.
#### Describe each of the following with an example of how they all fit together: 
 * Topic: A group of partitions handling the same type of data
 * Producer: The producer writes the record/message/event
 * Consumer: The consumer reads the record/message/event
 * Broker: a server that holds the partitions (these hold the append only logs in a 
organized system)
 * Partition: A partition is where the messages are written to, there are multiple partitions
and when using the redundancy factor messages are copied to different partitions in case
a broker goes down- no info would be lost.

#### Describe Kafka Producers and Consumers
Producers write the message and consumers read the message. 
#### How are consumers and consumer groups different in Kafka? 
* Helpful resource: [Consumers](https://youtu.be/lAdG16KaHLs)
* Helpful resource: [Confluent Consumer Overview](https://youtu.be/Z9g4jMQwog0)

#### How are Kafka offsets different than partitions? 
The offset is the unique number given by kafka to be able to locate the messages on a 
partition. 
#### How is data assigned to a specific partition in Kafka? 
A partition key which is defined when setting everything up. If no key is specified it goes 
to a random partition.
#### Describe immutability - Is data on a Kafka topic immutable? 
Yes- it is an append only log
#### How is data replicated across brokers in kafka? If you have a replication factor of 3 and 3 brokers, explain how data is spread across brokers
* Helpful resource [Brokers and Replication factors](https://youtu.be/ZOU7PJWZU9w)
You have one leader (one instance of the message stored on a broker(server), and 2 copies stored 
on the other two brokers, respectively).
#### What was the most fascinating aspect of Kafka to you while learning? 
The append only log. 