<?php
defined('BASEPATH') OR exit('No direct script access allowed');

require_once APPPATH . 'libraries/PageFetcherLib.php';
require_once APPPATH . 'libraries/GraphLib.php';

class FetchJob extends Collectable {
    public $url; // the query
    public $start_time; // timestamp when the job started
    public $kw_searched; // the keyword searched which was found in this url
    public $keywords; // the array of all keywords
    public $neighbours; // the list of keywords linked

    private static $pagefetcherlib;

    public function __construct($url, $kw_searched, $keywords) {
        $this->url = $url;
        $this->start_time = false; // not started yet
        $this->kw_searched = $kw_searched;
        $this->keywords = $keywords;
        $this->neighbours = array();
    }

    public function run() {
        $pagefetcherlib = new PageFetcherLib;
        $this->start_time = time();
        $page = $pagefetcherlib->fetch($this->url);
        if($page === false || stripos($page, $this->kw_searched) === false) {
            $this->neighbours = false; // wrong result
            $this->setGarbage(); // mark this job as done
            return;
        }
        foreach ($this->keywords as $kw_neighbour) {
            if($kw_neighbour !== $this->kw_searched && stripos($page, $kw_neighbour) !== false) {
                $this->neighbours[] = $kw_neighbour;
            }
        }
        // free memory
        unset($page);

        $this->setGarbage();
    }
}

class FetchPool extends Pool {

    public $g; // result graph
    public $graphlib;

    public function process() {
        $this->graphlib = new GraphLib;

        // create result
        $this->g = $this->graphlib->newGraph();

        // Run this loop as long as we have jobs in the pool
        while (count($this->work) > 0) {
            $this->collect(function (FetchJob $job) {
                // If a job was marked as done, collect its results
                if($job->isGarbage()) {
                    error_log(count($this->work)."\n", 3, "/tmp/php-log.txt");
                    $v = $this->graphlib->newVertex($job->kw_searched);
                    if($job->neighbours !== false) {
                        $v->addUrl($job->url);
                        foreach ($job->neighbours as $kw_neighbours) {
                            $this->g->addEdge($this->graphlib->newEdge($job->kw_searched, $kw_neighbour, $job->url));
                        }
                    }
                    $this->g->addVertex($v);
                    return true;
                } else {
                    // check if it is a blocking job, using a timeout
                    if(($job->start_time !== false) && ((time() - $job->start_time) > 30)) {
                        error_log(count($this->work)."\n", 3, "/tmp/php-log.txt");
                        $job->setGarbage(); // collect it on next loop
                    }
                    return false;
                }
            });
        }
        // All jobs are done
        // we can shutdown the pool
        $this->shutdown();

        return $g;
    }
}
