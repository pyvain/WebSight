<?php

/**
 * SearchEngine.php
 *
 * Interface used to describe specific search engines in a consitant way
 *
 * @author     Vincent LEVALLOIS
 * @version    0.1
 */

interface SearchEngine {

  /**
	  * Returns an array of URLs concerning a query
	  *
	  * @param String keyword that must be looked for with the search engine
	  * @param int number of results
	  * @return String array of ($depth) URLs
	  */
  public function getResults($query, $depth);
}
