# Chadash Client
This is an extremely basic client used to call the Chadash server during a deployment. This client wraps the deploy API and the monitor api for server sent events.

To build this client, you need `sbt`. Build the client with the following command

```sbt clean compile assembly```

This will create an uber jar for which you can use to both deploy and monitor your stacks.

To deploy a stack, the syntax looks like this:

```
java -Dchadash.url=${CHADASH_SERVER_URL} -jar chadash-client-assembly-DEV.jar deploy ${CHADASH_USER} ${CHADASH_PW} ${AMI_ID} ${VERSION} ${CHADASH_STACK_PATH}
```

To monitor a stack's deployment, the syntax looks like this:

```
java -Dchadash.url=${CHADASH_SERVER_URL} -jar chadash-client-assembly-DEV.jar monitor ${CHADASH_STACK_PATH} ${VERSION}
```

The monitor command will return a non-zero exit code in the event the stack deploy fails. This makes it easy to report failure within your CD tool's build pipeline.

#### Important Note
Because Chadash is server, and it is the server that is actually doing the deployment - cancelling the chadash-client job inside of your CD tool does absolutely  nothing - it just cancels the monitoring of the stack. 

#### Recommendation
If you use this client instead of rolling your own, we recommend forking this and figuring out a better way to integrate this into your build pipeline. You should not ever rebuild the chadash client, or check it out as source. Ideally, the client is already built and just ready to use inside your pipeline with the correct version number, ami id, etc passed from an upstream build job.


### License

This software is licensed under the Apache 2 license, quoted below.

Copyright (C) 2014-2016 LifeWay Christian Resources. (https://www.lifeway.com).

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
