<?php 
/**
 * testGraph.php
 *
 * testing script for class Graph
 *
 * @author     Etienne THIERY
 * @version    0.0
 */

define('BASEPATH', '../server/');
require_once(BASEPATH.'application/Graph.class.php');

$g = new Graph();
echo $g->json_encode().'
';

$g->setVertices(array('v0', 'v1', 'v2', 'v3'));
echo $g->json_encode().'
';

$edge1 = new Edge('v3', array('url1', 'url2'));
$edge2 = new Edge('v2', array('url3', 'url4', 'url5'));
$edge3 = new Edge('v0', array('url6'));
$g->update('v0', array($edge1, $edge2));
$g->update('v3', array($edge3));
echo $g->json_encode().'
';

$g->update('v0', array($edge2));
echo $g->json_encode().'
';

$g->setVertices(array('v0', 'v1', 'v2'));
echo $g->json_encode().'
';