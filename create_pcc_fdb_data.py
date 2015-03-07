#!/usr/bin/python

# Script to create a simple FoundationDB data source (name, mobile, email), taking records from PCC MySQL db
# Before importing to convert latin1 to utf8 encoding see: http://makezine.com/2007/05/08/mysql-database-migration-latin/

import fdb
import fdb.tuple
import MySQLdb
import sys
import re

fdb.api_version(300)
db = fdb.open()

ds = fdb.directory.create_or_open(db, ("data", "contact"))

connection = MySQLdb.connect (host = "localhost", user = "talend", passwd = "talend", db = "ishadb")
cursor = connection.cursor()
cursor.execute("set character_set_client=latin1")
cursor.execute("select Pam_Serial_Number, Calc_Full_Name, Clean_Mobile_Phone, Clean_Email_Id from addresslist_cleaned")
data = cursor.fetchall()

for row in data :
	#Need to explicitly convert to unicode, else bytes will be different (ascii)
	db[ds.pack((unicode(str(row[0]), "utf-8"), unicode("name", "utf-8")))] = str(row[1])
	db[ds.pack((unicode(str(row[0]), "utf-8"), unicode("mobile", "utf-8")))] = str(row[2])
	db[ds.pack((unicode(str(row[0]), "utf-8"), unicode("email", "utf-8")))] = str(row[3])
	print row[0], row[1], row[2], row[3]
	
cursor.close()
connection.close()
sys.exit()


	