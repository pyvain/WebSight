var validator = require("validator");
var nodemailer = require('nodemailer');
var jwt = require('jwt-simple');

var config = require('../config');
var user_model = require('../models/users.js');


/**
 * Signs up the user corresponding to email and password given
 * (if it doesn't already exist)
 *
 * @api
 * @uses _send_validation_email()
 * @see model->user
 * @see library->email
 *
 */
function signup(req, res)
{
    // get POST arguments
    var email    = req.body.email,
        password = req.body.password;

    // for debug
    email = 'sylvaincassiau@live.fr';
    password = 'Coucou1!';

    if(typeof email == 'undefined' || typeof password == 'undefined') {
        res.status(400).json({ error: 'Missing arguments' });
    } else if(!validator.isEmail(email)) {
        res.status(400).json({ error: 'Invalid email' });
    } else {
        user_model.userExists(email,
            // user exists
            function() {
                res.status(400).json({ error: 'Address is already used' });
            },
            // user does'nt exists
            function() {
                if(password.length < 8 ||             // at least length 8
                   password.search(/\d+/) == -1 ||    // at least a digit
                   password.search(/[a-z]+/) == -1 || // at least a lowercase character
                   password.search(/[A-Z]+/) == -1 || // at least an uppercase character
                   password.search(/\W+/) == -1 ) {   // at least a symbol
                    res.status(400).json({ error: 'Invalid password'});
                } else {
                    user_model.addUser(email, password,
                        function(activation_hash){
                            // build activation link
                            var activation_link = config.server.address + '/auth/validate?email=' + email + '&hash=' + activation_hash;

                            // create transporter object to send mail via SMTP
                            var transporter = nodemailer.createTransport(config.mail);

                            // setup e-mail data
                            var mailOptions = {
                                from: '"Websight\'s Team" <'+config.mail.auth.user+'>',
                                to: email,
                                subject: 'WebSight | Confirm your email address',
                                text: "Thanks for signing up!\n\n\
Your WebSight account has been created.\n\
To activate it, please open this link in a web browser:\n\
"+activation_link+"\n\n\
The WebSight Team",
                                html: "Thanks for signing up!<br><br>\
Your WebSight account has been created.<br>\
To activate it, please click on this link:<br>\
"+"<a href=\""+activation_link+"\">"+activation_link+"</a>"+"<br><br>\
The WebSight Team"
                            };

                            // send mail with defined transport object
                            transporter.sendMail(mailOptions, function(err, info){
                                if(err){
                                    user_model.removeUserByEmail(email,
                                        function(err) {
                                            if(err) {
                                                console.log(err);
                                                res.status(500).json({ error: 'Could not send validation email nor remove email from database' });
                                            } else
                                                res.status(500).json({ error: 'Could not send validation email' });
                                        });
                                    return console.log(err);
                                } else {
                                    console.log("Activation link: "+activation_link);
                                    res.status(200).json({ error: 'No error! Account created' });
                                }
                            });
                        },
                        function(){
                            res.status(500).json({ error: 'Could not create account' });
                        });
                }
            });
    }
}


/**
* Signs in the user corresponding to the given email and password
* and provides him an access token
*
* @api
* @see model->user
* @see library->TokenMaster
*/
function signin(req, res) {
    // get POST arguments
    var email    = req.body.email,
        password = req.body.password;

    // for debug
    email = 'sylvaincassiau@live.fr';
    password = 'Coucou1!';

    // log user in
    if(typeof email == 'undefined' || typeof password == 'undefined') {
        res.status(400).json({ error: 'Missing arguments' });
    } else {
        user_model.checkPassword(email, password,
            function(userId) {
                // generate json web token
                var payload = {
                    iss: config.jwt.issuer,
                    iat: Date.now(),
                    jti: 0, // Token ID. Not used for now.
                    data: {userId: userId},
                };
                var token = jwt.encode(payload, config.jwt.secret);
                console.log(token);
                res.status(200).json({ jwt: token });
            },
            function() {
                res.status(401).header('WWW-Authenticate', 'Bearer realm="websight"')
                               .json({ error: 'Invalid credentials' });
            });
    }
}


/**
 * Validate the account of user using 'email' and 'hash'.
 * Dispays an error message if the account is already valid or if the
 * arguments are incorrect
 */
function validate(req, res)
{
    var email = req.query.email,
        hash  = req.query.hash;

    if(typeof email === 'undefined' || typeof hash === 'undefined') {
        res.status(400).json({ error: 'Missing arguments' });
    } else {
        user_model.validateAccount(email, hash, function(err) {
            if(err)
                res.status(200).end('Invalid validation link');
            else
                res.status(200).end('Your account has been validated.');
        });
    }
}

/**
 * Deletes everything concerning the user asking for it
 */
function deleteaccount(req, res)
{
    if(isAuthenticated(req, res) === false) {return;}

    var password = req.body.password;

    password = "Coucou1!";
    if(typeof password === 'undefined') {
        res.status(400).json({ error: 'Missing arguments' });
    } else {
        var userId = isAuthenticated(req, res);
        user_model.getUserById(userId, function(err, user) {
            if(err) {res.status(400).json({ error: "No such user" }); return;}
            console.log(user);
            user_model.checkPassword(user.email, password,
            function() {
                user_model.removeUserByEmail(user.email,
                    function(err) {
                        if(err) {
                            console.log(err);
                            res.status(500).json({ error: 'Could not delete account' });
                        } else {
                            res.status(200).json({ error: 'Account successfuly deleted'})
                        }
                    });
            },
            function() {
                res.status(401).header('WWW-Authenticate', 'Bearer realm="websight"')
                               .json({ error: 'Invalid credentials' });
            });

        });
    }
}

/**
 * Checks if the header of the request 'req' contains a valid jwt token.
 * If so, returns the user ID of the user doing the request.
 * Sets res to an json error answer and returns false if there is no valid
 * jwt token in the headers.
 */
function isAuthenticated(req, res)
{
    req.headers.Authorization = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ3ZWJzaWdodC5weXZhaW4uZnIiLCJpYXQiOjE0NjEzNjIyNjg1MDEsImp0aSI6MCwiZGF0YSI6eyJ1c2VySWQiOjExM319.NH4BSWjKcg_6Ts4cLdvZ-FM8eUZBNnPZUUjmr0Lf54g';
    console.log(req.headers);
    if(typeof req.headers.Authorization !== 'undefined' &&
       req.headers.Authorization.search(/.*/) != -1) { // should follow the format "Bearer TOKEN"
        var token = req.headers.Authorization.split(' ')[0];
        return jwt.decode(token, config.jwt.secret).data.userId;
    } else {
        res.status(401).json({error: "Authentication required"});
        return false;
    }
}


function clean(req, res)
{
    user_model.clean(function() {res.status(200).json({ msg: "users table cleaned" });});
}

// export functions
module.exports = {
    signup: signup,
    signin: signin,
    validate: validate,
    deleteaccount: deleteaccount,
    isAuthenticated: isAuthenticated,
    clean: clean,
};
