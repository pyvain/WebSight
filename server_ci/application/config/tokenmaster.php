<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/*
 *---------------------------------------------------------------
 * JSON WEB TOKEN KEY
 *---------------------------------------------------------------
 *
 * JWT key created with base64_encode(openssl_random_pseudo_bytes(128))
 * TODO : read key from a non served local file
 */
$config['jwt_key'] = 'yNY3PrpXCJeOCFNuPp8DGG+ciq1MEB7ULbCaampUfVb2V4kY7OHzFG/vS1U+B/wVCvg1Oa6G67deED1FrkSToL/TjKgqag+WlaG89gZGSPFB44lv7OsaVZaqqEEwMtTWUTkIkc4VhmSe+CEMv73i0g+12WU//hFs2tWrnktvpP0=';

/*
 *---------------------------------------------------------------
 * JSON WEB TOKEN ISSUER
 *---------------------------------------------------------------
 *
 * Issuer name or server url
 */
$config['jwt_issuer'] = 'websight.pyvain.fr';
