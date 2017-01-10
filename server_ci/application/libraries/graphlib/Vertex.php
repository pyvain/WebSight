<?php
if (!defined('BASEPATH')) {
    exit('No direct script access allowed');
}

/**
 * Vertex.php
 *
 * Data structure class which represents a vertex of a user's
 * personal data graph
 *
 * @author     Victor SAINT GUILHEM
 * @version    0.0
 */

class Vertex {

    /**
      * @var String $keyword
      * The keyword stored in the vertex
      */
    public $keyword;

    /**
      * @var String Array $urls
      * The urls received by the search engine about $keyword
      */
    public $urls;

    /**
      * Constructor
      *
      * @param String $keyword
      * @param String Array $urls
      * @return Vertex
      */
    public function __construct($keyword) {
        $this->keyword = $keyword;
        $this->urls = array();
    }

    /**
      * Adds an url to the vertex, with no duplication
      *
      * @param String $url
      * @return void
      */
    public function addUrl($url) {
        if (!in_array($url, $this->urls)) {
            $this->urls[] = $url;
        }
    }
}

/* End of class Vertex.class.php */
/* Location: ./application/Edge.php */
