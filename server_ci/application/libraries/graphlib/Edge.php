<?php
if (!defined('BASEPATH')) {
    exit('No direct script access allowed');
}

/**
 * Edge.php
 *
 * Data structure class which represents an edge of a user's
 * personal data graph
 *
 * @author     Victor SAINT GUILHEM
 * @version    0.0
 */

class Edge {

    /**
      * @var String $src
      * The source of the Edge
      */
    public $src;

    /**
      * @var String $dst
      * The destination of the Edge
      */
    public $dst;


    /**
      * @var String $url
      * The url forming the Edge
      */
    public $url;

    /**
      * Constructor
      *
      * @param String $src
      * @param String $dst
      * @param String $url
      * @return Edge
      */
    public function __construct($src, $dst, $url) {
        $this->src = $src;
        $this->dst = $dst;
        $this->url = $url;
    }
}

/* End of class Edge.class.php */
/* Location: ./application/Edge.php */
