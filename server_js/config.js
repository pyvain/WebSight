/**
 * Config file for Websight's server
 */

module.exports = {
    // general configuration of the server
    server: {
        // port on which the server is running
        port: 8080,
        // server adress
        address: 'here.com',
        // indev status. when turned on, additional features are available for developers
        indev: false,
    },
    // mysql database configuration. passed as parameter when connecting to the database
    database: {
        host     : 'localhost',
        user     : 'user',
        password : 'password',
        database : 'database',
    },
    mail: {
        host: 'smtp url',
        port: 465,
        secure: true,
        auth: {
            user: 'mail user',
            pass: 'mail password'
        }
    },
    jwt: {
        secret: 'this is not secret change it',
        issuer: 'issuer'
    },
};
