var mysql = require('mysql');
var config = require('../config.js')

/**
* Connects to the database
*/
// TODO: store credentials in file
var db = mysql.createConnection(config.database);
db.connect();

module.exports = db;
