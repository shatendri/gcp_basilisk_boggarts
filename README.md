# gcp_basilisk_boggarts
A highly configurable Google Cloud Dataflow pipeline that writes data into a Google Big Query table from Pub/Sub

Compile this Dataflow pipeline into a runnable Jar (pubsub-to-bq.jar). Then call the jar with following parameters:

```bash
java.exe -jar "C:\Jars\pubsub-streaming.jar" 
    --runner=DataflowRunner
    --project='your project Id'
    --tempLocation=='storage bucket with saging folder for dataflow job' 
    --subscription='Topic subscription to read messages from'
    --keyFilePath='GCP service account key location'
    --bqDataSet='BigQuery dataset name'
    --bqTable='BigQuery table name'
```
Parameters formatted view:

- Only provide .json key files for GCP.
- Pipeline can support queues with batched messages.