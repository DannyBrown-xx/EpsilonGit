# How to develop an Epsilon Model Connectivity Driver

## What is EMC?
EMC stands for Episilon Model Connectivity and is a standard interface for Epsilon and the suite of Epsilon languages to talk to other technologies as if they were models.

![Epsilon Architecture](/Users/danielbrown/Documents/EpsilonGit/Documentation/images/epsilon-architecture.png)

As you can see from the Epsilon Architecture diagram, implementing an EMC layer gives you access to a whole host of Epsilon Technologies, including code generation and validation.

## Setting up your Eclipse Project
The rest of this tutorial assumes you are using the Epsilon Eclipse bundle.

EMC drivers consist of two Eclipse Plug-in projects. The first project contains the business logic of interacting with your chosen technology. The second project contains the user interface code for a configuration dialog that pops up when a user adds a
model to an E*L run configuration.

