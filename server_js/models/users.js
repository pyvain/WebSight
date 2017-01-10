// load database connection
var sql = require('./sql.js');
var crypto = require('crypto');


/**
 * Deletes all users from database. For devs only
 */
function clean(cb) {
    var query = 'DELETE FROM users';
    sql.query(query, [], function(err, rows, fields) {
        if(err){ console.log(err); throw err; }
        cb();
    });
}

/**
* Tells if 'email' is already used by a user
*
* @param string 'email'
* @return true if user exists or false otherwise
*/
function userExists(email, callback_true, callback_false)
{
    var query = 'SELECT id FROM users WHERE email = ?';
    sql.query(query, [email], function(err, rows, fields) {
        if(err){ console.log(err); throw err; }
        else if(rows.length > 0)
            typeof callback_true === 'function' && callback_true();
        else
            typeof callback_false === 'function' && callback_false();
    });
}


/**
* Verify if 'password' is correct for account 'email'
* Fail if the account does not exist or the password is not correct
*
* @param string 'email'
* @param string unencrypted 'password'
* @param function to call if password is correct. Takes user ID in argument
* @param function to call if password is incorrect
* @return true if success, false if failure
*/
function checkPassword(email, password, callback_success, callback_failure)
{

    // Check if account exists, and is active
    var query = 'SELECT id, encrypted_password, salt FROM users WHERE email = ? AND active = 1';
    sql.query(query, [email], function(err, rows, fields) {
        if(err){
            console.log(err);
            typeof callback_failure === 'function' && callback_failure();
        }
        if(rows.length == 0) {
            typeof callback_failure === 'function' && callback_failure();
        } else {
            var sha256 = crypto.createHash('sha256');
            var encrypted_password = sha256.update(rows[0].salt + password).digest('hex');
            if(encrypted_password === rows[0].encrypted_password)
                typeof callback_success === 'function' && callback_success(rows[0].id);
            else
                typeof callback_failure === 'function' && callback_failure();
        }
    });
}


/**
* Inserts a new user in database
* (storing a salted encrypted password and base64 encoded salt)
*
* @param string    'email'              The email of the user to add
* @param string    'password'           His unencrypted password
* @param function  'callback_success'   The function to call once it is done.
*                                       Takes a string representing the
*                                       activation hash as parameter.
* @param function  'callback_success'   The function to call if an error occure
* @return bool true if success, false if failure
*/
function addUser(email, password, callback_success, callback_failure)
{
    userExists(email, callback_failure, function() {
        var salt = new Buffer(crypto.randomBytes(64)).toString('base64').substring(0, 63);
        var sha256 = crypto.createHash('sha256');
        var encrypted_password = sha256.update(salt + password).digest('hex');
        var md5 = crypto.createHash('md5');
        var activation_hash = md5.update(crypto.randomBytes(64)).digest('hex');

        var query = 'INSERT INTO users(email, encrypted_password, salt, hash) VALUES (?, ?, ?, ?)';
        sql.query(query, [email, encrypted_password, salt, activation_hash], function(err, result) {
            if(err){ console.log(err); throw err; }
            if(result.affectedRows == 1) {
                typeof callback_success === 'function' && callback_success(activation_hash);
            } else {
                typeof callback_failure === 'function' && callback_failure();
            }
        });
    });
}


/**
 * Removes the entry of 'email' from the database. Then call callback passing
 * the error which occured in argument if there is any
 *
 * @param   string    'email'      the email address of the entry to removeUserByEmail
 * @param   function  'callback    the function to call after the query is done. Takes one
 *                                 argument representing an enventual error.
 */
function removeUserByEmail(email, callback)
{
    var query = 'DELETE FROM users WHERE email = ?';
    sql.query(query, [email], function(err, result) {
        if(err) { callback(err); }
        else if(result.affectedRows == 1)
            typeof callback === 'function' && callback();
        else
            typeof callback === 'function' && callback("Nothing was removed");
    });
}


/**
 * Actives the account corresponding to 'email' and 'hash'. Then call 'callback'
 * and pass an eventual error in argument.
 * @param string   'email'
 * @param string   'hash'
 * @param function 'callback'
 */
function validateAccount(email, hash, callback)
{
    var query = 'UPDATE users SET active = 1 WHERE email = ? AND hash = ? AND active = 0';
    sql.query(query, [email, hash], function(err, result) {
        if(err) { console.log(err); callback(err); }
        else if(result.affectedRows == 1)
            typeof callback === 'function' && callback();
        else
            typeof callback === 'function' && callback("Invalid activation link");
    });
}


/**
 * Selects the user corresponding to 'id' and call callback with the user in arguments
 * @param integer  'id'
 * @param function 'callback' Takes 2 arguments: an error (can be null) and a user
 */
function getUserById(id, callback)
{
    var query = 'SELECT * FROM users WHERE id = ?';
    sql.query(query, [id], function(err, rows, fields) {
        if(err) {console.log(err);}
        else if(rows.length == 0) callback("No such user", null);
        else callback(null, rows[0]);
    });
}

// export functions
module.exports = {
    userExists: userExists,
    checkPassword: checkPassword,
    addUser: addUser,
    removeUserByEmail: removeUserByEmail,
    validateAccount: validateAccount,
    getUserById: getUserById,
    clean: clean,
};
