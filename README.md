# Distributed Systems Class Assignments

This repository contains four assignments from a Distributed Systems class, culminating in the
development of a Tinder-like application called Twinder. The app supports swipe events for users and
stores information such as potential matches and the number of likes and dislikes. The Twinder
application is built using the CQRS pattern, making it highly scalable and easy to maintain.

## Repository Structure

- **Assignment
  1** : [Implemented Java servlets to handle HTTP requests](https://github.com/Xiaohan-Qin/twinder-distributed-system/tree/master/assignment1)
- **Assignment
  2** : [Introduced RabbitMQ and consumer](https://github.com/Xiaohan-Qin/twinder-distributed-system/tree/master/assignment2)
- **Assignment
  3** : [Introduced database for persistent data storage](https://github.com/Xiaohan-Qin/twinder-distributed-system/tree/master/assignment3)
- **Assignment
  4** : [Applied the CQRS pattern](https://github.com/Xiaohan-Qin/twinder-distributed-system/tree/master/assignment4)

## Key Components

- Two servers: Command Service (write operations) and Query Service (read operations)
- Two databases: Separate data stores for read and write operations using AWS Aurora DB cluster
- One message broker: RabbitMQ, used as a temporary storage for POST events.
- One consumer: Consumes messages and updates the write data store

## Scalability

Twinder's architecture makes it easy to scale out. The application can efficiently handle increased
loads without compromising performance.
