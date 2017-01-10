<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * GraphLib.php
 *
 * A custom library to manage our graphs
 *
 * @author     Victor SAINT GUILHEM
 * @version    0.0
 */

// includes the library
require_once(__DIR__ . '/graphlib/Graph.php');
require_once(__DIR__ . '/graphlib/Edge.php');
require_once(__DIR__ . '/graphlib/Vertex.php');


class GraphLib {
	/**
	  * Constructor
      *
      */
	public function __construct() {
		$CI =& get_instance();
	}

	/**
	    * Factory for a new graph with only vertices
	    *
	    * @return a new empty Graph object
	    */
	  public function newGraph()
	    {
	    return new Graph();
	  }


	  /**
	    * Factory for a new edge
	    *
	    * @param String $src source vertex
	    * @param String $dst destination vertex
	    * @param String $url url carried
		*
	    * @return a new Edge object
	    */
	  public function newEdge($src, $dst, $url)
	  {
	    return new Edge($src, $dst, $url);
	  }

	  /**
	    * Factory for a new vertex
	    *
	    * @param String $keyword
		*
	    * @return a new Vertex object
	    */
	  public function newVertex($keyword)
	  {
	    return new Vertex($keyword);
	  }

}

/* End of class GraphLib.php */
/* Location: ./application/libraries/GraphLib.php */
