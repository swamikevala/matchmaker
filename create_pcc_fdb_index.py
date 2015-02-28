#!/usr/bin/python

# Script to create a simple FoundationDB blocking index, taking records from PCC MySQL db

import fdb
import fdb.tuple
import MySQLdb
import sys
import re

def anti_vowel(s):
    return re.sub(r'[AEIOUHY]', '', s, flags=re.IGNORECASE)

def sort_alpha(s):
	return ''.join(sorted(s))
    
def makeKey(val):
	return sort_alpha(anti_vowel(val)).replace(" ", "");
	
fdb.api_version(300)
db = fdb.open()

index = fdb.directory.create_or_open(db, ("blocking_idx", "idx_1"))

connection = MySQLdb.connect (host = "localhost", user = "talend", passwd = "talend", db = "ishadb")
cursor = connection.cursor ()
cursor.execute ("select Pam_Serial_Number, Calc_Full_Name from addresslist_cleaned")
data = cursor.fetchall ()
for row in data :
	k = unicode(makeKey(row[1]), "utf-8")
	db[index.pack((k, unicode(str(row[0]), "utf-8")))] = ''
	print row[0], row[1], k

cursor.close ()
connection.close ()
sys.exit()


	