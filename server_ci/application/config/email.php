<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/*
| -------------------------------------------------------------------
| EMAIL SETTINGS
| -------------------------------------------------------------------
| This file will contain the settings needed to send mails.
|*/

$config['protocol'] = 'smtp';
$config['smtp_host'] = 'smtp.openmailbox.org';
$config['smtp_user'] = 'websight@openmailbox.org';
$config['smtp_pass'] = '4t0$P0rt0$';
$config['smtp_port'] = 587;
$config['smtp_crypto'] = 'tls';
$config['mailtype'] = 'text';
$config['crlf'] = "\r\n";
$config['newline'] = "\r\n";
