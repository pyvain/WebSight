<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class MY_Controller extends CI_Controller {

	/**
	* An array to pass data to a view
	* It is already created, so coders don't have to create it.
	*/
	public $data = array();

	/**
	* Default controller constructor
	*/
	public function __construct()
	{
		parent::__construct();

		date_default_timezone_set('Europe/Paris');

		log_message('debug', "MY_Controller Class Initialized");
	}

	/**
	* Determines if the request contains a valid token
	* If so, returns the user ID, otherwise send a 401 error to client and
	* returns false
	*
	* @see library->TokenMaster
	* @see model->user
	*
	* @return int|bool Matching userId if success, false else
	*/
	protected function _isAuthenticated()
	{
		$this->load->library('TokenMaster');
		$this->load->model('user');

		$headers = $this->input->request_headers(true);
		$token = $this->tokenmaster->getHeaderToken($headers);
		if($token !== false) {
			$userId = $this->tokenmaster->verifyAccessToken($token);
			if($userId !== false && $this->user->getUserById($userId) !== null) {
				return $userId;
			}
		}
		$this->output
		->set_status_header('401') // Unauthorized
		->set_content_type('application/json')
		->set_output(json_encode(array(
			'error' => 'Authentication needed'
		)));
		return false;
	}

}
