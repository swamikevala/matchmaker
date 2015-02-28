import fdb
fdb.api_version(300)
db = fdb.open()
del db[:]
