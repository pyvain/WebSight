<?php
if (!defined('BASEPATH')) {
    exit('No direct script access allowed');
}

/**
 * Graph.php
 *
 * Data structure class representing the personal data graph of a user
 *
 * @author     Victor SAINT GUILHEM
 * @version    0.0
 */

 include_once("Edge.php");
 include_once("Vertex.php");

class Graph {
    /**
      * @var array(Vertex) $vertices
      * Each Vertex of this array represents a piece of personal data
      * of the user and the corresponding urls according to the search engine
      */
    private $vertices;

    /**
      * @var array(Edge) $edges
      * Each Edge of this array represets a links between two user's data
      * (see class Edge)
      */
    private $edges;

    /**
      * Constructor
      *
      * @param String[] keywords
      * @return Graph a graph with only vertices
      */
    public function __construct() {
        $this->vertices = array();
        $this->edges = array();
    }

    /**
      * Adds the vertex if it doesn't already exist
      * If it already exists, it just adds the urls, without duplication
      *
      * @param Object Vertex $vertex
      * @return void
      */
    public function addVertex($vertex) {
        $bool = false;
        foreach ($this->vertices as $vertex_id => $v) {
            if($vertex->keyword === $v->keyword) {
                $same_vertex_id = $vertex_id;
                $bool = true;
                break;
            }
        }
        if ($bool) {
            foreach ($vertex->urls as $url) {
                $this->vertices[$same_vertex_id]->addUrl($url);
            }
        } else {
            $this->vertices[] = $vertex;
        }
    }

    /**
      * Adds the edge if it doesn't already exist
      *
      * @param Object Edge $edge
      *
      * @return void
      */
    public function addEdge($edge) {
        if(!in_array($edge, $this->edges)) {
            $this->addVertex(new Vertex($edge->src));
            $this->addVertex(new Vertex($edge->dst));
            $this->edges[] = $edge;
        }
    }

    /**
      * JSON encoder
      * Generates a compact JSON representation for a graph
      * containing N vertices and M edges:
      * {
      *  "vertices" : [
      *                {
      *                 "kw" : "vertex1_kw"
      *                 "url_ids" : ["vertex1_urlId1", ..., "vertex1_urlIdX"]
      *                },
      *                ...
      *               ]
      *  "edges" : [
      *             {
      *              "src" : edeg1_vertexId1,
      *              "dst" : edeg1_vertexId2,
      *              "url_ids" : ["edge1_urlId1", ..., "edge1_urlIdY"]]
      *             },
      *             ...,
      *            ]
      *  "urls" : ["url1", ..., "urlP"],
      * }
      *
      * @param Void
      * @return String
      */
    public function json_encode() {
        $urls = array();
        foreach ($this->vertices as $v) {
            foreach ($v->urls as $url) {
                if (!in_array($url, $urls)) {
                    $urls[] = $url;
                }
            }
        }

        return json_encode(array('vertices' => $this->encodeVertices($urls),
                                 'edges' => $this->encodeEdges($urls),
                                 'urls' => $urls));
    }

    /**
      * Stores the vertices in a nice way so they can be encoded
      * according to the JSON representation
      *
      * @param String Array $urls containing all the urls contained in the graph, with no duplication
      * @return Array $Encoded_V containing the vertices so they can be encoded in the JSON
      */
    public function encodeVertices(&$urls) {
        // $V is the array of  "vertex_kw" => ["vertex_urlId1", ..., "vertex_urlIdX"]
        $V = array();
        foreach ($this->vertices as $v) {
            if (!isset($V[$v->keyword])) {
                $V[$v->keyword] = array();
            }
            foreach ($v->urls as $url) {
                $V[$v->keyword][] = array_search($url, $urls);
            }
        }
        // to encode $V in a nice way
        $Encoded_V = array();
        foreach ($V as $kw => $url_ids) {
            $Encoded_V[] = array('kw' => $kw, 'url_ids' => $url_ids);
        }

        return $Encoded_V;
    }

    /**
      * Stores the edges in a nice way so they can be encoded
      * according to the JSON representation
      *
      * @param String Array $urls containing all the urls contained in the graph, with no duplication
      * @return Array $Encoded_E containing the edges so they can be encoded in the JSON
      */
    public function encodeEdges(&$urls) {
        // $E is the 3D array of triples [vertex_id_1, vertex_id_2, ["url1_id", ..., "urlx_id"]
        $E = array();
        foreach ($this->edges as $edge) {
            $i = array_search($this->getVertexByKeyword($edge->src), $this->vertices);
            $j = array_search($this->getVertexByKeyword($edge->dst), $this->vertices);
            if ($i < $j) {
                $tmp = $i;
                $i = $j;
                $j = $tmp;
            }
            if (!isset($E[$i][$j])) {
                $E[$i][$j] = array();
            }
            $url_id = array_search($edge->url, $urls);
            if (!in_array($edge->url, $E[$i][$j]) && !in_array($url_id, $E[$i][$j])) {
                $E[$i][$j][] = array_search($edge->url, $urls);
            }
        }
        // to encode $E in a nice way
        $Encoded_E = array();
        foreach ($E as $i => $columns) {
            foreach ($columns as $j => $url_ids) {
                $Encoded_E[] = array('src' => $i, 'dst' => $j, 'url_ids' => $url_ids);
            }
        }

        return $Encoded_E;
    }

    /**
      * JSON pure encoder to check what is inside a graph
      * Generates a compact JSON representation for a graph
      *
      * @param void
      * @return String
      */
    public function pure_json_encode() {
        $V = array();
        foreach ($this->vertices as $v) {
            $V[] = array('kw' => $v->keyword, "urls" => $v->urls);
        }

        $E = array();
        foreach ($this->edges as $edge) {
            $E[] = array('src' => $edge->src, 'dst' => $edge->dst, 'url' => $edge->url);
        }

        return json_encode(array('vertices' => $V, 'edges' => $E));
    }

    /**
      * Vertex getter from a keyword
      *
      * @param String $keyword
      * @return Vertex|bool the Vertex whose keyword is $keyword or false if none is founded
      */
    public function getVertexByKeyword($keyword) {
        foreach ($this->vertices as $v) {
            if ($v->keyword == $keyword) {
                return $v;
            }
        }
        return false;
    }

    /**
      * Vertices getter
      *
      * @param void
      * @return array(Vertex)
      */
    public function getVertices() {
        return $this->vertices;
    }

    /**
      * Edges getter
      *
      * @param void
      * @return array(Edge)
      */
    public function getEdges() {
        return $this->edges;
    }

}
