<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Auth extends MY_Controller {

	/**
	  * @todo
	  */
	public function index()
	{
		show_404();
	}

	/** @todo For the moment, tests the authentication
	  *
	  *
	  */
	public function test()
	{
		if($this->_isAuthenticated() === false) { return; }
		$this->output
		->set_status_header('200') // ok
		->set_content_type('application/json')
		->set_output(json_encode(array(
			'error' => 'You are logged in'
		)));
	}

	/**
	  * Signs up the user corresponding to email and password given
	  * (if it doesn't already exist)
	  *
	  * @api
	  * @uses _send_validation_email()
	  * @see model->user
	  * @see helper->email
	  *
	  */
	public function signup()
	{
		$email = $this->input->post('email', true); // true activates xss filtering
		$password = $this->input->post('password', true);

		$this->load->model('user');
		$this->load->helper('email');

		if(!$email || !$password) {
			// Bad request (missing arguments)
			$this->output
				->set_status_header('400')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Missing arguments')));
		} elseif(!valid_email($email)) {
			// Bad request (invalid email)
			$this->output
				->set_status_header('400')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Invalid email')));
		} elseif($this->user->userExists($email)) {
			// Bad request (email already used)
			$this->output
				->set_status_header('400')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Address is already used')));
		} elseif(strlen($password) < 8
				 || !preg_match('#\d+#', $password)
				 || !preg_match('#[a-z]+#', $password)
				 || !preg_match('#[A-Z]+#', $password)
				 || !preg_match('#\W+#', $password)) {
			// Bad request (Invalid password)
			$this->output
				->set_status_header('400')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Invalid password')));
		} elseif(!$this->user->addUser($email, $password)) {
			// Internal Server Error (Could not create account)
			$this->output
				->set_status_header('500')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Could not create account')));
		} elseif(!$this->_send_validation_email($email)) {
			$id = $this->user->getUserByEmail($email)->id;
			$this->user->deleteUser($id);
			// Internal Server error (Could not send validation email)
			$this->output
				->set_status_header('500')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Could not send validation email')));
		} else {
			// OK
			$this->output
				->set_status_header('200');
		}
	}

	/**
	  * Signs in the user corresponding to email and password given
	  * and provides him an access token
	  *
	  * @api
	  * @see model->user
	  * @see library->TokenMaster
	  *
	  */
	public function signin()
	{
		$email = $this->input->post('email', true); // true activates xss filtering
		$password = $this->input->post('password', true);

		$this->load->model('user');

		if(!$email || !$password) {
			$this->output
				->set_status_header('400') // Bad request (missing arguments)
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Missing arguments'
				)));
		} elseif(!$this->user->checkPassword($email, $password)) {
			$this->output
				->set_status_header('401') // Unauthorized (invalid credentials)
				->set_header('WWW-Authenticate: Bearer realm="websight"')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Invalid credentials'
				)));
		} else {
			$this->load->library('TokenMaster');
			$userId = $this->user->getUserByEmail($email)->id;
			$jwt = $this->tokenmaster->generateAccessToken($userId);
			$this->output
				->set_status_header('200') // OK
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'jwt' => $jwt
				)));
		}
	}

	/**
	  * Unsubscribes the user by deleting his account and all stored data
	  * (if it doesn't already exist)
	  *
	  * @api
	  * @see model->user
	  *
	  */
	public function deleteaccount()
	{
		if($this->_isAuthenticated() === false) { return; }

		$password = $this->input->post('password', true); // true activates xss filtering
		$userId = $this->_isAuthenticated();

		$this->load->model('user');

		if(!$password) {
			// Bad request (missing arguments)
			$this->output
				->set_status_header('400')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Missing password')));
		} elseif(!$this->user->checkPassword($this->user->getUserById($userId)->email, $password)) {
			// Wrong password
			$this->output
				->set_status_header('401') // Unauthorized
				->set_header('WWW-Authenticate: Bearer realm="websight"')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Invalid credentials')));
		} elseif(!$this->user->deleteUser($userId)) {
			// Internal Server Error (Could not delete account)
			$this->output
				->set_status_header('500')
				->set_content_type('application/json')
				->set_output(json_encode(array(
					'error' => 'Could not delete account')));
		} else {
			// OK
			$this->output
				->set_status_header('200');
		}
	}

	/**
	  * @api
	  * @see model->user
	  * @see views->auth/validation_failure, auth/validation_ok
	  */
	public function validate()
	{
		$email = $this->input->get('email', true); // true activates xss filtering
		$hash = $this->input->get('hash', true);

		$this->load->model('user');

		if(!$email || !$hash || !$this->user->validateAccount($email, $hash)) {
			$this->load->view('auth/validation_failure');
		} else {
			$this->load->view('auth/validation_ok');
		}
	}


	/**
	 * Web page to ask for a link to reset user password
	 */
	public function lostpassword()
	{
		$this->load->library('form_validation');
		$this->load->helper(array('form', 'url'));
		$this->load->model('user');

		$this->form_validation->set_rules('email', 'Email', 'required|valid_email');

        if ($this->form_validation->run())
		{
			$email = $this->input->post('email');
			if($this->user->userExists($email)) {
				$this->_send_reset_email($email);
			}
			$this->load->view('auth/lostpassword_success');
		}
        else
		{
			$this->load->view('auth/lostpassword_form');
		}
	}


	/**
	 * Web page to reset a user's password
	 */
	public function resetpassword()
	{
		$this->load->library('form_validation');
		$this->load->helper(array('form', 'url'));
		$this->load->model('user');

		$email = $this->input->get('email', true);
		$hash = $this->input->get('hash', true);

		$this->form_validation->set_rules('password', 'Password', 'required|min_length[8]|callback__password_check');
		$this->form_validation->set_rules('password2', 'Password confirmation', 'required|matches[password]');
		$password = $this->input->post('password', true);

        if ($email !== false && $hash !== false && $this->form_validation->run() && $this->user->resetPassword($email,$hash,$password)) {
			$this->load->view('auth/resetpassword_success');
		} elseif($email !== false && $hash !== false && $this->user->isValidHash($email,$hash)) {
			$data['email'] = $email;
			$data['hash'] = $hash;
			$this->load->view('auth/resetpassword_form', $data);
		} else {
			echo "Invalid link";
		}
	}

	/**
	 * Callback for password compliance
	 */
	public function _password_check($pwd)
	{
		if(!preg_match('#\d+#', $pwd)) {
			$this->form_validation->set_message('_password_check', 'The {field} field must contain at least a digit.');
			return false;
		} elseif(!preg_match('#[a-z]+#', $pwd)) {
			$this->form_validation->set_message('_password_check', 'The {field} field must contain at least a lowercase letter.');
			return false;
		} elseif(!preg_match('#[A-Z]+#', $pwd)) {
			$this->form_validation->set_message('_password_check', 'The {field} field must contain at least an uppercase letter.');
			return false;
		} elseif(!preg_match('#\W+#', $pwd)) {
			$this->form_validation->set_message('_password_check', 'The {field} field must contain at least a non-alphanumeric symbol.');
			return false;
		} else {
			return true;
		}
	}


	/**
	  * Sends an e-mail to ensure the user owns the mail address
	  *
	  * @see library->email
	  * @see model->user
	  * @see view->auth/validation_email
	  *
	  * @param String $email
	  * the email that will be sent the validation
	  *
	  */
	private function _send_validation_email($email)
	{
		$this->load->library('email');
		$this->load->model('user');
		$link = $this->user->getValidationLink($email);
		if(!$link)
			$link = '*ERROR*';

		$this->email->from('sender email', 'WebSight Personal Data Tracker');
		$this->email->to($email);
		$this->email->subject('WebSight | Confirm your email address');
		$this->email->message($this->load->view('auth/validation_email', array('link'=>$link), true));

		// the mail server is sometime lagging.
		$res = $this->email->send();
		for($i = 0; $i < 3 && !$res; $i++) {
			sleep(1);
			$res = $this->email->send();
		}
		return $res;
	}


	/**
	  * Sends an e-mail with a link to reset the account password
	  *
	  * @see library->email
	  * @see model->user
	  * @see view->auth/reset_email
	  *
	  * @param String $email
	  *
	  */
	private function _send_reset_email($email)
	{
		$this->load->library('email');
		$this->load->model('user');
		$link = $this->user->getResetLink($email);
		if(!$link)
			$link = '*ERROR*';

		$this->email->from('sender email', 'WebSight Personal Data Tracker');
		$this->email->to($email);
		$this->email->subject('WebSight | Reset your password');
		$this->email->message($this->load->view('auth/reset_email', array('link'=>$link), true));

		// the mail server is sometime lagging.
		$res = $this->email->send();
		for($i = 0; $i < 3 && !$res; $i++) {
			sleep(1);
			$res = $this->email->send();
		}
		return $res;
	}
}
