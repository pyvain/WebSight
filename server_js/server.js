/***************** import modules *****************/
var config = require('./config.js');
var express = require('express');
var bodyParser = require('body-parser');

/***************** setup express *****************/
var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

/***************** define routes *****************/
var auth = require('./controllers/auth.js');
app.post('/auth/signin', auth.signin);
app.post('/auth/signup', auth.signup);
app.get('/auth/validate', auth.validate);
app.post('/auth/deleteaccount', auth.deleteaccount);

// for devs
if(config.server.indev) {
    // cleans the user database
    app.get('/auth/clean', auth.clean);
}

/***************** launch server *****************/
app.listen(config.server.port, function () {
  console.log('Websight\'s server listening on port ' + config.server.port);
});
