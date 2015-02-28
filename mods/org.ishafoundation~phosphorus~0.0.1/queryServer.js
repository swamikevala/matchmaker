var vertx = require('vertx');
var console = require('vertx/console');
var container = require('vertx/container');
var eb = vertx.eventBus;

container.deployModule("org.ishafoundation~phosphorus.fdb~0.0.1");

vertx.createHttpServer().requestHandler(function(req) {

	var body = new vertx.Buffer();
	req.dataHandler(function(buffer) {
		body.appendBuffer(buffer);
	});
  
	req.endHandler(function() {
		var key = makeKey(body.getString(0, body.length()));
		eb.send('me.index_query', key, function(returnedIds) {
			req.response.end(returnedIds);
		});
	});
  
}).listen(8080, 'localhost');

var antiVowel = function(s) {
    return s.replace(/[aeiouhy]/gi, '');
}

var sortAlpha = function(s) {
	return s.split('').sort().join('');
}

var makeKey = function(s) {
	return sortAlpha(antiVowel(s));
}
