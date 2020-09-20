flume-ng agent -n q1agent -c /usr/local/flume/apache-flume-1.9.0-bin/conf/ -f /usr/local/flume/apache-flume-1.9.0-bin/conf/flume.conf -Dflume.root.logger=INFO,console -Dflume.log.dir=/tmp -Dflume.log.file=flume-agent.log
flume-ng agent -n q1agent -c /usr/local/flume/apache-flume-1.9.0-bin/conf/ -f /usr/local/flume/apache-flume-1.9.0-bin/conf/flume.conf

flume-ng agent -n q2agent -c /usr/local/flume/apache-flume-1.9.0-bin/conf/ -f /usr/local/flume/apache-flume-1.9.0-bin/conf/flume.conf -Dflume.root.logger=INFO,console -Dflume.log.dir=/tmp -Dflume.log.file=flume-agent.log
flume-ng agent -n q2agent -c /usr/local/flume/apache-flume-1.9.0-bin/conf/ -f /usr/local/flume/apache-flume-1.9.0-bin/conf/flume.conf