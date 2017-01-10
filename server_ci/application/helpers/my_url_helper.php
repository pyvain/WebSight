<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * Custom URL Helper
 *
 * @category	Helpers
 * @author		Victor Saint Guilhem
 */

// ------------------------------------------------------------------------

if ( ! function_exists('url_hostname'))
{
	/**
	 * Extracts the hostname from an url
	 *
	 * @param	String	$url
	 * @return	String The hostname
	 */
	function url_hostname($url)
	{
	    if (parse_url($url, PHP_URL_SCHEME) == '') {
	        $exploded_url = explode('.', $url);
	    } else {
	        $exploded_url = explode('.', parse_url($url, PHP_URL_HOST));
	    }
	    $offset = preg_match('#www.#U', $url)?1:0;
	    return $exploded_url[$offset] . '.' . $exploded_url[$offset + 1];
	}
}
