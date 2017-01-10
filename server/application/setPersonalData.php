<?php
if (!defined('BASEPATH')) {
	exit('No direct script access allowed');
}

/**
 * setPersonalData.php
 *
 * Personal data setting script
 *
 * @author     Etienne THIERY
 * @version    0.0
 */

require_once(AUTHDIR.'tokenCheck.php');

header('Content-type: application/json');
	
if (isset($userId)) {
	if (!isset($_POST['data'])) {
		http_response_code(400); // Bad request (missing arguments)
		echo json_encode(array('error' => 'Missing arguments'));
		
	// Check data is a json encoded array of strings
	} else {
		$valid = true;
		$data = json_decode($_POST['data'])
		if (!is_array($data)) {
			$valid = false;
		} else {
			foreach ($data as $d) {
				if (!is_string($d)) {
					$valid = false;
					break;
				}
			}
		}
		if (!$valid) {
			http_response_code(400); // Bad request (missing arguments)
			echo json_encode(array('error' => 'Invalid arguments'));

		// Set personal data		
		} else {
			include_once(DBDIR.'DbRequester.class.php');
			$db = new DbRequester();
			include_once(APPDIR.'Graph.class.php');
			$graph = new Graph();
			$graph->setVertices($data);
			if(!$db->setGraph($userId, $graph)) {
				http_response_code(500); // Internal error
				echo json_encode(array('error' => 'Error setting personal data'));
			} else {
				http_response_code(200); // OK
				echo json_encode(array());
			}
		}
	}
}

/* End of file setPersonalData.php */
/* Location: ./application/setPersonalData.php */
