echo "Setup HDFS"
hdfs namenode -format
$HADOOP_HOME/sbin/start-dfs.sh

echo "Create output directories"
# directories where input data will be added
hdfs dfs -mkdir /flume
hdfs dfs -mkdir /flume/query1_input_data
hdfs dfs -mkdir /flume/query2_input_data
# directories where output data will be added
hdfs dfs -mkdir /query1
hdfs dfs -mkdir /query2

hdfs dfs -chown -R vi:vi /query1
hdfs dfs -chown -R vi:vi /query2
