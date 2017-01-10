<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * TokenMaster.php
 *
 * Helper class for (JSON Web) token generation and validation
 *
 * @author     Etienne THIERY
 * @version    0.0
 */

class TokenMaster {
	/**
	  * @var String JSON Web Token encryption key
	  */
    private $jwtKey;

	/**
	  * @var issuer Server name
	  */
	private $issuer;

	/**
	  * Constructor
      *
      * @param void
      * @return TokenMaster
		*/
	public function __construct() {
		$CI =& get_instance();
		$CI->config->load('tokenmaster');
		$this->jwtKey = base64_decode($CI->config->item('jwt_key'));
		$this->issuer = $CI->config->item('jwt_issuer');
	}

	/**
	  * Access Token generator
	  * @param int $userId
	  * @return String|bool Access token for user $userId if success,
	  * false else
	  */
	public function generateAccessToken($userId) {
		// Token id is not used fo the moment, but could be used
		// to invalidate tokens
		$tokenId = base64_encode(openssl_random_pseudo_bytes(32));
		$payload = array(
			'iat' => time(),
			'jti' => $tokenId,
			'iss' => $this->issuer,
			'data' => array('userId' => $userId)
			);
		require_once(__DIR__ . '/php-jwt/src/JWT.php');
		$jwt = Firebase\JWT\JWT::encode($payload, $this->jwtKey, 'HS512');
		return $jwt;
	}

	/**
	  * Access Token verifier
	  * @param String $jwt JSON Web Token
	  * @return int|bool Matching userId if success, false else
	  */
      public function verifyAccessToken($jwt) {
          require_once(__DIR__ . '/php-jwt/src/JWT.php');
          try {
              $payload = (array) Firebase\JWT\JWT::decode(
              $jwt,
              $this->jwtKey,
              array('HS512')
          );
          $payload['data'] = (array) $payload['data'];
      } catch (Exception $e) {
          return false;
      }
      if (isset($payload['data']['userId'])) {
          return $payload['data']['userId'];
      } else {
          return false;
      }
  }

	/**
	  * HTTP Header Access Token extracter
	  * Fetch the JWT access token contained in the 'Authorization' field
	  * of the current request HTTP header, then return it.
	  *
	  * @param
	  * @return String|bool JWT access token if success, false else
	  */
	public function getHeaderToken($headers) {
		if (isset($headers['Authorization'])) {
    		$matches = array();
            preg_match('#^(.*)$#', $headers['Authorization'], $matches);
    		if (isset($matches[1])) {
      			return $matches[1];
      		} else {
      			return false;
      		}
    	}
        return false;
  	}

}

/* End of class TokenMaster.php */
/* Location: ./application/libraries/TokenMaster.php */
