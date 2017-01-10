Here is an installation guide of WebSight's server, using Apache 2 with PHP 5.5 using pthreads module.
This guide is made for a fresh Debian 8 server.

The support of threads in PHP is not natively included. In order to use them, you need to recompile PHP with Zend Thread Safe option.

# install apache2 and activate some mods
`sudo apt-get install apache2`

`sudo a2enmod rewrite`

# Allow the use of .htaccess files
Edit `/etc/apache2/apache2.conf`, and set `AllowOverride` to `all` for the directory you plan to put WebSight's server in (usually `/var/www`).

# Required libraries
`sudo apt-get install gcc make libzzip-dev libreadline-dev libxml2-dev libssl-dev libmcrypt-dev libcurl4-openssl-dev apache2-dev autoconf pkg-config`

# Download and extract PHP version 5.5 source
`wget http://www.php.net/distributions/php-5.5.33.tar.gz`

`tar zxvf php-5.5.33.tar.gz`

# Configure
`cd php-5.5.33`

`./configure --prefix=/usr --with-config-file-path=/etc --enable-maintainer-zts --enable-pthreads --with-curl --with-apxs2 --with-mysqli --with-openssl --with-mysql-sock=/var/run/mysqld/mysqld.sock`

# Correct an error in the Makefile
In file `Makefile`, line 82 in `EXTRA_LIBS`: change last "-lcrypt" to "-lcrypto"

# Build
`make -j3`

# Install
`sudo make install`

# Copy configuration
`sudo cp php.ini-development /etc/php.ini`

# Set default timezone in `/etc/php.ini`
`sudo sed -i "s/^;date.timezone =$/date.timezone = \"Europe\/Paris\"/" /etc/php.ini`

# Download and extract pthreads 2 source for php5
`cd ..`

`wget http://pecl.php.net/get/pthreads-2.0.10.tgz`

`tar zxvf pthreads-2.0.10.tgz`

# Configure pthread and build it
`cd pthreads-2.0.10`

`phpize`

`./configure`

`make -j3`

`sudo make install`

# Add pthreads to php.ini
`sudo sh -c "echo \"extension=pthreads.so\" >> /etc/php.ini"`

# edit Apache php conf
Create file `/etc/apache2/mods-available/php5.conf`
```
<FilesMatch ".+\.ph(p[345]?|t|tml)$">
    SetHandler application/x-httpd-php
</FilesMatch>
<FilesMatch ".+\.phps$">
    SetHandler application/x-httpd-php-source
    # Deny access to raw php sources by default
    # To re-enable it's recommended to enable access to the files
    # only in specific virtual host or directory
    Order Deny,Allow
    Deny from all
</FilesMatch>
# Deny access to files without filename (e.g. '.php')
<FilesMatch "^\.ph(p[345]?|t|tml|ps)$">
    Order Deny,Allow
    Deny from all
</FilesMatch>

# Running PHP scripts in user directories is disabled by default
#
# To re-enable PHP in user directories comment the following lines
# (from <IfModule ...> to </IfModule>.) Do NOT set it to On as it
# prevents .htaccess files from disabling it.
<IfModule mod_userdir.c>
    <Directory /home/*/public_html>
        php_admin_flag engine Off
    </Directory>
</IfModule>
```

# Refresh apache php mod
`sudo a2dismod php5.load && sudo a2enmod php5`

`sudo service apache2 restart`

# Install WebSight's server
Copy WebSight's server files (this directory) in the directory of your choice. For this example, we will use `/var/www/html` since it is already configured in Apache. If you use another location, you will need to setup an Apache virtual host.

# Install MySQL server
`sudo apt-get install mysql-server`

# Edit default mysql socket in php.ini
`sudo sed -i "s/^mysqli.default_socket =$/mysqli.default_socket = \/var\/run\/mysqld\/mysqld.sock/" /etc/php.ini`

# Check php modules
`php -m | grep pthreads` should display `pthreads`.

Open a php page containing `<?php echo phpinfo(); ?>`. Pthreads should be listed as a module.

# Build tables
You can import `websight.sql` in your database. It will build tables and include some content.

# Setup WebSight's server and database credentials
Edit `application/config/config.php` and set the URL of the server.

Edit `application/config/database.php` with your database credentials.
