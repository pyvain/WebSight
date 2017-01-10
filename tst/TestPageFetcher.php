<?php
$dir = dirname(__FILE__);
include_once($dir."/../src/application/PageFetcher.class.php");

$fetcher = PageFetcher::getInstance();
$url = "http://www.enseirb-matmeca.fr/";
print_r($fetcher->fetch($url));
?>
