<?php
include_once(__DIR__ . '/SearchEngine.php');

/**
 * QwantSearchEngine.php
 *
 * Class used to make internet searches using Qwant's search engine
 *
 * @author     Vincent LEVALLOIS
 * @version    0.1
 */

class QwantSearchEngine implements SearchEngine {

  public function __construct() {

  }


  /**
    * Send a request to Qwant via an URL
    *
    * @param String complete Qwant URL containing the query
    * @return bool|JSON object containing Qwant search results or false if failure
    */
  private function performSearch($url) {
    $curl = curl_init($url);
    if ($curl !== false) {
      curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
      $json = curl_exec($curl);
      return $json;
    }
    echo 'cURL initialisation failed';
    return false;
  }

  /**
    * Extracts all the URLs from a JSON object
    *
    * @param JSON object containing URLs to be extracted
    * @return String array containing URLs
    */
  private function getUrl($object) {
    return $object["url"];
  }

  /**
	  * Returns an array of URLs concerning a query
	  *
	  * @param String $query   keyword that must be looked for with Qwant search engine
	  * @param int $depth      the number of results, request over 30 will be ignored by Qwant
	  * @return String Array|bool of ($depth) URLs, unless $depth is over 30 or false if failure
	  */
  public function getResults($query, $depth) {
    $ret = array();
    $count = 30; //max depth in qwant API
    foreach (['web', 'social', 'news'] as $data_type) {
        for ($i = $depth; $i > 0; $i -= $count) {
            $url = 'https://api.qwant.com/api/search/' . $data_type . '?'
            . 'count=' . (($i >= $count) ? $count : $i) . '&'
            . 'q=' . urlencode($query) . '&'
            . 'offset=' . ($depth - $i);
            $json = self::performSearch($url);
            if ($json === false) { return false;}
            $ret = array_merge($ret, array_map("self::getUrl", json_decode($json, true)["data"]["result"]["items"]));
        }
    }
    return $ret;
  }
}
