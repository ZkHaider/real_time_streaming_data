CREATE KEYSPACE sparkdata WITH REPLICATION ={'class':'SimpleStrategy','replication_factor':1};

USE sparkdata;

CREATE TABLE cust_data (fname text, lname text, url text, product text, cnt counter, primary key (fname, lname, url, product));
