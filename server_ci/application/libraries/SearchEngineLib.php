<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * SearchEngineLib.php
 *
 * A custom library to search the web
 *
 * @author     Vincent LEVALLOIS
 * @version    0.0
 */

// includes the library
require_once(__DIR__ . '/searchengines/FarooSearchEngine.php');
require_once(__DIR__ . '/searchengines/QwantSearchEngine.php');


class SearchEngineLib {

	/**
	  * Instance of faroo's search engine
	  * @var FarooSearchEngine
	  */
	public $faroo;

	/**
	 * Instance of qwant's search engine
	 * @var QwantSearchEngine
	 */
	public $qwant;

	/**
	  * Constructor
      *
      */
	public function __construct() {
		$CI =& get_instance();
		$this->faroo = new FarooSearchEngine();
		$this->qwant = new QwantSearchEngine();
		//$CI->config->load('searchengine');
		//$this->my_attribute = $CI->config->item('my_attribute_config');
	}
}

/* End of class SearchEngineLib.php */
/* Location: ./application/libraries/SearchEngineLib.php */
