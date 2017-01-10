<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Request extends MY_Controller {

	/**
	  * @todo
	  */
	public function index()
	{
		echo "request";
	}

	/**
	 * A page to test graph generation without needing authentication
	 */
	public function testgraph()
	{
		$algo = $this->input->get('threads', true);

		if($algo === false) {
			$graph = $this->_build_graph(array("some_keyword","another_keyword","one_last_keyword"));
		} else {
			$graph = $this->_build_graph_threads(array("some_keyword","another_keyword","one_last_keyword"));
		}
		if ($graph === false) {
			// Service Unavailable
			$this->output
				->set_status_header('503')
				->set_content_type('application/json')
				->set_output(json_encode(array("error" => "Service Unavailable")));
		}
		$this->output
			->set_status_header('200') // OK
			->set_content_type('application/json')
			->set_output($graph->json_encode());
	}


	/**
	  * Sends to the user the graph corresponding to the given keywords
	  * and send it into a JSON form
	  *
	  * @api
	  * @uses _isAuthenticated()
	  *
	  */
	public function graph()
	{
		if($this->_isAuthenticated() === false) { return; }

		$keywords = (array) $this->input->post('keywords', true); // true activates xss filtering
		$userId = $this->_isAuthenticated();

		if(!$keywords) {
			// Bad request (missing arguments)
			$this->output
				->set_status_header('400')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Missing arguments')));
		}

		// use the right algorithm
		if($this->config->item('graph_algorithm') == BUILD_GRAPH) {
			$graph = $this->_build_graph($keywords);
		} elseif($this->config->item('graph_algorithm') == BUILD_GRAPH_THREADS) {
			$graph = $this->_build_graph_threads($keywords);
		} else {
			// Unknown algorithm
			$this->output
				->set_status_header('500')
				->set_content_type('application/json')
				->set_output(json_encode(array("error" => "Unknown algorithm")));
			return;
		}
		// if we don't get any result from search engines
		if ($graph === false) {
			// Service Unavailable
			$this->output
				->set_status_header('503')
				->set_content_type('application/json')
				->set_output(json_encode(array("error" => "Service Unavailable")));
		}
		$this->output
			->set_status_header('200') // OK
			->set_content_type('application/json')
			->set_output($graph->json_encode());
	}

	/**
	  * Sends to the user the advice corresponding to the given url and keywords
	  * and send it as a webview
	  *
	  * @api
	  * @uses _isAuthenticated()
	  * @see models->User, Advice
	  * @see helper->my_url
	  *
	  */
	public function advice()
	{
		if($this->_isAuthenticated() === false) { return; }

		$keywords = (array) $this->input->post('keywords', true); // true activates xss filtering
		$url = $this->input->post('url', true);
		$userId = $this->_isAuthenticated();

		if(!$keywords || !$url) {
			// Bad request (missing arguments)
			$this->output
				->set_status_header('400')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Missing arguments')));
			return;
		}
		$this->load->model('advice');
		$this->load->model('user');
		$this->load->helper('my_url');
		$hostname = url_hostname($url);

		// to set the head of the webview
		$this->load->view('advice/head');
		$data['keywords'] = $keywords;
		$data['hostname'] = $hostname;
		$view = (count($keywords) == 1)?
		$this->load->view('advice/single', $data):
		$this->load->view('advice/double', $data);

		// to display the origin of the data
		$data['src_url'] = $url;
		$this->load->view('advice/source', $data);

		$data['contact_email'] = null;
		if (!$this->advice->adviceExists($hostname)) {
			$this->load->view('advice/default', $data);
		} else { // to display all custom advice
			$advice = $this->advice->getAdviceByHostname($hostname);
			if($advice->contact_email != '') {$data['contact_email'] = $advice->contact_email;}
			if($advice->account_url != '') {$data['account_url'] = $advice->account_url;}
			if($advice->delete_url != '') {$data['delete_url'] = $advice->delete_url;}
			if($advice->report_url != '') {$data['report_url'] = $advice->report_url;}

			/*
			 * todo: fetch the page to locate the data in it
			 */

			$this->load->view('advice/account', $data);
			$this->load->view('advice/report', $data);
		}
		// The bottom of every advice
		$this->load->view('advice/delete', $data);
		$this->load->view('advice/rights');
	}

	/**
	  * Sends an e-mail with model correspondences
	  *
	  * @see library->email
	  * @see view->advice/model_email
	  * @see model->use
	  *
	  */
	public function emailmodels()
	{
		$this->load->library('email');
		$this->load->model('user');
		$id = $this->_isAuthenticated();
		if($id === false) { return; }
		$email = $this->user->getUserById($id)->email;

		$this->email->from('sender email', 'WebSight Personal Data Tracker');
		$this->email->to($email);
		$this->email->subject('WebSight | Model Correspondences');
		$this->email->message($this->load->view('advice/model_email'), array(), true);

		// the mail server is sometime lagging.
		$res = $this->email->send();
		for($i = 0; $i < 3 && !$res; $i++) {
			sleep(1);
			$res = $this->email->send();
		}
		if(!$res) {
			// Service Unavailable
			$this->output
				->set_status_header('503')
				->set_content_type('application/json')
				->set_output(json_encode(array("error" => "Service Unavailable")));
		}
		$this->output
			->set_status_header('200') // OK
			->set_content_type('application/json')
			->set_output(json_encode(array("error" => "The mail has been sent")));

	}

	/**
	  * Web Page to add a new advice to the database
	  *
	  * @api
	  * @see models->User, Advice
	  * @see libraries->form_validation
	  * @see helpers->my_url, form, url
	  * @see views->advice/collaborate_success, advice/collaborate_form, errors/html/error_400
	  *
	  */
	public function collaborate() {
		$this->load->helper(array('form', 'url', 'my_url'));
		$this->load->library('form_validation');
		$this->load->model('advice');
		$hostname = $this->input->get('hostname', true);
		if (!$hostname) {
			$data['heading'] = "Error 400: Missing arguments";
			$data['message'] = "You need to provide a valid hostname to collaborate.";
			$this->load->view('errors/html/error_400', $data);
			return;
		}
		$hostname = url_hostname($hostname);

		$this->form_validation->set_rules('account_url', 'Sign in page URL', 'valid_url');
		$this->form_validation->set_rules('delete_url', 'URL to delete an account', 'valid_url');
		$this->form_validation->set_rules('report_url', 'URL to report a content', 'valid_url');
		$this->form_validation->set_rules('contact_email', 'Site\'s owner\'s email', 'valid_email');
		$this->form_validation->set_rules('global_help', 'Global advice for this website');

		if ($this->form_validation->run() && !$this->advice->adviceExists($hostname)) {
			$this->advice->addAdvice($hostname);
			$form_content = array('AccountUrl' => $this->input->post('account_url', true),
								  'DeleteUrl' => $this->input->post('delete_url', true),
								  'ReportUrl' => $this->input->post('report_url', true),
								  'ContactEmail' => $this->input->post('contact_email', true),
							  	  'GlobalHelp' => $this->input->post('global_help', true));
			foreach ($form_content as $key => $value) {
				if($value !== null) {
					$function = 'add' . $key;
					$this->advice->$function($value, $hostname);
				}
			}
			$this->load->view('advice/collaborate_success');
		}
		else if ($this->advice->adviceExists($hostname)) {
			$this->load->view('advice/collaborate_failure');
		} else {
			$data['hostname'] = $hostname;
			$this->load->view('advice/collaborate_form', $data);
		}
	}

	/**
	  * Web Page to moderate added advice (to edit, turn the into active advice or delete them)
	  *
	  * @api
	  * @see models->User, Advice
	  * @see libraries->form_validation
	  * @see helpers->my_url, form, url
	  * @see views->advice/collaborate_success, advice/collaborate_form, errors/html/error_400
	  *
	  */
	public function moderate() {
		$this->load->helper(array('form', 'url', 'my_url'));
		$this->load->model(array('advice'));
		$hostname = $this->input->post('hostname');
		if ($this->input->post('validate') === "") {
			$this->advice->validateAdvice($hostname);
		} else if($this->input->post('delete') === "") {
			$this->advice->deleteAdvice($hostname);
		}
		$this->load->library('form_validation');

		$advice = $this->advice->getInactiveAdvice();
		if(!$advice) {
			$this->load->view('advice/moderate_success');
		} else {
			$this->form_validation->set_rules('account_url', 'Sign in page URL', 'valid_url');
			$this->form_validation->set_rules('delete_url', 'URL to delete an account', 'valid_url');
			$this->form_validation->set_rules('report_url', 'URL to report a content', 'valid_url');
			$this->form_validation->set_rules('contact_email', 'Site\'s owner\'s email', 'valid_email');
			$this->form_validation->set_rules('global_help', 'Global advice for this website');

			if ($this->form_validation->run()) {
				$form_content = array('AccountUrl' => $this->input->post('account_url', true),
				'DeleteUrl' => $this->input->post('delete_url', true),
				'ReportUrl' => $this->input->post('report_url', true),
				'ContactEmail' => $this->input->post('contact_email', true),
				'GlobalHelp' => $this->input->post('global_help', true));
				foreach ($form_content as $key => $value) {
					if($value !== null) {
						$function = 'add' . $key;
						$this->advice->$function($value, $hostname);
					}
				}
				redirect(base_url() . 'request/moderate');
			} else {
				$data['advice'] = $advice;
				$this->load->view('advice/moderate_form', $data);
			}
		}
	}

	/**
	  * Web Page to administrate advice (to edit or delete them)
	  *
	  * @api
	  * @see models->User, Advice
	  * @see libraries->form_validation
	  * @see helpers->my_url, form, url
	  * @see views->advice/collaborate_success, advice/collaborate_form, errors/html/error_400
	  *
	  */
	public function administrate() {
		$this->load->helper(array('form', 'url', 'my_url'));
		$this->load->model(array('advice'));
		$hostname = $this->input->post('hostname');
		if ($this->input->post('edit') === "") {
			$form_content = array('AccountUrl' => $this->input->post('account_url', true),
								  'DeleteUrl' => $this->input->post('delete_url', true),
								  'ReportUrl' => $this->input->post('report_url', true),
								  'ContactEmail' => $this->input->post('contact_email', true),
								  'GlobalHelp' => $this->input->post('global_help', true));
			foreach ($form_content as $key => $value) {
				if($value !== null) {
					$function = 'add' . $key;
					$this->advice->$function($value, $hostname);
				}
			}
		} else if($this->input->post('delete') === "") {
			$this->advice->deleteAdvice($hostname);
		}
		$this->load->library('form_validation');

		$advice = $this->advice->getActiveAdvice();
		if(!$advice) {
			$this->load->view('advice/administrate_success');
		} else {
			$this->form_validation->set_rules('account_url', 'Sign in page URL', 'valid_url');
			$this->form_validation->set_rules('delete_url', 'URL to delete an account', 'valid_url');
			$this->form_validation->set_rules('report_url', 'URL to report a content', 'valid_url');
			$this->form_validation->set_rules('contact_email', 'Site\'s owner\'s email', 'valid_email');
			$this->form_validation->set_rules('global_help', 'Global advice for this website');

			if ($this->form_validation->run()) {
				$form_content = array('AccountUrl' => $this->input->post('account_url', true),
				'DeleteUrl' => $this->input->post('delete_url', true),
				'ReportUrl' => $this->input->post('report_url', true),
				'ContactEmail' => $this->input->post('contact_email', true),
				'GlobalHelp' => $this->input->post('global_help', true));
				foreach ($form_content as $key => $value) {
					if($value !== null) {
						$function = 'add' . $key;
						$this->advice->$function($value, $hostname);
					}
				}
				redirect(base_url() . 'request/administrate');
			} else {
				$data['advice'] = $advice;
				$this->load->view('advice/administrate_form', $data);
			}
		}
	}

	/**
	  * Builds the graph fetching the web based on keywords
	  *
	  * @see libraries->GraphLib, SearchEngineLib, PageFetcherLib
	  *
	  * @param String[] $keywords
	  * the private data the user wants to search for
	  *
	  * @return Graph|bool The user's graph or false if failure
	  */
	private function _build_graph($keywords)
	{
		$this->load->library(array('SearchEngineLib', 'GraphLib', 'PageFetcherLib'));

		$g = $this->graphlib->newGraph();
		$se = $this->searchenginelib->qwant;
		foreach ($keywords as $kw_searched) {
			$g->addVertex($this->graphlib->newVertex($kw_searched));
			$results = $se->getResults($kw_searched, 2); // string array of urls
			if ($results === false) {return false;}
			$v = $this->graphlib->newVertex($kw_searched);
			foreach ($results as $url) {
				$page = $this->pagefetcherlib->fetch($url);
				if (false === $page || stripos($page, $kw_searched) === false) {
					continue;
				}
				$v->addUrl($url);
				$g->addVertex($v);
				foreach ($keywords as $kw_neighbour) {
					if ($kw_neighbour !== $kw_searched && stripos($page, $kw_neighbour) !== false) {
						$g->addEdge($this->graphlib->newEdge($kw_searched, $kw_neighbour, $url));
					}
				}
			}
		}
		return $g;
	}


	/**
	  * Builds the graph fetching the web based on keywords
	  * Uses multiple threads.
	  * This function is particularly CPU intensive
	  *
	  * @see libraries->GraphLib, SearchEngineLib, PageFetcherLib
	  *
	  * @param String[] $keywords
	  * the private data the user wants to search for
	  *
	  * @return Graph
	  * the user's graph
	  */
	private function _build_graph_threads($keywords)
	{
		$this->load->library('SearchEngineLib');
		$this->load->helper('async_fetcher2');

		$se = $this->searchenginelib->qwant;
		$pool = new FetchPool(30, Worker::class);

		// launch page fetching in separated threads
		error_log("Filling pool...\n", 3, "/tmp/php-log.txt");
		foreach ($keywords as $kw_searched) {
			$url_list = $se->getResults($kw_searched, 30);
			if($url_list === false) {return false;} // error in qwant API
			foreach ($url_list as $url) {
				$pool->submit(new FetchJob($url, $kw_searched, $keywords));
			}
		}

		error_log("Pool filled.\n", 3, "/tmp/php-log.txt");
		// build and get graph
		$g = $pool->process();
		error_log("\rGraph done                     \n", 3, "/tmp/php-log.txt");

		return $g;
	}

}
