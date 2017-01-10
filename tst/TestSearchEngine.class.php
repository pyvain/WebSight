<?php
class SearchEngine {

  private static $_instance;
  private static $lastSearchTime;

  private static $api_key = "lNcFtLLuA@kn@wRbvCXjulWBQ4M_";

  private function __construct() {
    self::$lastSearchTime = time();
  }

  public static function getInstance() {
    if (is_null(self::$_instance)) {
      self::$_instance = new SearchEngine();
    }
    return self::$_instance;
  }

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

  private function xmlParseForUrl($xml) {
    $firstSplit = explode("<url>", $xml);
    //At this point every value of the array begins with an url, except for the first one
    $retTable = array();
    for ($i = 1 ; $i < count($firstSplit) ; $i++) {
      $retTable[] = explode("</url>", $firstSplit[$i])[0];
    }
    return $retTable;
  }

  public function getResults($query, $depth) {
    $ret = array();
    for ($i = $depth; $i > 0; $i -= 10) {
      $farooUrl = 'http://www.faroo.com/api?q=' . urlencode($query) .
                  '&start=' . (1 + $depth - $i) .
                  '&length=' . (($i >= 10) ? 10 : $i) .
                  '&l=en&src=web&f=xml&key=' . (self::$api_key);
      while (time() <= self::$lastSearchTime){
        sleep(1);
      }
      $lastSearchTime = time();
      $xml = self::performSearch($farooUrl);
      $ret = array_merge($ret, self::xmlParseForUrl($xml));
    }
    return $ret;
  }
}

$instance = SearchEngine::getInstance();

$table = $instance->getResults('Bernie Sanders', 69);

$i = 0;

foreach ($table as $value) {
  echo ++$i . "-\t" . $value . "\n";
}
?>
