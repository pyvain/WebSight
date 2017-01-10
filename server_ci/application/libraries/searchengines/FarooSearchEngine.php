<?php
include_once(__DIR__ . '/SearchEngine.php');

/**
 * FarooSearchEngine.php
 *
 * Class used to make internet searches using Faroo's search engine
 *
 * @author     Vincent LEVALLOIS
 * @version    0.1
 */

class FarooSearchEngine implements SearchEngine {

  /**
    * @var int $lastSearchTime
    * Saves the time of the last search, to make sure the "1 query per second"
    * limitation is respected
    */
  private static $lastSearchTime;

  /**
    * @var String $api_key
    * API key required to use Faroo's API
    */
  private static $api_key = "ask faroo for an api key";

  public function __construct() {
    $lastSearchTime = time();
  }

  /**
    * Send a request to Faroo via an URL and uptade the time of last search
    *
    * @param String complete Faroo URL containing the query
    * @return String xml object containing Faroo search results
    */
  private function performSearch($url) {
    $curl = curl_init($url);
    if (false != $curl) {
      curl_setopt($curl, CURLOPT_RETURNTRANSFER,1);
      $xml = curl_exec($curl);
      return $xml;
    } else {
      echo 'cURL initialisation failed';
    }
    self::$lastSearchTime = time();
  }


  /**
    * Extracts all URLs from an xml String
    *
    * @param String xml containing URLs to be extracted
    * @return array(String) containing URLs
    */
  private function getUrl($xml) {
    $firstSplit = explode("<url>", $xml);
    //At this point every value of the array begins with an url, except for the first one
    $retTable = array();
    for ($i = 1 ; $i < count($firstSplit) ; $i++) {
      $retTable[] = explode("</url>", $firstSplit[$i])[0];
    }
    return $retTable;
  }


  /**
    * Returns an array of URLs concerning a query; waits for the API to be
    * usable before doing so
    *
    * @param String keyword that must be looked for with Faroo search engine
    * @param int number of results
    * @return array(String) of ($depth) URLs
    */
  public function getResults($query, $depth) {
    $ret = array();
    for ($i = $depth; $i > 0; $i -= 10) {
      $url = 'http://www.faroo.com/api?q=' . $query .
                  '&start=' . (1 + $depth - $i) .
                  '&length=' . (($i >= 10) ? 10 : $i) .
                  '&l=en&src=web&f=xml&key=' . self::$api_key;
      while (time() <= self::$lastSearchTime){
        sleep(1);
      }
      $xml = self::performSearch($url);
      $ret = array_merge($ret, self::getUrl($xml));
    }
    return $ret;
  }
}
