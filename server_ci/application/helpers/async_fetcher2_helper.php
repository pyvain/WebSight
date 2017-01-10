<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * This file contains a re-implementation of pthreads' Pool using only
 * threads.
 * This is a fix because the pthreads' implementation of Pool has memory leaks
 * which has only been fixed in pthreads for PHP 7.
 */

ini_set('max_execution_time', 300); // 5 min
require_once APPPATH . 'libraries/PageFetcherLib.php';
require_once APPPATH . 'libraries/GraphLib.php';

class FetchJob extends Thread {
    public $url; // the query
    public $start_time; // timestamp when the job started
    public $kw_searched; // the keyword searched which was found in this url
    public $keywords; // the array of all keywords
    public $neighbours; // the list of keywords linked
    private $isDone;
    public $isKilled;

    public function __construct($url, $kw_searched, $keywords) {
        $this->url = $url;
        $this->start_time = false; // not started yet
        $this->kw_searched = $kw_searched;
        $this->keywords = $keywords;
        $this->neighbours = array();
        $this->isDone = false;
        $this->isKilled = false;
    }

    public function isGarbage() { return $this->isDone; }

    public function setGarbage() { $this->isDone = true; }

    public function run() {
        $pagefetcherlib = new PageFetcherLib;
        $this->start_time = time();
        $page = $pagefetcherlib->fetch($this->url);
        if(($page === false) || (stripos($page, $this->kw_searched) === false)) {
            $this->neighbours = false; // invalidate result
        } else {
            foreach ($this->keywords as $kw_neighbour) {
                if(($kw_neighbour !== $this->kw_searched) && (stripos($page, $kw_neighbour) !== false)) {
                    // use array_merge() to not corrupt the neighbours array with $this->neighbours[] = ...
                    $this->neighbours = array_merge($this->neighbours, array($kw_neighbour));
                }
            }
        }
        // free memory
        unset($page);

        $this->setGarbage(); // mark this job as done
    }
}

class FetchPool {

    public $g; // result graph
    public $graphlib;

    private $work; // list of pending jobs
    private $size; // max number of simultaneous threads
    private $used; // current number of simultaneous threads

    public function __construct($size, $class) { // class is not used. Just for prototype compatibility  with async_fetcher_helper.php
        $this->graphlib = new GraphLib;

        // create result
        $this->g = $this->graphlib->newGraph();

        $this->work = array();
        $this->size = $size;
        $this->used = 0;
    }

    private function collect($collect_fct) {
        foreach($this->work as $key => $w) {
            if($this->used < $this->size && !$w->isStarted()) {
                $w->start();
                $this->used++;
            } elseif($collect_fct($w) === true) {
                // result has been collected, can free memory
                if(!$w->isKilled) // only wait for non killed threads
                    $w->join();
                unset($this->work[$key]);
                $this->used--;
            }
        }
    }

    public function submit($job) {
        $this->work[] = $job;
    }

    public function process() {
        // Run this loop as long as we have jobs in the pool
        while (count($this->work) > 0) {
            $this->collect(function (FetchJob $job) {
                // If a job was marked as done, collect its results
                if($job->isGarbage()) {
                    error_log("\rRemaining jobs: ".count($this->work)."        ", 3, "/tmp/php-log.txt");
                    $v = $this->graphlib->newVertex($job->kw_searched);
                    if($job->neighbours !== false) {
                        $v->addUrl($job->url);
                        //error_log(".".var_dump($job->neighbours).".        ", 3, "/tmp/php-log.txt");
                        foreach ($job->neighbours as $kw_neighbour) {
                            $this->g->addEdge($this->graphlib->newEdge($job->kw_searched, $kw_neighbour, $job->url));
                        }
                    }
                    $this->g->addVertex($v);
                    return true;
                } else {
                    // check if it is a blocking job, using a timeout
                    if(($job->start_time !== false) && ((time() - $job->start_time) > 20)) {
                        $job->kill(); // kill thread because it's too long
                        $job->isKilled = true; // mark it as killed
                        $job->setGarbage(); // collect it on next loop
                    }
                    return false;
                }
            });
        }
        // All jobs are done

        return $this->g;
    }
}
