# Spark-Cassandra-WordCount
##Spark program to perform  word count on a Cassandra table with 1 field called sentences
Before running this program you must create a Cassandra keyspace with 2 tables:

**1.To create a keyspace:**
```
CREATE KEYSPACE sentencesks
           WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};

USE sentencesks;
```
**2.To create a table that'll hold all the sentences on which wordcount has to be performed**
```
CREATE TABLE sentencesks.sentences (
    sentence text,
    PRIMARY KEY (sentence)
);
```
**3.To create a table that'll hold the results of the word count**
```
CREATE TABLE sentencesks.wordCount (
    word text,
    count int,
    PRIMARY KEY (word)
);
```
**4.To insert sentences into the table called semtence**

```
INSERT INTO sentences (sentence) VALUES ('Enter your sentences here');
```
**5.To view the results of the program i.e. the word Count :**
```
SELECT * FROM wordCount;
```
#NOTE:
Remember to use the right Spark-Cassandra connector based on the versions of both applications configured on your local system.
